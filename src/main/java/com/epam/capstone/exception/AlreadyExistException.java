package com.epam.capstone.exception;

/**
 * Thrown when entity with the same unique constraints found in the database.
 */
public class AlreadyExistException extends RuntimeException {
    public AlreadyExistException(String message) {
        super(message);
    }
}
