package com.smartinstitute.erp.common.exception;

/**
 * Thrown when client sends an invalid request.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}