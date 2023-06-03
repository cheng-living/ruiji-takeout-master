package com.chenchen.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenchen.reggie.common.Result;
import com.chenchen.reggie.dto.OrdersDto;
import com.chenchen.reggie.entity.Orders;
import com.chenchen.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 *  orders相关的操作
 *  Slf4j：一套通用的接口规范，使应用程序可以在运行时绑定到不同的日志系统实现上。
 *  RestController：表明该类是rest风格的控制器
 *  RequestMapping：指定HTTP请求的路径前缀（/order）
 * */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {
    //自动装配
    @Autowired
    private OrdersService ordersService;

    /**
     * 用户支付方法
     * PostMapping：设置访问路径为/order/submit
     * RequestBody：从请求正文中反序列化得到的员工对象。
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders){
        //1.调用ordersService类中的submit方法进行支付
        ordersService.submit(orders);
        //2.返回结果
        return Result.success("下单成功！");
    }

    /**
     * 查询最新订单/历史订单
     * GetMapping：设置访问路径为/order/userPage
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public Result<Page<OrdersDto>> userPage(int page, int pageSize){
        //1.调用ordersService类中的ordersPage方法进行查询
        Page<OrdersDto> ordersDtoPage = ordersService.ordersPage(page, pageSize);
        //2.返回结果
        return Result.success(ordersDtoPage);
    }

    /**
     * 后台查询订单详细信息方法
     * GetMapping：设置访问路径为/order/page
     * RequestBody：从请求正文中反序列化得到的员工对象。
     * @param map
     * @return
     */
    @GetMapping("/page")
    public Result<Page<OrdersDto>> page(@RequestParam Map<String, String> map){
        //1.调用ordersService类中的ordersDetailPage方法进行查询
        Page<OrdersDto> ordersPage = ordersService.ordersDetailPage(map);
        //2.返回结果
        return Result.success(ordersPage);
    }

    /**
     * 后台修改订单状态方法
     * PutMapping：设置访问路径为/order
     * RequestBody：从请求正文中反序列化得到的员工对象。
     * @param orders
     * @return
     */
    @PutMapping
    public Result<String> userPage(@RequestBody Orders orders){
        //1.直接调用ordersService类中的修改方法即可，前端已经将要修改的id和状态传过来了
        ordersService.updateById(orders);
        //2.返回结果
        return Result.success("订单状态修改成功!");
    }

    /**
     * 再来一单
     * PostMapping：设置访问路径为/order/again
     * RequestBody：从请求正文中反序列化得到的员工对象。
     * @param orders
     * @return
     */
    @PostMapping("/again")
    public Result<String> again(@RequestBody Orders orders){
        //1.调用ordersService类中的again方法进行添加到购物车
        ordersService.again(orders);
        //2.返回结果
        return Result.success("添加到购物车成功！");
    }

}
