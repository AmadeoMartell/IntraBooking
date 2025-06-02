package com.epam.capstone.exception;

/**
 * Thrown when an expected entity is not found in the database.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
