package com.github.chromiumore.orbitamarket.payments_service.repository.inbox;

import com.github.chromiumore.orbitamarket.payments_service.domain.inbox.InboxPaymentEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentEventsInboxRepository extends JpaRepository<InboxPaymentEvent, UUID> {
    Optional<InboxPaymentEvent> findByOrderId(Long orderId);
}
