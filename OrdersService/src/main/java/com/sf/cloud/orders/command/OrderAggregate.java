package com.sf.cloud.orders.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;
import com.sf.cloud.orders.core.data.OrderStatus;
import com.sf.cloud.orders.core.event.OrderApprovedEvent;
import com.sf.cloud.orders.core.event.OrderCreateEvent;
import com.sf.cloud.orders.core.event.OrderRejectedEvent;

@Aggregate
public class OrderAggregate {
    @AggregateIdentifier
    private String orderId;
    private String userId;
    private String productId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;


    public OrderAggregate() {
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand) {

        OrderCreateEvent orderCreateEvent = new OrderCreateEvent();
        BeanUtils.copyProperties(createOrderCommand, orderCreateEvent);

        AggregateLifecycle.apply(orderCreateEvent);
    }

    @EventSourcingHandler
    public void on(OrderCreateEvent orderCreateEvent) {
        this.orderId = orderCreateEvent.getOrderId();
        this.userId = orderCreateEvent.getUserId();
        this.productId = orderCreateEvent.getProductId();
        this.quantity = orderCreateEvent.getQuantity();
        this.addressId = orderCreateEvent.getAddressId();
        this.orderStatus = orderCreateEvent.getOrderStatus();
    }

    @CommandHandler
    public void handle(ApproveOrderCommand approveOrderCommand){

        //create and publish the OrderApprovedEvent
        OrderApprovedEvent orderApprovedEvent = new OrderApprovedEvent(approveOrderCommand.getOrderId());


        AggregateLifecycle.apply(orderApprovedEvent);
    }

    @EventSourcingHandler
    public void on(OrderApprovedEvent orderApprovedEvent){
        this.orderStatus = orderApprovedEvent.getOrderStatus();
    }

    @CommandHandler
    public void handle(RejectOrderCommand rejectOrderCommand){
        OrderRejectedEvent orderRejectedEvent = new OrderRejectedEvent(rejectOrderCommand.getOrderId(), rejectOrderCommand.getReason());

        AggregateLifecycle.apply(orderRejectedEvent);
    }

    @EventSourcingHandler
    public void on(OrderRejectedEvent orderRejectedEvent){
        this.orderStatus = orderRejectedEvent.getOrderStatus();
    }
}
