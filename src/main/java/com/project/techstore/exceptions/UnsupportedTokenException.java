package com.project.techstore.exceptions;

public class UnsupportedTokenException extends RuntimeException {
    public UnsupportedTokenException(String message) {
        super(message);
    }
    
    public UnsupportedTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
