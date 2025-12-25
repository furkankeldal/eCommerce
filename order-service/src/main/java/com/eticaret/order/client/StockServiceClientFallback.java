package com.eticaret.order.client;

import com.eticaret.order.dto.StockDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StockServiceClientFallback implements StockServiceClient {
    
    @Override
    public StockDTO getStockByProductId(String productId) {
        log.error("Stock Service çağrısı başarısız oldu - Fallback devreye girdi: productId={}", productId);
        return null; // null döndür, Order Service'te kontrol edilecek
    }
    
    @Override
    public StockDTO reserveStock(Long id, Integer quantity) {
        log.error("Stock Service rezerve çağrısı başarısız oldu - Fallback devreye girdi: stockId={}, quantity={}", id, quantity);
        throw new RuntimeException("Stock Service kullanılamıyor - Stok rezerve edilemedi");
    }
    
    @Override
    public StockDTO releaseStock(Long id, Integer quantity) {
        log.error("Stock Service release çağrısı başarısız oldu - Fallback devreye girdi: stockId={}, quantity={}", id, quantity);
        // Release işlemi için exception fırlatmıyoruz, sadece logluyoruz
        // Çünkü bu işlem kritik değil (sipariş iptal durumunda)
        return null;
    }
}

