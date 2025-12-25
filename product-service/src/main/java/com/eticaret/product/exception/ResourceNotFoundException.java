package com.eticaret.product.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resource, String id) {
        super(String.format("%s bulunamadı: ID=%s", resource, id));
    }
}

