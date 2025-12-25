package com.eticaret.user.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
    
    public DuplicateResourceException(String resource, String value) {
        super(String.format("%s zaten mevcut: %s", resource, value));
    }
}

