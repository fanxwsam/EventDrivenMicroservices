package com.sf.cloud.orders.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import com.sf.cloud.orders.core.data.OrderEntity;
import com.sf.cloud.orders.core.data.OrderRepository;
import com.sf.cloud.orders.core.event.OrderApprovedEvent;
import com.sf.cloud.orders.core.event.OrderCreateEvent;
import com.sf.cloud.orders.core.event.OrderRejectedEvent;

@Component
@ProcessingGroup("order-group")
public class OrderEventHandler {
    private final OrderRepository orderRepository;

    public OrderEventHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @EventHandler
    public void on(OrderCreateEvent orderCreateEvent){
        OrderEntity orderEntity = new OrderEntity();
        BeanUtils.copyProperties(orderCreateEvent, orderEntity);

        orderRepository.save(orderEntity);
    }

    @EventHandler
    public void on(OrderApprovedEvent orderApprovedEvent){
        OrderEntity orderEntity = orderRepository.findByOrderId(orderApprovedEvent.getOrderId());
        if(orderEntity == null){
            // ...
            return;
        }

        orderEntity.setOrderStatus(orderApprovedEvent.getOrderStatus());
        orderRepository.save(orderEntity);
    }

    @EventHandler
    public void on(OrderRejectedEvent orderRejectedEvent){
        OrderEntity orderEntity = orderRepository.findByOrderId(orderRejectedEvent.getOrderId());
        orderEntity.setOrderStatus(orderRejectedEvent.getOrderStatus());
        orderRepository.save(orderEntity);
    }

}
