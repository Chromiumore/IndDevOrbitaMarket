package com.github.chromiumore.orbitamarket.payments_service.kafka;

import com.github.chromiumore.orbitamarket.payments_service.domain.account.PaymentAccount;
import com.github.chromiumore.orbitamarket.payments_service.service.PaymentAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventListener {

    public final static String ORDER_EVENTS_TOPIC = "orders-payment-requests";

    private final PaymentAccountService accountService;
    private final KafkaService kafkaService;

    @KafkaListener(topics = ORDER_EVENTS_TOPIC, groupId = "payment-service")
    public void handleOrderEvent(OrderPaymentRequest event, Acknowledgment ack) {
        try {
            PaymentAccount account = accountService.debitForOrder(event.userId(), event.orderId(), event.amount());
            ack.acknowledge();

            kafkaService.sendToKafka(ORDER_EVENTS_TOPIC, KafkaUtils.createCompletedEvent(account, event.orderId(), event.amount()));
        } catch (Exception e) {
            log.error("Failed to process order payment", e);
            kafkaService.sendToKafka(ORDER_EVENTS_TOPIC, KafkaUtils.createFailedEvent(event.userId(), event.orderId(), e.getMessage()));

            throw e;
        }
    }
}
