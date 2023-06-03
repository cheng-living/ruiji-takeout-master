package com.chenchen.reggie.dto;

import com.chenchen.reggie.entity.OrderDetail;
import com.chenchen.reggie.entity.Orders;
import lombok.Data;
import java.util.List;
/*
    订单详情
 */
@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
