package com.eticaret.gateway.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
public class GatewayController {
    
    @Bean
    public RouterFunction<ServerResponse> rootRoute() {
        return route()
            .GET("/", request -> ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                        "message": "E-Ticaret API Gateway",
                        "version": "1.0.0",
                        "endpoints": {
                            "users": "/api/users",
                            "products": "/api/products",
                            "stocks": "/api/stocks",
                            "orders": "/api/orders",
                            "payments": "/api/payments"
                        },
                        "swagger": {
                            "user-service": "http://localhost:8090/swagger/users/index.html",
                            "product-service": "http://localhost:8090/swagger/products/index.html",
                            "stock-service": "http://localhost:8090/swagger/stocks/index.html",
                            "order-service": "http://localhost:8090/swagger/orders/index.html",
                            "payment-service": "http://localhost:8090/swagger/payments/index.html"
                        },
                        "note": "Swagger UI'lar ve API istekleri API Gateway üzerinden yapılmalıdır."
                    }
                    """))
            .GET("/health", request -> ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                        "status": "UP",
                        "service": "api-gateway"
                    }
                    """))
            .build();
    }
}

