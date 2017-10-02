package com.github.dambaron.bank.validation;

import com.github.dambaron.bank.model.operation.OperationType;
import com.github.dambaron.bank.model.operation.WithdrawalOperation;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;

public class WithdrawalOperationValidator implements OperationValidator<WithdrawalOperation> {

    public boolean isValidOperationType(OperationType operationType) {
        return OperationType.WITHDRAWAL.equals(operationType);
    }

    public boolean isValidAmount(BigDecimal amount) {
        return amount != null && BigDecimal.ZERO.compareTo(amount) <= 0;
    }

    @Override
    public ValidationResult<WithdrawalOperation> validate(WithdrawalOperation operation) {
        checkNotNull(operation, "operation must not be null");

        ValidationResult<WithdrawalOperation> validationResult = new ValidationResult<>(operation);
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
