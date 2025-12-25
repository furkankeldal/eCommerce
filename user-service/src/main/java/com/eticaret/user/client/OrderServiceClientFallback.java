package com.eticaret.user.client;

import com.eticaret.user.dto.OrderResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class OrderServiceClientFallback implements OrderServiceClient {
    
    @Override
    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        log.error("Order Service çağrısı başarısız oldu - Fallback devreye girdi: userId={}", userId);
        return new ArrayList<>(); // Boş liste döndür
    }
}

