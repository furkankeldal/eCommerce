package com.eticaret.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    @NotBlank
    private String productId;
    
    @NotBlank
    private String productName;
    
    @NotNull
    @Min(1)
    private Integer quantity;
    
    @NotNull
    @Min(0)
    private BigDecimal price;
}

