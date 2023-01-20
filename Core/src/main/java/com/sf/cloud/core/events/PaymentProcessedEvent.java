package com.sf.cloud.core.events;

import lombok.Data;
import com.sf.cloud.core.data.PaymentDetails;

@Data
public class PaymentProcessedEvent {

    private String orderId;
    private String paymentId;
    private PaymentDetails paymentDetails;
}
