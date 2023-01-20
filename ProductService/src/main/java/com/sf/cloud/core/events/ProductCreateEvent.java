package com.sf.cloud.core.events;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreateEvent {
    private String productId;
    private String title;
    private BigDecimal price;
    private Integer quantity;

}
