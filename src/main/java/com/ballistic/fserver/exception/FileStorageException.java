package com.ballistic.fserver.exception;


public class FileStorageException extends RuntimeException {

    public FileStorageException() { }

    public FileStorageException(String error, Exception ex) {
        super(error, ex);
    }

    public FileStorageException(String error) {
        super(error);
    }

    public FileStorageException(String message, Throwable cause) { super(message, cause); }

    public FileStorageException(Throwable cause) { super(cause); }

    public FileStorageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
