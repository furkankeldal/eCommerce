package com.eticaret.order.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
    
    public InsufficientStockException(String productName, Integer available, Integer requested) {
        super(String.format("Ürün '%s' için yetersiz stok: Mevcut=%d, İstenen=%d", productName, available, requested));
    }
}

