package com.system.hakeem.Exceptions;

/**
 * Custom runtime exception for resource not found errors (404 status code)
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
