package com.github.chromiumore.orbitamarket.payments_service.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_accounts")
@Getter
@Setter
@NoArgsConstructor
public class PaymentAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("user_id")
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @JsonProperty("balance")
    @Column(nullable = false)
    private Double amount = 0.0;

    @JsonProperty("created_at")
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @JsonIgnore
    @Version
    private Long version;
}
