package com.sf.cloud.orders.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class RejectOrderCommand {
    @TargetAggregateIdentifier
    private final String orderId;
    private final String reason;
}
