package com.github.chromiumore.orbitamarket.payments_service.domain.inbox;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_events_inbox", uniqueConstraints = { @UniqueConstraint(columnNames = "order_id", name = "uk_trans_order") })
@NoArgsConstructor
@Getter
@Setter
public class InboxPaymentEvent {
    @Id
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
