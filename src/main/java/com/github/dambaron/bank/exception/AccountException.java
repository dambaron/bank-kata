package com.github.dambaron.bank.exception;

public class AccountException extends RuntimeException {
    public AccountException() {
        super();
    }

    public AccountException(String message) {
        super(message);
    }
}
