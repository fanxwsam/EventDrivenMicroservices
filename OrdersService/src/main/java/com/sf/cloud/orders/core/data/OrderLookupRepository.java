package com.sf.cloud.orders.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLookupRepository extends JpaRepository<OrderLookupEntity, String> {
}
