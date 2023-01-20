package com.sf.cloud.core.commands;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import com.sf.cloud.core.data.PaymentDetails;

@Data
@Builder
public class ProcessPaymentCommand {
    @TargetAggregateIdentifier
    private final String paymentId;
    private final String orderId;
    private final PaymentDetails paymentDetails;

}
