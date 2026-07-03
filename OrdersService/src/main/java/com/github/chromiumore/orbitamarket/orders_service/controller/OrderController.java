package com.github.chromiumore.orbitamarket.orders_service.controller;

import com.github.chromiumore.orbitamarket.orders_service.domain.order.Order;
import com.github.chromiumore.orbitamarket.orders_service.dto.CreateOrderRequest;
import com.github.chromiumore.orbitamarket.orders_service.dto.OrderDto;
import com.github.chromiumore.orbitamarket.orders_service.exception.MissingUserIdException;
import com.github.chromiumore.orbitamarket.orders_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
            @RequestHeader(value = "X-User-Id", required = false) UUID userId,
            @RequestBody CreateOrderRequest request
    ) {
        if (userId == null) {
            throw new MissingUserIdException("X-User-Id is not provided");
        }
        Order order = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderDto.from(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getUserOrders(
            @RequestHeader(value = "X-User-Id", required = false) UUID userId
    ) {
        if (userId == null) {
            throw new MissingUserIdException("X-User-Id is not provided");
        }
        List<Order> orders = orderService.getUserOrdersData(userId);
        return ResponseEntity.ok(orders.stream().map(OrderDto::from).toList());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrder(
            @RequestHeader(value = "X-User-Id", required = false) UUID userId,
            @PathVariable("orderId") Long orderId
    ) {
        if (userId == null) {
            throw new MissingUserIdException("X-User-Id is not provided");
        }
        Order order = orderService.getOrder(userId, orderId);
        return ResponseEntity.ok(OrderDto.from(order));
    }
}
