package com.github.chromiumore.orbitamarket.orders_service.repository.outbox;

import com.github.chromiumore.orbitamarket.orders_service.domain.outbox.OrderPaymentsOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderPaymentsOutboxRepository extends JpaRepository<OrderPaymentsOutbox, UUID> {
    List<OrderPaymentsOutbox> findBySentAtIsNull();
}
