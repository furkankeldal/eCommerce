package com.eticaret.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private Long id;
    
    @NotNull
    private Long userId;
    
    @NotNull
    private List<OrderItem> items;
    
    @NotNull
    @Min(0)
    private BigDecimal totalAmount;
    
    private OrderStatus status;
    
    private String shippingAddress;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    {
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}

