package com.sf.cloud.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;
import com.sf.cloud.core.commands.CancelProductReserveCommand;
import com.sf.cloud.core.events.ProductReservationCancelledEvent;
import com.sf.cloud.core.events.ProductReservedEvent;
import com.sf.cloud.core.commands.ReserveProductCommand;
import com.sf.cloud.core.events.ProductCreateEvent;

import java.math.BigDecimal;

@Aggregate(snapshotTriggerDefinition = "productSnapshotTriggerDefinition")
public class ProductAggregate {
    @AggregateIdentifier
    private String productId;
    private String title;
    private BigDecimal price;
    private Integer quantity;

    public ProductAggregate(){

    }

    @CommandHandler
    public ProductAggregate(CreateProductCommand createProductCommand)  {

        // validate create product command
        // business logic
        if(createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("price cannot be less or equal zero");
        }

        if(createProductCommand.getTitle().isBlank()){
            throw new IllegalArgumentException("Title cannot be empty");
        }

        ProductCreateEvent productCreateEvent = new ProductCreateEvent();
        BeanUtils.copyProperties(createProductCommand, productCreateEvent);

        AggregateLifecycle.apply(productCreateEvent);

        // !!!! code here will run before the code above :  AggregateLifecycle.apply(productCreateEvent);
        // !!!! If there's any exception happen in method ProductAggregate (a CommandHandler), events will be sent
//        if(true){
//            throw new Exception("An error took place in the CreateProductCommandHandler @ProductAggregate");
//        }

    }

    @EventSourcingHandler
    public void on(ProductCreateEvent productCreateEvent){
        this.productId = productCreateEvent.getProductId();
        this.title = productCreateEvent.getTitle();
        this.price = productCreateEvent.getPrice();
        this.quantity = productCreateEvent.getQuantity();
    }


    @CommandHandler
    public void handle(ReserveProductCommand reserveProductCommand){
        if(quantity < reserveProductCommand.getQuantity()){
            throw new IllegalArgumentException("Insufficient number of items in stock");
        }

        ProductReservedEvent productReservedEvent = ProductReservedEvent.builder()
                .orderId(reserveProductCommand.getOrderId())
                .productId(reserveProductCommand.getProductId())
                .quantity(reserveProductCommand.getQuantity())
                .userId(reserveProductCommand.getUserId())
                .build();

        AggregateLifecycle.apply(productReservedEvent);
    }

    @EventSourcingHandler
    public void on(ProductReservedEvent productReservedEvent){
        this.quantity = this.quantity - productReservedEvent.getQuantity();
    }

    @CommandHandler
    public  void handle(CancelProductReserveCommand cancelProductReserveCommand){
        ProductReservationCancelledEvent productReservationCancelledEvent = ProductReservationCancelledEvent.builder()
                .productId(cancelProductReserveCommand.getProductId())
                .orderId(cancelProductReserveCommand.getOrderId())
                .userId(cancelProductReserveCommand.getUserId())
                .reason(cancelProductReserveCommand.getReason())
                .quantity(cancelProductReserveCommand.getQuantity())
                .build();

        AggregateLifecycle.apply(productReservationCancelledEvent);


    }

    @EventSourcingHandler
    public void on(ProductReservationCancelledEvent productReservationCancelledEvent){
        this.quantity = this.quantity + productReservationCancelledEvent.getQuantity();
    }

}
