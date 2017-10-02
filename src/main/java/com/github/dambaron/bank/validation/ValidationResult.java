package com.github.dambaron.bank.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult<T> {

    private T subject;

    private List<String> errors = new ArrayList<>();

    private ValidationResult() {
        //DO NOTHING
    }

    public ValidationResult(T subject) {
        this.subject = subject;
    }

    public T getSubject() {
        return subject;
    }

    public void setSubject(T subject) {
        this.subject = subject;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
