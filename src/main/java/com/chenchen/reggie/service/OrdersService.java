package com.chenchen.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chenchen.reggie.dto.OrdersDto;
import com.chenchen.reggie.entity.Orders;

import java.util.Map;

/**
 * 这个接口用于定义一些mybatis无法生成的一些增CRUD方法
 * IService：提供了许多通用方法，可以用来获取更多的数据操作。
 */
public interface OrdersService extends IService<Orders> {
    //新增一个方法，用于用户提交支付
    public void submit(Orders orders);
    //新增一个方法，用于查询最新订单/历史订单
    public Page<OrdersDto> ordersPage(int page, int pageSize);
    //新增一个方法，用于后台查询订单详细信息
    public Page<OrdersDto> ordersDetailPage(Map<String, String> map);
    //新增一个方法，用于再来一单方法
    public void again(Orders orders);

}
