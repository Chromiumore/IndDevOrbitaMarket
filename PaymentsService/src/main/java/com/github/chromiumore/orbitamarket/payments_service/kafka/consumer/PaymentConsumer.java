package com.github.chromiumore.orbitamarket.payments_service.kafka.consumer;

import com.github.chromiumore.orbitamarket.payments_service.domain.account.PaymentAccount;
import com.github.chromiumore.orbitamarket.payments_service.dto.event.OrderPaymentRequest;
import com.github.chromiumore.orbitamarket.payments_service.dto.event.OrderPaymentResponse;
import com.github.chromiumore.orbitamarket.payments_service.kafka.producer.PaymentProducer;
import com.github.chromiumore.orbitamarket.payments_service.service.PaymentAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentConsumer {

    public final static String ORDER_EVENTS_TOPIC = "orders-payment-requests";

    private final PaymentAccountService accountService;
    private final PaymentProducer paymentProducer;

    @KafkaListener(topics = ORDER_EVENTS_TOPIC, groupId = "payment-service")
    public void handleOrderEvent(OrderPaymentRequest event) {
        try {
            PaymentAccount account = accountService.debitForOrder(event.userId(), event.orderId(), event.amount());

            paymentProducer.sendToKafka(OrderPaymentResponse.createCompletedEvent(account, event.orderId(), event.amount()));
        } catch (Exception e) {
            log.error("Failed to process order payment", e);
            paymentProducer.sendToKafka(OrderPaymentResponse.createFailedEvent(event.userId(), event.orderId(), e.getMessage()));

            throw e;
        }
    }
}
