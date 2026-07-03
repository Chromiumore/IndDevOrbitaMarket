package com.github.chromiumore.orbitamarket.orders_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.chromiumore.orbitamarket.orders_service.domain.order.Order;
import com.github.chromiumore.orbitamarket.orders_service.domain.order.OrderStatus;
import com.github.chromiumore.orbitamarket.orders_service.domain.order.ProductType;
import com.github.chromiumore.orbitamarket.orders_service.dto.CreateOrderRequest;
import com.github.chromiumore.orbitamarket.orders_service.exception.InvalidPriceExcepion;
import com.github.chromiumore.orbitamarket.orders_service.exception.OrderNotFoundException;
import com.github.chromiumore.orbitamarket.orders_service.exception.UnknownProductTypeException;
import com.github.chromiumore.orbitamarket.orders_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException("Order not found")
        );
    }

    private String convertMapToJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting map to JSON", e);
        }
    }
}
