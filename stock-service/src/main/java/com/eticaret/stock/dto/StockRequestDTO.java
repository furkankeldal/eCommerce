package com.eticaret.stock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockRequestDTO {
    @NotBlank
    private String productId;
    
    @NotNull
    @Min(0)
    private Integer quantity;
    
    @Min(0)
    private Integer reservedQuantity;
    
    private String location;
}

