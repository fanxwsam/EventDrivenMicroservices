package com.sf.cloud.orders.saga;

import com.sf.cloud.orders.core.event.OrderCreateEvent;
import com.sf.cloud.orders.core.event.OrderRejectedEvent;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.sf.cloud.core.commands.CancelProductReserveCommand;
import com.sf.cloud.core.commands.ProcessPaymentCommand;
import com.sf.cloud.core.data.User;
import com.sf.cloud.core.events.PaymentProcessedEvent;
import com.sf.cloud.core.events.ProductReservationCancelledEvent;
import com.sf.cloud.core.events.ProductReservedEvent;
import com.sf.cloud.core.commands.ReserveProductCommand;
import com.sf.cloud.core.query.FetchUserPaymentDetailsQuery;
import com.sf.cloud.orders.command.ApproveOrderCommand;
import com.sf.cloud.orders.command.RejectOrderCommand;
import com.sf.cloud.orders.core.data.OrderSummary;
import com.sf.cloud.orders.core.event.OrderApprovedEvent;
import com.sf.cloud.orders.query.FindOrderQuery;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Saga
public class OrderSaga {
    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;

    @Autowired
    private transient QueryUpdateEmitter queryUpdateEmitter;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

    private final String PAYMENT_PROCESS_DEADLINE = "paymentProcessDeadline";

    private String scheduleId;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreateEvent orderCreateEvent) {
        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .orderId(orderCreateEvent.getOrderId())
                .productId(orderCreateEvent.getProductId())
                .quantity(orderCreateEvent.getQuantity())
                .userId(orderCreateEvent.getUserId())
                .build();

        LOGGER.info("######## ReserveProductCommand created: orderId: " + orderCreateEvent.getOrderId()
                + " productId: " + orderCreateEvent.getProductId());

        commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {
            @Override
            public void onResult(@Nonnull CommandMessage<? extends ReserveProductCommand> commandMessage, @Nonnull CommandResultMessage<?> commandResultMessage) {
                if (commandResultMessage.isExceptional()) {
                    // start a compensating transaction
                    RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(orderCreateEvent.getOrderId(), commandResultMessage.exceptionResult().getMessage());
                    commandGateway.send(rejectOrderCommand);
                    LOGGER.info("######## Exception back from other events:   " + commandResultMessage.toString());
                }
            }
        });


    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent){
        // Process user payment
        LOGGER.info("######## SagaEventHandler for Payment. ProductReservedEvent. OrderId: " + productReservedEvent.getOrderId()
                + " productId: " + productReservedEvent.getProductId());

        FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery = FetchUserPaymentDetailsQuery.builder()
                .userId(productReservedEvent.getUserId())
                .build();
        User userPaymentDetails = null;
        try {
            userPaymentDetails = queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
        }catch (Exception exception){
            LOGGER.error(exception.getMessage());
            // Start compensating transaction
            cancelProductReservation(productReservedEvent, exception.getMessage());

            return;
        }

        if (userPaymentDetails == null){
            // start compensating transaction
            cancelProductReservation(productReservedEvent, "could not fetch the user payment details..");
            return;
        }

        LOGGER.info("Successfully fetched user payment details for user: " + userPaymentDetails.getFirstName());

        scheduleId = deadlineManager.schedule(Duration.of(120, ChronoUnit.SECONDS),
                PAYMENT_PROCESS_DEADLINE,
                productReservedEvent);

        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .paymentId(UUID.randomUUID().toString())
                .paymentDetails(userPaymentDetails.getPaymentDetails())
                .build();

        String result = null;
        try {
            //result = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
            result = commandGateway.sendAndWait(processPaymentCommand);
        } catch (Exception ex){
            LOGGER.error(ex.getMessage());
            // Start compensating transaction
            cancelProductReservation(productReservedEvent, ex.getMessage());
            return;
        }

        if(result == null){
            LOGGER.info("The ProcessPaymentCommand result is null. Payment failed, please try again");
            // Start compensating transaction
            cancelProductReservation(productReservedEvent, "The ProcessPaymentCommand result is null. Payment failed. cancel the product reserve");
        }
    }

    private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason){
        cancelDeadline();
        CancelProductReserveCommand cancelProductReserveCommand = CancelProductReserveCommand.builder()
                .productId(productReservedEvent.getProductId())
                .quantity(productReservedEvent.getQuantity())
                .orderId(productReservedEvent.getOrderId())
                .userId(productReservedEvent.getUserId())
                .reason(reason)
                .build();
        commandGateway.send(cancelProductReserveCommand);

    }


    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent){
        cancelDeadline();

        // send an ApproveOrderCommand
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());
        commandGateway.send(approveOrderCommand);

    }

    private void cancelDeadline(){
        if(scheduleId != null) {
            //deadlineManager.cancelAll(PAYMENT_PROCESS_DEADLINE);
            deadlineManager.cancelSchedule(PAYMENT_PROCESS_DEADLINE, scheduleId);
            scheduleId = null;
        }
    }


    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationCancelledEvent productReservationCancelledEvent){
        // create a order cancel command
        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(productReservationCancelledEvent.getOrderId(), productReservationCancelledEvent.getReason());
        commandGateway.send(rejectOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderApprovedEvent orderApprovedEvent){
        LOGGER.info("Order is approved, order saga is complete for order Id : " + orderApprovedEvent.getOrderId());
        queryUpdateEmitter.emit(FindOrderQuery.class,
                query->true,
                new OrderSummary(orderApprovedEvent.getOrderId(), orderApprovedEvent.getOrderStatus(), "Order created successfully.")
        );
        //SagaLifecycle.end();
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderRejectedEvent orderRejectedEvent){
        LOGGER.info("Order is rejected, order saga is complete for order Id : " + orderRejectedEvent.getOrderId());
        queryUpdateEmitter.emit(FindOrderQuery.class,
                query->true,
                new OrderSummary(orderRejectedEvent.getOrderId(), orderRejectedEvent.getOrderStatus(), orderRejectedEvent.getReason())
        );
    }

    @DeadlineHandler(deadlineName = PAYMENT_PROCESS_DEADLINE)
    public void handlePaymentDeadline(ProductReservedEvent productReservedEvent){
        LOGGER.info("### Payment process deadline happened, sending compensating command to cancel product");
        cancelProductReservation(productReservedEvent, "Payment timeout");


    }




}
