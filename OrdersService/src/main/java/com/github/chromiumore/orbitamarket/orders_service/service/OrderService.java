package com.github.chromiumore.orbitamarket.orders_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.chromiumore.orbitamarket.orders_service.domain.order.Order;
import com.github.chromiumore.orbitamarket.orders_service.domain.order.OrderStatus;
import com.github.chromiumore.orbitamarket.orders_service.domain.order.ProductType;
import com.github.chromiumore.orbitamarket.orders_service.dto.CreateOrderRequest;
import com.github.chromiumore.orbitamarket.orders_service.exception.InvalidPayloadException;
import com.github.chromiumore.orbitamarket.orders_service.exception.InvalidPriceException;
import com.github.chromiumore.orbitamarket.orders_service.exception.OrderNotFoundException;
import com.github.chromiumore.orbitamarket.orders_service.exception.UnknownProductTypeException;
import com.github.chromiumore.orbitamarket.orders_service.kafka.producer.PaymentEventProducer;
import com.github.chromiumore.orbitamarket.orders_service.repository.OrderRepository;
import com.github.chromiumore.orbitamarket.orders_service.service.outbox.OutboxPaymentEventsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxPaymentEventsService outboxService;
    private final PaymentEventProducer paymentProducer;
    private final ObjectMapper objectMapper;

    @Transactional(noRollbackFor = { InvalidPriceException.class, UnknownProductTypeException.class, InvalidPayloadException.class })
    public Order createOrder(UUID userId, CreateOrderRequest request) {

        Order order = new Order();
        order.setUserId(userId);
        order.setProductType(request.productType());
        order.setPrice(request.price());
        order.setPayload(convertMapToJson(request.payload()));
        order.setCreatedAt(Instant.now());

        RuntimeException exception = null;
        if (request.price() <= 0) {
            exception = new InvalidPriceException("Price must be grater than zero");
            order.setFailureReason("INVALID_PRICE");
        } else if (Arrays.stream(ProductType.values())
                .noneMatch(e -> e.name().equals(request.productType()))) {
            exception = new UnknownProductTypeException("Unknown product type: " + request.productType());
            order.setFailureReason("UNKNOWN_PRODUCT_TYPE");
        } else if (!validatePayload(request.payload())) {
            exception = new InvalidPayloadException("Invalid payload");
            order.setFailureReason("INVALID_PAYLOAD");
        }

        if (exception != null) {
            order.setStatus(OrderStatus.REJECTED);
            orderRepository.save(order);

            throw exception;
        }

        order.setStatus(OrderStatus.CREATED);
        order = orderRepository.save(order);

        outboxService.createOutboxEvent(order);

        return order;
    }

    @Transactional(readOnly = true)
    public List<Order> getUserOrdersData(UUID userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
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

    private boolean validatePayload(Map<String, Object> payload) {
        return payload.containsKey("aoi") && payload.containsKey("capture_date") && payload.containsKey("sensor_type");
    }
}
