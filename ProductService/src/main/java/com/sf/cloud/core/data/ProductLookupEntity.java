package com.sf.cloud.core.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Table(name="ProductLookUp")
@Entity
public class ProductLookupEntity implements Serializable {
    private static final long serialVersionUID = -227687987989899L;
    @Id
    @Column(unique = true)
    private String productId;

    @Column(unique = true)
    private String title;

    public ProductLookupEntity() {
    }

    public ProductLookupEntity(String productId, String title) {
        this.productId = productId;
        this.title = title;
    }
}
