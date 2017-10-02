package com.github.dambaron.bank.validation;

import com.github.dambaron.bank.model.operation.Operation;

public interface OperationValidator<T extends Operation> {

    ValidationResult<T> validate(T operation);
}
