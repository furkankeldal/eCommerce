package com.eticaret.stock.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resource, Long id) {
        super(String.format("%s bulunamadı: ID=%d", resource, id));
    }
    
    public ResourceNotFoundException(String resource, String productId) {
        super(String.format("%s bulunamadı: ProductID=%s", resource, productId));
    }
}

