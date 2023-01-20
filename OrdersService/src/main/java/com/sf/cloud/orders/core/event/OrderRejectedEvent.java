package com.sf.cloud.orders.core.event;

import lombok.Value;
import com.sf.cloud.orders.core.data.OrderStatus;

@Value
public class OrderRejectedEvent {
    private final String orderId;
    private final String reason;
    private final OrderStatus orderStatus = OrderStatus.REJECTED;
}
