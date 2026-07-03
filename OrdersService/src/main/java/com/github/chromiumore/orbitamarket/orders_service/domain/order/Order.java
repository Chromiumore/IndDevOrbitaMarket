package com.github.chromiumore.orbitamarket.orders_service.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "product_type", nullable = false)
    private String productType;

    @Column(nullable = false)
    private Long price;

    @JdbcTypeCode(SqlTypes.JSON)
    private String payload;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @JsonIgnore
    @Version
    private Long version;
}
