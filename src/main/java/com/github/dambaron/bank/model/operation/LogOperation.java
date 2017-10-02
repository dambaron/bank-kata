package com.github.dambaron.bank.model.operation;

import com.github.dambaron.bank.exception.OperationException;
import com.github.dambaron.bank.model.Account;
import com.github.dambaron.bank.validation.LogOperationValidator;
import com.github.dambaron.bank.validation.ValidationResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

public class LogOperation extends Operation {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogOperation.class);

    private static final LogOperationValidator VALIDATOR = new LogOperationValidator();

    private final Consumer<Account> logConsumer = account -> {
        checkNotNull(account, "account must not be null");

        boolean hasMatchingAccountIds = StringUtils.equals(this.getAccountId(), account.getId());
        if (!hasMatchingAccountIds) {
            LOGGER.error("Current account id is {} but target account id is {}", account.getId(), this.getAccountId());
            throw new OperationException("Account id mismatch");
        }

        ValidationResult<LogOperation> validationResult = VALIDATOR.validate(this);
        if (validationResult.hasErrors()) {
            validationResult.getErrors().forEach(error -> LOGGER.error("{}", error));
            throw new OperationException();
        }
    };

    public LogOperation(String accountId) {
        super(new Builder(accountId, OperationType.LOG));
        this.consumer = logConsumer;
    }

    @Override
    public String toString() {
        return "LogOperation{" +
                "id='" + id + '\'' +
                ", accountId='" + accountId + '\'' +
                ", type=" + type +
                '}';
    }
}
