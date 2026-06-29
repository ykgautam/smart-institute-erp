package com.smartinstitute.erp.common.exception;

public class InvalidRequestException
        extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }

}