package com.sf.cloud.orders.command.rest;

import lombok.Data;
import com.sf.cloud.orders.core.data.OrderStatus;

@Data
public class CreateOrderRestModel {
    private String userId;
    private String productId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;
}
