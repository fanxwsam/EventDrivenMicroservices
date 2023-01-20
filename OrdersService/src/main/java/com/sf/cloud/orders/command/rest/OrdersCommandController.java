package com.sf.cloud.orders.command.rest;

import com.sf.cloud.orders.command.CreateOrderCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sf.cloud.orders.core.data.OrderSummary;
import com.sf.cloud.orders.query.FindOrderQuery;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrdersCommandController {
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public OrdersCommandController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping
    public OrderSummary createOrder(@RequestBody CreateOrderRestModel createOrderRestModel){
        String orderId = UUID.randomUUID().toString();
        String userId = "27b95829-4f3f-4ddf-8983-151ba010e35b";
        CreateOrderCommand createOrderCommand = CreateOrderCommand
                .builder()
                .orderId(orderId)
                .userId(userId)
                .orderStatus(createOrderRestModel.getOrderStatus())
                .quantity(createOrderRestModel.getQuantity())
                .addressId(createOrderRestModel.getAddressId())
                .productId(createOrderRestModel.getProductId())
                .build();

        SubscriptionQueryResult<OrderSummary, OrderSummary> subscriptionQueryResult = queryGateway.subscriptionQuery(new FindOrderQuery(orderId),
                ResponseTypes.instanceOf(OrderSummary.class),
                ResponseTypes.instanceOf(OrderSummary.class)
                );

        try {
            commandGateway.sendAndWait(createOrderCommand);
            return subscriptionQueryResult.updates().blockFirst();
        } finally {
            subscriptionQueryResult.close();
        }
    }


}
