package com.eticaret.order.client;

import com.eticaret.order.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserServiceClientFallback implements UserServiceClient {
    
    @Override
    public UserDTO getUserById(Long id) {
        log.error("User Service çağrısı başarısız oldu - Fallback devreye girdi: userId={}", id);
        return null; // null döndür, Order Service'te kontrol edilecek
    }
}

