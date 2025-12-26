package com.eticaret.payment.consumer;

import com.eticaret.payment.model.PaymentRequest;
import com.eticaret.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "order-created", groupId = "payment-service-group")
    public void consumeOrderCreated(
            @Payload Map<String, Object> orderData,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            log.info("--- [PAYMENT MOCK] RECEIVED ORDER-CREATED EVENT ---");
            Long orderId = Long.valueOf(orderData.get("id").toString());
            
            log.info("Processing automatic payment for OrderId: {}", orderId);
            
            PaymentRequest request = new PaymentRequest();
            request.setOrderId(orderId);
            
            // Simulate payment processing time
            Thread.sleep(2000);
            
            paymentService.processPayment(request);
            
        } catch (Exception e) {
            log.error("Error processing automatic payment: {}", e.getMessage(), e);
        }
    }
}
