package com.eticaret.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private Long id;
    private Long userId;
    private List<OrderItemDTO> items;
    private BigDecimal totalAmount;
    private String status; // OrderStatus enum'ı String olarak dönecek
    private String shippingAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

