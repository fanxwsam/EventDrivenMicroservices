package com.sf.cloud.payment;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;
import com.sf.cloud.core.commands.ProcessPaymentCommand;
import com.sf.cloud.core.data.PaymentDetails;
import com.sf.cloud.core.events.PaymentProcessedEvent;

@Aggregate
public class PaymentAggregate {
    @AggregateIdentifier
    private String paymentId;
    private String orderId;
    private String status;
    private PaymentDetails paymentDetails;



    @CommandHandler
    public PaymentAggregate(ProcessPaymentCommand processPaymentCommand) {

        PaymentProcessedEvent paymentProcessedEvent = new PaymentProcessedEvent();

        BeanUtils.copyProperties(processPaymentCommand, paymentProcessedEvent);

        if(processPaymentCommand.getPaymentDetails() == null || processPaymentCommand.getPaymentDetails().getCvv() == null || processPaymentCommand.getPaymentDetails().getCvv().equals("")){
            throw new IllegalArgumentException("Wrong payment information!!!");
        }

        AggregateLifecycle.apply(paymentProcessedEvent);
    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent paymentProcessedEvent){
        this.orderId = paymentProcessedEvent.getOrderId();
        this.paymentId = paymentProcessedEvent.getPaymentId();
        this.status = "PAID_SUCCESS";
        this.paymentDetails = paymentProcessedEvent.getPaymentDetails();
    }




}
