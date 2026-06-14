package com.vehicle.rental.g11.exception;

public class RentalSystemException extends Exception {

    // Basic: just a message
    public RentalSystemException(String message) {
        super(message);
    }

    // With cause: wraps another exception (e.g. SQLException)
    public RentalSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
