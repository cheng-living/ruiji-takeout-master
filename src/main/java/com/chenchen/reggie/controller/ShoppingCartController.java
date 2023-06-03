package com.chenchen.reggie.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chenchen.reggie.common.BaseContext;
import com.chenchen.reggie.common.Result;
import com.chenchen.reggie.entity.ShoppingCart;
import com.chenchen.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 *  套餐管理
 *  Slf4j：一套通用的接口规范，使应用程序可以在运行时绑定到不同的日志系统实现上。
 *  RestController：表明该类是rest风格的控制器
 *  RequestMapping：指定HTTP请求的路径前缀（/shoppingCart）
 * */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    //自动装配
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 将选择的菜品/套餐添加到购物车
     * Transactional：事务处理，保证全部成功或者全部失败
     * PostMapping：指定HTTP请求的路径前缀（/shoppingCart/add）
     * RequestBody：从请求正文中反序列化得到的分类对象。
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    @Transactional
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //1.获取当前使用的用户id
        Long currentId = BaseContext.getCurrentId();
        //2.将本次操作购物车的用户设置为刚才获取的用户id
        shoppingCart.setUserId(currentId);
        //3.获取菜品id
        Long dishId = shoppingCart.getDishId();
        //4.判断菜品id是否为空，如果为空则表示当前添加的是套餐
        //①创建条件构造器
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //②添加条件，当前使用的用户id和数据库中的id一致
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);
        //③判断菜品还是套餐
        if (dishId != null){
            //不为空则是菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //为空则是套餐
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //5.查询当前套餐/菜品在购物车中是否为第一次添加
        //①调用shoppingCartService类中的getOne方法进行查询获取唯一一个值
        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);
        //②判断是否为第一次添加
        if (one != null){
            //不等于空则表示不是第一次添加，在原有的number上加一，并且执行修改
            Integer number = one.getNumber();
            one.setNumber(number+1);
            shoppingCartService.updateById(one);
        }else {
            //等于空表示第一次添加
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
            //第一次添加将创建时间保存
            shoppingCart.setCreateTime(LocalDateTime.now());
        }
        //6.返回结果
        return Result.success(one);
    }

    /**
     * 减少用户点的份数
     * Transactional：事务处理，保证全部成功或者全部失败
     * PostMapping：指定HTTP请求的路径前缀（/shoppingCart/add）
     * RequestBody：从请求正文中反序列化得到的分类对象。
     * @param shoppingCart
     * @return
     */
    @Transactional
    @PostMapping("/sub")
    public Result<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        //1.获取当前使用的用户id
        Long currentId = BaseContext.getCurrentId();
        //2.将本次操作购物车的用户设置为刚才获取的用户id
        shoppingCart.setUserId(currentId);
        //3.获取菜品id
        Long dishId = shoppingCart.getDishId();
        //4.判断菜品id是否为空，如果为空则表示当前减去的是套餐
        //①创建条件构造器
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //②添加条件，当前使用的用户id和数据库中的id一致
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);
        //③判断菜品还是套餐
        if (dishId != null){
            //不为空则是菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //为空则是套餐
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //5.将当前菜品/套餐份数-1
        //①调用shoppingCartService类中的getOne方法进行查询获取唯一一个值
        ShoppingCart one = shoppingCartService.getOne(lambdaQueryWrapper);
        //②获取份数
        Integer number = one.getNumber();
        //③将份数-1
        one.setNumber(number-1);
        //④保存
        shoppingCartService.updateById(one);
        //6.判断当前的份数是否还有，如果为零，将数据库中的信息删除
        lambdaQueryWrapper.eq(ShoppingCart::getNumber,0);
        shoppingCartService.remove(lambdaQueryWrapper);
        //7.返回结果
        return Result.success(one);
    }

    /**
     * 查询购物车方法
     * GetMapping：指定HTTP请求的路径前缀（/shoppingCart/list）
     * @return
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        //1.创建条件构造器
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //2.添加条件，当前使用的用户id
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        //3.添加排序条件
        lambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        //4.执行查询
        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        //5.返回结果
        return Result.success(list);
    }


    /**
     * 清空购物车方法
     * DeleteMapping：指定HTTP请求的路径前缀（/shoppingCart/clean）
     * @return
     */
    @DeleteMapping("/clean")
    public Result<String> clean(){
        //1.创建条件构造器
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //2.添加条件，当前使用的用户id
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        //3.执行删除
        shoppingCartService.remove(lambdaQueryWrapper);
        //4.返回结果
        return Result.success("清空购物车成功！");
    }


}
