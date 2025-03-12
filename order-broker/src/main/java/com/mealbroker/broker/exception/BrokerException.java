package com.mealbroker.broker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a broker operation fails
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BrokerException extends RuntimeException {

    public BrokerException(String message) {
        super(message);
    }
}