package com.system.hakeem.Exceptions;

/**
 * Custom runtime exception for bad request errors (400 status code)
 * Used for validation and business logic errors
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
