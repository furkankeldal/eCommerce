package com.eticaret.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {
    private Long id;
    private String productId;
    private Integer quantity;
    private Integer reservedQuantity;
    private String location;
    
    public Integer getAvailableQuantity() {
        return quantity - reservedQuantity;
    }
}

