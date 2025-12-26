package com.eticaret.payment.service;

import com.eticaret.payment.model.PaymentRequest;
import com.eticaret.payment.producer.PaymentProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentProducer paymentProducer;

    public boolean processPayment(PaymentRequest request) {
        log.info("Processing manual payment for OrderId: {}", request.getOrderId());
        
        // Mock payment logic - always success for now
        boolean success = true;
        
        if (success) {
            log.info("Manual payment successful for OrderId: {}", request.getOrderId());
            paymentProducer.sendPaymentCompleted(request.getOrderId(), "SUCCESS");
        } else {
            log.warn("Manual payment failed for OrderId: {}", request.getOrderId());
            paymentProducer.sendPaymentCompleted(request.getOrderId(), "FAILED");
        }
        
        return success;
    }
}
