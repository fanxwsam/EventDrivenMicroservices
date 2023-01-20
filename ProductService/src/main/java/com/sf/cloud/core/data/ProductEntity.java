package com.sf.cloud.core.data;

import lombok.Data;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name="products")
@Data
public class ProductEntity implements Serializable {
    private static final long serialVersionUID = -227687987989898L;

    @Id
    @Column(unique = true)
    private String productId;

    @Column(unique = true)
    private String title;

    private BigDecimal price;

    private Integer quantity;


}
