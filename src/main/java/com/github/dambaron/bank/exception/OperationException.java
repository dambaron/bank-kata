package com.github.dambaron.bank.exception;

public class OperationException extends RuntimeException {

    public OperationException() {
        super();
    }

    public OperationException(String message) {
        super(message);
    }
}
