package com.ballistic.fserver.exception;

import org.springframework.validation.FieldError;

import java.util.List;

public class IllegalBeanFieldException extends RuntimeException {

    public IllegalBeanFieldException() {
    }

    public IllegalBeanFieldException(String message) {
        super(message);
    }

    public IllegalBeanFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalBeanFieldException(Throwable cause) {
        super(cause);
    }

    public IllegalBeanFieldException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
