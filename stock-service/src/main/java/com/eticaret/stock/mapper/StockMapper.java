package com.eticaret.stock.mapper;

import com.eticaret.stock.dto.StockRequestDTO;
import com.eticaret.stock.dto.StockResponseDTO;
import com.eticaret.stock.model.Stock;
import org.springframework.stereotype.Component;

@Component
public class StockMapper {
    
    public Stock toEntity(StockRequestDTO dto) {
        Stock stock = new Stock();
        stock.setProductId(dto.getProductId());
        stock.setQuantity(dto.getQuantity());
        stock.setReservedQuantity(dto.getReservedQuantity() != null ? dto.getReservedQuantity() : 0);
        stock.setLocation(dto.getLocation());
        return stock;
    }
    
    public StockResponseDTO toResponseDTO(Stock stock) {
        StockResponseDTO dto = new StockResponseDTO();
        dto.setId(stock.getId());
        dto.setProductId(stock.getProductId());
        dto.setQuantity(stock.getQuantity());
        dto.setReservedQuantity(stock.getReservedQuantity());
        dto.setAvailableQuantity(stock.getAvailableQuantity());
        dto.setLocation(stock.getLocation());
        return dto;
    }
    
    public void updateEntityFromDTO(Stock stock, StockRequestDTO dto) {
        if (dto.getQuantity() != null) {
            stock.setQuantity(dto.getQuantity());
        }
        if (dto.getReservedQuantity() != null) {
            stock.setReservedQuantity(dto.getReservedQuantity());
        }
        if (dto.getLocation() != null) {
            stock.setLocation(dto.getLocation());
        }
    }
}

