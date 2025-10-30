package com.project.techstore.exceptions;

public class TokenSignatureException extends RuntimeException {
    public TokenSignatureException(String message) {
        super(message);
    }
    
    public TokenSignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
