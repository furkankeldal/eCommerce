package com.eticaret.order.client;

import com.eticaret.order.dto.ProductDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductServiceClientFallback implements ProductServiceClient {
    
    @Override
    public ProductDTO getProductById(String id) {
        log.error("Product Service çağrısı başarısız oldu - Fallback devreye girdi: productId={}", id);
        return null; // null döndür, Order Service'te kontrol edilecek
    }
}

