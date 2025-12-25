package com.eticaret.user.client;

import com.eticaret.user.dto.OrderResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
    name = "order-service", 
    url = "${order.service.url:http://order-service:9014}",
    fallback = OrderServiceClientFallback.class
)
public interface OrderServiceClient {
    
    @GetMapping("/api/orders/user/{userId}")
    List<OrderResponseDTO> getOrdersByUserId(@PathVariable Long userId);
}

