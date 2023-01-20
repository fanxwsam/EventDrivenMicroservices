package com.sf.cloud.orders.query;

import com.sf.cloud.orders.core.data.OrderEntity;
import com.sf.cloud.orders.core.data.OrderRepository;
import com.sf.cloud.orders.core.data.OrderSummary;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class OrderQueriesHandler {

    OrderRepository orderRepository;

    public OrderQueriesHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @QueryHandler
    public OrderSummary findOrder(FindOrderQuery findOrderQuery){
        OrderEntity orderEntity = orderRepository.findByOrderId(findOrderQuery.getOrderId());

        return new OrderSummary(orderEntity.getOrderId(), orderEntity.getOrderStatus(), "Query from DB successfully");
    }
}
