package com.sf.cloud.orders.core.event;

import com.sf.cloud.orders.core.data.OrderStatus;
import lombok.Value;

@Value
public class OrderApprovedEvent {
    private final String orderId;
    private final OrderStatus orderStatus = OrderStatus.APPROVED;

}
