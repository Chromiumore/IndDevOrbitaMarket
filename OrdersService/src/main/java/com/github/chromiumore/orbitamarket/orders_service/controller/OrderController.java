package com.github.chromiumore.orbitamarket.orders_service.controller;

import com.github.chromiumore.orbitamarket.orders_service.domain.order.Order;
import com.github.chromiumore.orbitamarket.orders_service.dto.CreateOrderRequest;
import com.github.chromiumore.orbitamarket.orders_service.dto.OrderDto;
import com.github.chromiumore.orbitamarket.orders_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
            @RequestHeader(value = "X-User-Id") UUID userId,
            @RequestBody CreateOrderRequest request
    ) {
        Order order = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderDto.from(order));
    }
}
