package com.eticaret.order.service;

import com.eticaret.order.model.Order;

import java.util.List;

public interface IOrderService {
    Order createOrder(Order order);
    Order getOrderById(Long id);
    List<Order> getOrdersByUserId(Long userId);
    List<Order> getAllOrders();
    Order updateOrderStatus(Long id, String status);
    void cancelOrder(Long id);
}

