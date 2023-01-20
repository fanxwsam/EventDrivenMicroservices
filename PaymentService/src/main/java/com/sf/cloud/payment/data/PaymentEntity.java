package com.sf.cloud.payment.data;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "payments")
@Data
public class PaymentEntity {
    @Id
    @Column(unique = true)
    private String paymentId;
    @Column
    public String orderId;

}
