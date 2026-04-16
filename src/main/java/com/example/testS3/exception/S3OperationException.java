package com.example.testS3.exception;

/**
 * Exceção lançada quando ocorre um erro durante operações com o S3.
 */
public class S3OperationException extends RuntimeException {

    public S3OperationException(String message) {
        super(message);
    }

    public S3OperationException(String message, Throwable cause) {
        super(message, cause);
    }
}

