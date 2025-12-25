package com.eticaret.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockResponseDTO {
    private Long id;
    private String productId;
    private Integer quantity;
    private Integer reservedQuantity;
    private Integer availableQuantity;
    private String location;
}

