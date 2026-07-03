package com.github.chromiumore.orbitamarket.orders_service.repository;

import com.github.chromiumore.orbitamarket.orders_service.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
