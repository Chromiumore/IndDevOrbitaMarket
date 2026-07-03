package com.github.chromiumore.orbitamarket.orders_service.repository;

import com.github.chromiumore.orbitamarket.orders_service.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
