package com.project.techstore.exceptions;

public class MalformedTokenException extends RuntimeException {
    public MalformedTokenException(String message) {
        super(message);
    }
    
    public MalformedTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
