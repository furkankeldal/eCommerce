package com.eticaret.order.mapper;

import com.eticaret.order.dto.OrderItemDTO;
import com.eticaret.order.dto.OrderRequestDTO;
import com.eticaret.order.dto.OrderResponseDTO;
import com.eticaret.order.model.Order;
import com.eticaret.order.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    
    public Order toEntity(OrderRequestDTO dto) {
        Order order = new Order();
        order.setUserId(dto.getUserId());
        order.setShippingAddress(dto.getShippingAddress());
        
        // OrderItemDTO'ları OrderItem'lara çevir
        if (dto.getItems() != null) {
            List<OrderItem> items = dto.getItems().stream()
                    .map(this::toOrderItem)
                    .collect(Collectors.toList());
            order.setItems(items);
        }
        
        return order;
    }
    
    public OrderResponseDTO toResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        
        // OrderItem'ları OrderItemDTO'lara çevir
        if (order.getItems() != null) {
            List<OrderItemDTO> items = order.getItems().stream()
                    .map(this::toOrderItemDTO)
                    .collect(Collectors.toList());
            dto.setItems(items);
        }
        
        return dto;
    }
    
    private OrderItem toOrderItem(OrderItemDTO dto) {
        OrderItem item = new OrderItem();
        item.setProductId(dto.getProductId());
        item.setProductName(dto.getProductName());
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());
        return item;
    }
    
    private OrderItemDTO toOrderItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        return dto;
    }
}

