package com.eticaret.order.client;

import com.eticaret.order.dto.StockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "stock-service", 
    url = "${stock.service.url:http://stock-service:9013}",
    fallback = StockServiceClientFallback.class
)
public interface StockServiceClient {
    
    @GetMapping("/api/stocks/product/{productId}")
    StockDTO getStockByProductId(@PathVariable String productId);
    
    @PostMapping("/api/stocks/{id}/reserve")
    StockDTO reserveStock(@PathVariable Long id, @RequestParam Integer quantity);
    
    @PostMapping("/api/stocks/{id}/release")
    StockDTO releaseStock(@PathVariable Long id, @RequestParam Integer quantity);
}

