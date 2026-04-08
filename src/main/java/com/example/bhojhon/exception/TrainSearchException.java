package com.example.bhojhon.exception;

/**
 * Custom exception for train search validation errors.
 */
public class TrainSearchException extends Exception {
    
    public TrainSearchException(String message) {
        super(message);
    }

    public TrainSearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
