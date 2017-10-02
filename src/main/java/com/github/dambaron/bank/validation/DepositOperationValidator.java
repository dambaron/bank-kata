package com.github.dambaron.bank.validation;

import com.github.dambaron.bank.model.operation.DepositOperation;
import com.github.dambaron.bank.model.operation.OperationType;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;

public class DepositOperationValidator implements OperationValidator<DepositOperation> {

    public boolean isValidOperationType(OperationType operationType) {
        return OperationType.DEPOSIT.equals(operationType);
    }

    public boolean isValidAmount(BigDecimal amount) {
        return amount != null && BigDecimal.ZERO.compareTo(amount) <= 0;
    }

    @Override
    public ValidationResult<DepositOperation> validate(DepositOperation operation) {
        checkNotNull(operation, "operation must not be null");

        ValidationResult<DepositOperation> validationResult = new ValidationResult<>(operation);
        boolean isValidOperationType = this.isValidOperationType(operation.getType());
        if (!isValidOperationType) {
            validationResult.getErrors().add("Wrong operation type");
        }

        boolean isValidAmount = this.isValidAmount(operation.getAmount());
        if (!isValidAmount) {
            validationResult.getErrors().add("Wrong operation amount");
        }
        return validationResult;
    }
}
