package com.eticaret.payment.controller;

import com.eticaret.payment.model.PaymentRequest;
import com.eticaret.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<String> processPayment(@RequestBody PaymentRequest request) {
        boolean result = paymentService.processPayment(request);
        if (result) {
            return ResponseEntity.ok("Payment processed successfully for OrderId: " + request.getOrderId());
        } else {
            return ResponseEntity.badRequest().body("Payment failed for OrderId: " + request.getOrderId());
        }
    }
}
