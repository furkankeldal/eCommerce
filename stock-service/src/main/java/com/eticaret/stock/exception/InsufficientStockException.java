package com.eticaret.stock.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
    
    public InsufficientStockException(Integer available, Integer requested) {
        super(String.format("Yetersiz stok: Mevcut=%d, İstenen=%d", available, requested));
    }
}

