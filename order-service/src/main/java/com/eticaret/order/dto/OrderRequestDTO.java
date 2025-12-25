package com.eticaret.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    @NotNull
    private Long userId;
    
    @NotNull
    @NotEmpty
    private List<OrderItemDTO> items;
    
    private String shippingAddress;
    // totalAmount ve status service tarafından hesaplanacak
}

