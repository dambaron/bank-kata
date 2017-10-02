package com.github.dambaron.bank.validation;

import com.github.dambaron.bank.model.operation.LogOperation;
import com.github.dambaron.bank.model.operation.OperationType;

import static com.google.common.base.Preconditions.checkNotNull;

public class LogOperationValidator implements OperationValidator<LogOperation> {

    public boolean isValidOperationType(OperationType operationType) {
        return OperationType.LOG.equals(operationType);
    }

    @Override
    public ValidationResult<LogOperation> validate(LogOperation operation) {
        checkNotNull(operation, "operation must not be null");

        ValidationResult<LogOperation> validationResult = new ValidationResult<>(operation);
        boolean isValidOperationType = this.isValidOperationType(operation.getType());
        if (!isValidOperationType) {
            validationResult.getErrors().add("Wrong operation type");
        }

        return validationResult;
    }
}
