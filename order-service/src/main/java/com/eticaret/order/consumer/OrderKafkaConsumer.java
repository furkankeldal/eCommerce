package com.eticaret.order.consumer;

import com.eticaret.order.model.Order;
import com.eticaret.order.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaConsumer {
    
    private final IOrderService orderService;
    
    @KafkaListener(topics = "order-created", groupId = "order-service-group-v2")
    public void consumeOrderCreated(
            @Payload Order order,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        try {
            log.info("--- [DEBUG] KAFKA MESSAGE RECEIVED V2 ---");
            log.info("Kafka mesajı alındı - Topic: {}, Partition: {}, Offset: {}", topic, partition, offset);
            log.info("Yeni sipariş oluşturuldu - OrderId: {}, UserId: {}, TotalAmount: {}, Status: {}", 
                    order.getId(), order.getUserId(), order.getTotalAmount(), order.getStatus());
            
            log.info("Sipariş oluşturma mesajı başarıyla işlendi - OrderId: {}", order.getId());
            
        } catch (Exception e) {
            log.error("Sipariş oluşturma mesajı işlenirken hata: OrderId={}, Error={}", 
                    order != null ? order.getId() : "unknown", e.getMessage(), e);
        }
    }
    
    @KafkaListener(topics = "order-status-updated", groupId = "order-service-group")
    public void consumeOrderStatusUpdated(
            @Payload Order order,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        try {
            log.info("Kafka mesajı alındı - Topic: {}, Partition: {}, Offset: {}", topic, partition, offset);
            log.info("Sipariş durumu güncellendi - OrderId: {}, UserId: {}, NewStatus: {}", 
                    order.getId(), order.getUserId(), order.getStatus());
            
            log.info("Sipariş durum güncelleme mesajı başarıyla işlendi - OrderId: {}", order.getId());
            
        } catch (Exception e) {
            log.error("Sipariş durum güncelleme mesajı işlenirken hata: OrderId={}, Error={}", 
                    order != null ? order.getId() : "unknown", e.getMessage(), e);
        }
    }
    
    @KafkaListener(topics = "order-cancelled", groupId = "order-service-group")
    public void consumeOrderCancelled(
            @Payload Order order,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        try {
            log.info("Kafka mesajı alındı - Topic: {}, Partition: {}, Offset: {}", topic, partition, offset);
            log.info("Sipariş iptal edildi - OrderId: {}, UserId: {}, Status: {}", 
                    order.getId(), order.getUserId(), order.getStatus());
            
            log.info("Sipariş iptal mesajı başarıyla işlendi - OrderId: {}", order.getId());
            
        } catch (Exception e) {
            log.error("Sipariş iptal mesajı işlenirken hata: OrderId={}, Error={}", 
                    order != null ? order.getId() : "unknown", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "payment-completed", groupId = "order-service-group")
    public void consumePaymentCompleted(
            @Payload Map<String, Object> paymentData,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            log.info("Kafka mesajı alındı - Topic: {}", topic);
            Long orderId = Long.valueOf(paymentData.get("orderId").toString());
            String status = paymentData.get("status").toString();
            
            if ("SUCCESS".equals(status)) {
                log.info("Ödeme başarılı. Sipariş durumu PAID olarak güncelleniyor - OrderId: {}", orderId);
                orderService.updateOrderStatus(orderId, "PAID");
            } else {
                log.warn("Ödeme başarısız - OrderId: {}", orderId);
            }
            
        } catch (Exception e) {
            log.error("Ödeme mesajı işlenirken hata: {}", e.getMessage(), e);
        }
    }
}

