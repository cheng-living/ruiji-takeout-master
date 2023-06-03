package com.chenchen.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenchen.reggie.common.BaseContext;
import com.chenchen.reggie.common.CustomException;
import com.chenchen.reggie.dto.OrdersDto;
import com.chenchen.reggie.entity.*;
import com.chenchen.reggie.mapper.OrdersMapper;
import com.chenchen.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Service：用于标记该类是一个Spring Bean。
 * ServiceImpl：继承这个类可以免去编写基本的增删改查等操作
 * OrdersService：可以调用这个接口的常用方法
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>  implements OrdersService {
    //自动装配
    @Autowired
    private OrderDetailService orderDetailService;
    //自动装配
    @Autowired
    private ShoppingCartService shoppingCartService;
    //自动装配
    @Autowired
    private UserService userService;
    //自动装配
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 用户提交方法
     * Transactional：确保食物确保成功或者失败
     * @param orders
     */
    @Transactional
    @Override
    public void submit(Orders orders) {
        //1.获取用户id
        Long userId = BaseContext.getCurrentId();
        //2.获取用户购物车的数据
        //①创建条件构造器
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //②添加条件
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        //③查询
        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        //④判断购物车是否有数据
        if (list == null || list.size() == 0){
            //没有数据直接抛出异常
            throw new CustomException("购物车为空，不能支付！");
        }
        //3.将订单信息插入到订单表
        //①获取用户的数据
        User user = userService.getById(userId);
        //②获取地址
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        //③判断地址是否为空
        if (addressBook == null){
            throw new CustomException("地址信息异常，不能支付！");
        }
        //4.设置订单的明细信息以及计算出订单的总金额
        //①订单id生成方法，mybatisPlus提供的方法
        long id = IdWorker.getId();
        //②单总金额，这个对象可以保证多线程时也不会出现错误
        AtomicInteger amount = new AtomicInteger(0);
        //③遍历购物车获取每一个菜品/套餐的信息
        List<OrderDetail> orderDetails = list.stream().map((item) -> {
            //④设置信息
            OrderDetail orderDetail = new OrderDetail();
            //(1)订单名字
            orderDetail.setName(item.getName());
            //(2)订单id
            orderDetail.setOrderId(id);
            //(4)菜品id
            orderDetail.setDishId(item.getDishId());
            //(5)套餐id
            orderDetail.setSetmealId(item.getSetmealId());
            //(6)口味
            orderDetail.setDishFlavor(item.getDishFlavor());
            //(7)数量
            orderDetail.setNumber(item.getNumber());
            //(8)图片
            orderDetail.setImage(item.getImage());
            //(9)金额
            orderDetail.setAmount(item.getAmount());
            //⑤计算总金额，addAndGet：这个方法可以计算金额，计算公式：当前菜品/套餐金额*当前菜品/套餐份数
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            //⑥将结果返回
            return orderDetail;
        }).collect(Collectors.toList());
        //5.插入订单数据
        //①订单号
        orders.setNumber(String.valueOf(id));
        orders.setId(id);
        //②订单下单时间
        orders.setOrderTime(LocalDateTime.now());
        //③订单支付成功时间
        orders.setCheckoutTime(LocalDateTime.now());
        //④订单状态：1待付款，2待派送，3已派送，4已完成，5已取消
        orders.setStatus(2);
        //④订单实际收款金额
        orders.setAmount(new BigDecimal(amount.get()));
        //⑤订单的用户id
        orders.setUserId(userId);
        //⑥订单的用户名字
        orders.setUserName(user.getName());
        //⑦订单收货人
        orders.setConsignee(addressBook.getConsignee());
        //⑧订单手机号
        orders.setPhone(addressBook.getPhone());
        //⑨订单地址,依次为省市县(区）详细地址，每个都要判断是否为空，如果为空就拼接空字符串
        orders.setAddress(addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName()
                 + addressBook.getCityName() == null ? "" : addressBook.getCityName()
                 + addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName()
                 + addressBook.getDetail() == null ? "" : addressBook.getDetail());
        //⑩保存
        this.save(orders);
        //6.保存订单详细数据
        orderDetailService.saveBatch(orderDetails);
        //7.清空购物车数据
        shoppingCartService.remove(lambdaQueryWrapper);
    }

    /**
     * 查询最新/历史订单
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page<OrdersDto> ordersPage(int page, int pageSize) {
        //1.获取订单信息
        //①创建分页构造器
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        //②创建条件构造器
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //③添加条件，用户id一致
        lambdaQueryWrapper.eq(Orders::getUserId,BaseContext.getCurrentId());
        //④添加排序条件，最新下单的在最上面
        lambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        //⑤进行查询
        this.page(ordersPage,lambdaQueryWrapper);
        //2.获取订单的详细信息
        //①创建分页构造器
        Page<OrdersDto> ordersDtoPage = new Page<>();
        //②将订单信息拷贝到订单详细信息对象中，除去records属性
        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");
        //③通过records属性获取订单的详细信息
        List<OrdersDto> dtoList = ordersPage.getRecords().stream().map((item) -> {
            //(1)创建将当前的每个订单拷贝到一个新的对象中
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            //(2)查询订单的详细信息
            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId, ordersDto.getId());
            List<OrderDetail> list = orderDetailService.list(orderDetailLambdaQueryWrapper);
            //(3)将查询到的信息赋值给ordersDto对象
            ordersDto.setOrderDetails(list);
            //(4)将ordersDto对象返回
            return ordersDto;
        }).collect(Collectors.toList());
        //④设置订单的详细信息
        ordersDtoPage.setRecords(dtoList);
        //3.返回结果
        return ordersDtoPage;
    }

    /**
     * 后台查询订单详细信息
     * @param map
     * @return
     */
    @Override
    public Page<OrdersDto> ordersDetailPage(Map<String, String> map) {
        //1.创建分页构造器
        //①获取当前页数
        Integer page = Integer.valueOf(map.get("page"));
        //②获取每页显示多少条
        Integer pageSize = Integer.valueOf(map.get("pageSize"));
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        //2.创建条件构造器
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //①添加条件，查询的订单号
        if (map.get("number") != null){
            //获取订单号
            Long number = Long.valueOf(map.get("number"));
            //创建条件
            lambdaQueryWrapper.eq(Orders::getNumber,number);
        }
        //②添加条件，选择的时间
        //StringUtils.isNotBlank：判断一个字符串是否为空或只包含空格
        //map.get("beginTime")：开始时间
        //map.get("endTime")：结束时间
        if (StringUtils.isNotBlank(map.get("beginTime")) && StringUtils.isNotBlank(map.get("endTime"))){
            //(1)设置一个时间格式
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            //(2)获取开始时间，并且转为以刚才设置的格式转为LocalDateTime类型的对象
            LocalDateTime beginTime = LocalDateTime.parse(map.get("beginTime"), dateTimeFormatter);
            //(3)获取结束时间，并且转为以刚才设置的格式转为LocalDateTime类型的对象
            LocalDateTime endTime = LocalDateTime.parse(map.get("endTime"), dateTimeFormatter);
            //(4)创建条件，在开始和结束时间中间的
            lambdaQueryWrapper.between(Orders::getOrderTime,beginTime,endTime);
        }
        //③添加排序条件
        lambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        //④调用方法进行查询
        this.page(ordersPage,lambdaQueryWrapper);
        //3.获取订单的详细信息
        //①创建分页构造器
        Page<OrdersDto> ordersDtoPage = new Page<>();
        //②将订单信息拷贝到订单详细信息对象中，除去records属性
        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");
        //③通过records属性获取订单的详细信息
        List<OrdersDto> dtoList = ordersPage.getRecords().stream().map((item) -> {
            //(1)创建将当前的每个订单拷贝到一个新的对象中
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            //(2)查询订单的详细信息
            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId, item.getId());
            List<OrderDetail> list = orderDetailService.list(orderDetailLambdaQueryWrapper);
            //(3)将查询到的信息赋值给ordersDto对象
            ordersDto.setOrderDetails(list);
            //(4)根据地址查询用户地址，名字，并且设置给ordersDto
            Long addressBookId = item.getAddressBookId();
            AddressBook addressBook = addressBookService.getById(addressBookId);
            ordersDto.setAddress(addressBook.getDetail());
            ordersDto.setUserName(addressBook.getConsignee());
            //(5)将ordersDto对象返回
            return ordersDto;
        }).collect(Collectors.toList());
        //④设置订单的详细信息
        ordersDtoPage.setRecords(dtoList);
        //4.将对象返回
        return ordersDtoPage;
    }

    /**
     * 再来一单方法，需要将上一次下单的购物车信息进行回显
     * Transactional：确保食物确保成功或者失败
     * @param orders
     */
    @Transactional
    @Override
    public void again(Orders orders) {
        //1.根据订单id查询订单信息
        LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId,orders.getId());
        List<OrderDetail> list = orderDetailService.list(orderDetailLambdaQueryWrapper);
        //2.根据订单信息获取订单详细信息
        List<ShoppingCart> shoppingCarts = list.stream().map((item) -> {
            //①创建购物车对象
            ShoppingCart shoppingCart = new ShoppingCart();
            //②设置购物车对象的信息
            //(1)本次下单的用户id
            shoppingCart.setUserId(BaseContext.getCurrentId());
            //(2)商品名称
            shoppingCart.setName(item.getName());
            //(3)商品图片
            shoppingCart.setImage(item.getImage());
            //(4)判断本次获取到的订单信息是菜品还是套餐
            if (item.getDishId() == null) {
                //本次为套餐
                shoppingCart.setSetmealId(item.getSetmealId());
            } else {
                //本次为菜品
                shoppingCart.setDishId(item.getDishId());
            }
            //(5)口味
            shoppingCart.setDishFlavor(item.getDishFlavor());
            //(6)数量
            shoppingCart.setNumber(item.getNumber());
            //(7)单价
            shoppingCart.setAmount(item.getAmount());
            //(8)菜品/套餐创建时间
            shoppingCart.setCreateTime(LocalDateTime.now());
            //③返回对象
            return shoppingCart;
        }).collect(Collectors.toList());
        //3.将菜品/套餐全部添加到购物车
        shoppingCartService.saveBatch(shoppingCarts);
    }


}
