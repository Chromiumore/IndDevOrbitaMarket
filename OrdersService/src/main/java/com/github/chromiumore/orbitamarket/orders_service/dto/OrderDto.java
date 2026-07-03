package com.github.chromiumore.orbitamarket.orders_service.dto;

import com.github.chromiumore.orbitamarket.orders_service.domain.order.Order;

import java.time.Instant;

public record OrderDto(
        Long orderId,
        String status,
        String productType,
        Double price,
        Instant createdAt,
        String failureReason
) {
    public static OrderDto from(Order order) {
        return new OrderDto(
                order.getId(),
                order.getStatus().name(),
                order.getProductType(),
                order.getPrice(),
                order.getCreatedAt(),
                order.getFailureReason()
        );
    }
}
