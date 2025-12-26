package com.eticaret.payment.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProducer {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public void sendPaymentCompleted(Long orderId, String status) {
        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("orderId", orderId);
        paymentData.put("status", status);
        paymentData.put("timestamp", System.currentTimeMillis());
        
        log.info("Sending payment-completed event for OrderId: {}, Status: {}", orderId, status);
        kafkaTemplate.send("payment-completed", paymentData);
    }
}
