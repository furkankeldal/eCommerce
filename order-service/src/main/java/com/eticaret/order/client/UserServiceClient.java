package com.eticaret.order.client;

import com.eticaret.order.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "user-service", 
    url = "${user.service.url:http://user-service:9011}",
    fallback = UserServiceClientFallback.class
)
public interface UserServiceClient {
    
    @GetMapping("/api/users/{id}")
    UserDTO getUserById(@PathVariable Long id);
}

