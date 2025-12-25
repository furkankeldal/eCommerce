package com.eticaret.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {
    @NotBlank
    private String name;
    
    private String description;
    
    @NotNull
    @Positive
    private BigDecimal price;
    
    @NotBlank
    private String category;
    
    private String imageUrl;
}

