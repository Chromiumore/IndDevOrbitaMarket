package com.github.chromiumore.orbitamarket.orders_service.dto;

import java.util.Map;

public record CreateOrderRequest(
        String productType,
        Double price,
        Map<String, Object> payload
) {
}
