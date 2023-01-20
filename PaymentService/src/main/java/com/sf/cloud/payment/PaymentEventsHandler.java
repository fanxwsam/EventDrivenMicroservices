package com.sf.cloud.payment;

import com.sf.cloud.payment.data.PaymentEntity;
import com.sf.cloud.payment.data.PaymentsRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import com.sf.cloud.core.events.PaymentProcessedEvent;

@Component
public class PaymentEventsHandler {
    private PaymentsRepository paymentsRepository;

    public PaymentEventsHandler(PaymentsRepository paymentsRepository) {
        this.paymentsRepository = paymentsRepository;
    }

    @EventHandler
    public void on(PaymentProcessedEvent event){
        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setOrderId(event.getOrderId());
        paymentEntity.setPaymentId(event.getPaymentId());

        paymentsRepository.save(paymentEntity);
    }
}
