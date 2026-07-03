package com.github.chromiumore.orbitamarket.orders_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.chromiumore.orbitamarket.orders_service.domain.order.Order;
import com.github.chromiumore.orbitamarket.orders_service.domain.order.OrderStatus;
import com.github.chromiumore.orbitamarket.orders_service.domain.order.ProductType;
import com.github.chromiumore.orbitamarket.orders_service.dto.CreateOrderRequest;
import com.github.chromiumore.orbitamarket.orders_service.exception.InvalidPayloadException;
import com.github.chromiumore.orbitamarket.orders_service.exception.InvalidPriceExcepion;
import com.github.chromiumore.orbitamarket.orders_service.exception.OrderNotFoundException;
import com.github.chromiumore.orbitamarket.orders_service.exception.UnknownProductTypeException;
import com.github.chromiumore.orbitamarket.orders_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    public Order createOrder(UUID userId, CreateOrderRequest request) {
        Double price = request.price();
        if (price <= 0) {
            throw new InvalidPriceExcepion("Price must be grater than zero");
        }

        String productType = request.productType();
        if (Arrays.stream(ProductType.values())
                .noneMatch(e -> e.name().equals(productType))) {
            throw new UnknownProductTypeException("Unknown product type: " + productType);
        }

        validatePayload(request.payload());

        Order order = new Order();
        order.setUserId(userId);
        order.setProductType(request.productType());
        order.setPrice(request.price());
        order.setPayload(convertMapToJson(request.payload()));
        order.setCreatedAt(Instant.now());

        order.setStatus(OrderStatus.CREATED);
        return orderRepository.save(order);
    }

    public List<Order> getUserOrdersData(UUID userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Order getOrder(UUID userId, Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);

        if (order.isEmpty() || !order.get().getUserId().equals(userId)) {
            throw  new OrderNotFoundException("Order not found");
        }

        return order.get();
    }

    private String convertMapToJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new InvalidPayloadException("Failed to process order payload");
        }
    }

    private void validatePayload(Map<String, Object> payload) {
         if (!(payload.containsKey("aoi") && payload.containsKey("capture_date") && payload.containsKey("sensor_type"))) {
             throw new InvalidPayloadException("Invalid payload");
         }
    }
}
