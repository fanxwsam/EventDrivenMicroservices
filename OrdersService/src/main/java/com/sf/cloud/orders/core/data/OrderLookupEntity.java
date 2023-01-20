package com.sf.cloud.orders.core.data;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name="OrderLookUp")
public class OrderLookupEntity implements Serializable {
    private static final long serialVersionUID = -227687987989453L;

    @Id
    @Column(unique = true)
    private String orderId;

    public OrderLookupEntity() {
    }

    public OrderLookupEntity(String orderId) {
        this.orderId = orderId;
    }
}
