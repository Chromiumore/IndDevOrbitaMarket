package com.github.chromiumore.orbitamarket.payments_service.service.inbox;

import com.github.chromiumore.orbitamarket.payments_service.domain.inbox.InboxPaymentEvent;
import com.github.chromiumore.orbitamarket.payments_service.dto.event.PaymentRequestedEvent;
import com.github.chromiumore.orbitamarket.payments_service.repository.inbox.PaymentEventsInboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentEventsInboxService {

    private final PaymentEventsInboxRepository inboxRepository;

    @Transactional
    public InboxPaymentEvent createInboxEvent(PaymentRequestedEvent event) {
        InboxPaymentEvent inbox = new InboxPaymentEvent();
        inbox.setId(event.eventId());
        inbox.setOrderId(event.orderId());
        inbox.setUserId(event.userId());
        inbox.setAmount(event.amount());
        inbox.setCreatedAt(Instant.now());

        return inboxRepository.save(inbox);
    }

    @Transactional(readOnly = true)
    public Optional<InboxPaymentEvent> getInboxEventByEventId(UUID eventId) {
        return inboxRepository.findById(eventId);
    }

    @Transactional(readOnly = true)
    public Optional<InboxPaymentEvent> getInboxEventByOrderId(Long orderId) {
        return inboxRepository.findByOrderId(orderId);
    }
}
