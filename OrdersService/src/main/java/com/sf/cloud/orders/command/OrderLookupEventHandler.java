package com.sf.cloud.orders.command;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import com.sf.cloud.orders.core.data.OrderLookupEntity;
import com.sf.cloud.orders.core.data.OrderLookupRepository;
import com.sf.cloud.orders.core.event.OrderCreateEvent;

@Component
@ProcessingGroup("order-group")
public class OrderLookupEventHandler {
    private final OrderLookupRepository orderLookupRepository;

    public OrderLookupEventHandler(OrderLookupRepository orderLookupRepository) {
        this.orderLookupRepository = orderLookupRepository;
    }

    @EventHandler
    public void on(OrderCreateEvent orderCreateEvent){
        OrderLookupEntity orderLookupEntity = new OrderLookupEntity( orderCreateEvent.getOrderId());
        orderLookupRepository.save(orderLookupEntity);
    }
}
