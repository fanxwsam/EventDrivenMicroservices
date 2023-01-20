package com.sf.cloud.orders.core.event;

import com.sf.cloud.orders.core.data.OrderStatus;
import lombok.Data;

@Data
public class OrderCreateEvent {
    private String orderId;
    private String userId;
    private String productId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;
}
