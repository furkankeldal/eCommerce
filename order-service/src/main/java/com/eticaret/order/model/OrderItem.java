package com.eticaret.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
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

