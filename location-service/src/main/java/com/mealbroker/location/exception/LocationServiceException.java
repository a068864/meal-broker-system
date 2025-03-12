package com.mealbroker.location.exception;

/**
 * Exception thrown when a location service operation fails
 */
public class LocationServiceException extends RuntimeException {

    public LocationServiceException(String message) {
        super(message);
    }

    public LocationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}