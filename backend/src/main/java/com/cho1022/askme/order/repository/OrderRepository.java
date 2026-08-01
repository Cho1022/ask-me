package com.cho1022.askme.order.repository;

import com.cho1022.askme.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
