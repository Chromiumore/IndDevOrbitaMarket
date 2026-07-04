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
import com.github.chromiumore.orbitamarket.orders_service.kafka.KafkaService;
import com.github.chromiumore.orbitamarket.orders_service.kafka.KafkaUtils;
import com.github.chromiumore.orbitamarket.orders_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    public final static String ORDER_EVENTS_TOPIC = "orders-payment-requests";
    private final OrderRepository orderRepository;
    private final KafkaService kafkaService;
    private final ObjectMapper objectMapper;

    public Order createOrder(UUID userId, CreateOrderRequest request) {

        Order order = new Order();
        order.setUserId(userId);
        order.setProductType(request.productType());
        order.setPrice(request.price());
        order.setPayload(convertMapToJson(request.payload()));
        order.setCreatedAt(Instant.now());

        RuntimeException exception = null;
        if (request.price() <= 0) {
            exception = new InvalidPriceExcepion("Price must be grater than zero");
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

        kafkaService.sendToKafka(ORDER_EVENTS_TOPIC, KafkaUtils.createEvent(order));

        return order;
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

    private boolean validatePayload(Map<String, Object> payload) {
        return payload.containsKey("aoi") && payload.containsKey("capture_date") && payload.containsKey("sensor_type");
    }
}
