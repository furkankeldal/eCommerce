package com.eticaret.order.exception;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
    
    public ServiceUnavailableException(String service, String reason) {
        super(String.format("%s servisi kullanılamıyor: %s", service, reason));
    }
}

