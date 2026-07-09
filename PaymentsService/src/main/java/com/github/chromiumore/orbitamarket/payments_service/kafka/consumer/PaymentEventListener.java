package com.github.chromiumore.orbitamarket.payments_service.kafka.consumer;

import com.github.chromiumore.orbitamarket.payments_service.domain.account.PaymentAccount;
import com.github.chromiumore.orbitamarket.payments_service.dto.event.PaymentRequestedEvent;
import com.github.chromiumore.orbitamarket.payments_service.dto.event.PaymentResponseEvent;
import com.github.chromiumore.orbitamarket.payments_service.exception.AccountNotFoundException;
import com.github.chromiumore.orbitamarket.payments_service.exception.InsufficientBalanceException;
import com.github.chromiumore.orbitamarket.payments_service.exception.InvalidAmountException;
import com.github.chromiumore.orbitamarket.payments_service.exception.event.EventDuplicateException;
import com.github.chromiumore.orbitamarket.payments_service.exception.event.OrderDuplicateException;
import com.github.chromiumore.orbitamarket.payments_service.kafka.producer.PaymentEventProducer;
import com.github.chromiumore.orbitamarket.payments_service.service.PaymentAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventListener {

    public final static String ORDER_EVENTS_TOPIC = "orders-payment-requests";

    private final PaymentAccountService accountService;
    private final PaymentEventProducer paymentProducer;

    @KafkaListener(topics = ORDER_EVENTS_TOPIC, groupId = "payment-service")
    public void handleOrderEvent(PaymentRequestedEvent event) {
        try {
            PaymentAccount account = accountService.processDebitEvent(event);

            paymentProducer.sendToKafka(PaymentResponseEvent.createCompletedEvent(account, event.orderId(), event.amount()));
        } catch (InvalidAmountException e) {
            log.error("Failed to process order payment: invalid amount");
            paymentProducer.sendToKafka(PaymentResponseEvent.createFailedEvent(event.userId(), event.orderId(), "INVALID_AMOUNT"));
        } catch (AccountNotFoundException e) {
            log.error("Failed to process order payment: account not found");
            paymentProducer.sendToKafka(PaymentResponseEvent.createFailedEvent(event.userId(), event.orderId(), "ACCOUNT_NOT_FOUND"));
        } catch (InsufficientBalanceException e) {
            log.error("Failed to process order payment: insufficient balance");
            paymentProducer.sendToKafka(PaymentResponseEvent.createFailedEvent(event.userId(), event.orderId(), "INSUFFICIENT_BALANCE"));
        } catch (EventDuplicateException e) {
            log.error("Failed to process order payment: event duplicate");
        } catch (OrderDuplicateException e) {
            log.error("Failed to process order payment: order duplicate");
        } catch (Exception e) {
            log.error("Failed to process order payment", e);
            paymentProducer.sendToKafka(PaymentResponseEvent.createFailedEvent(event.userId(), event.orderId(), "INTERNAL_ERROR"));
        }
    }
}
