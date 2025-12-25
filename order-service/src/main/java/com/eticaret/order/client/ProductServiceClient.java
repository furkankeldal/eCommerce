package com.eticaret.order.client;

import com.eticaret.order.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "product-service", 
    url = "${product.service.url:http://product-service:9012}",
    fallback = ProductServiceClientFallback.class
)
public interface ProductServiceClient {
    
    @GetMapping("/api/products/{id}")
    ProductDTO getProductById(@PathVariable String id);
}

