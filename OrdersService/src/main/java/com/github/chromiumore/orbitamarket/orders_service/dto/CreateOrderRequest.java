package com.github.chromiumore.orbitamarket.orders_service.dto;

import java.util.Map;

public record CreateOrderRequest(
        String productType,
        Long price,
        Map<String, Object> payload
) {
}
