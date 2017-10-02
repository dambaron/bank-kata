package com.github.dambaron.bank.model.operation;

import com.github.dambaron.bank.exception.OperationException;
import com.github.dambaron.bank.model.Account;
import com.github.dambaron.bank.model.Statement;
import com.github.dambaron.bank.validation.DepositOperationValidator;
import com.github.dambaron.bank.validation.ValidationResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

public class DepositOperation extends BalanceOperation {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepositOperation.class);

    private static final DepositOperationValidator VALIDATOR = new DepositOperationValidator();

    private final Consumer<Account> depositConsumer = account -> {
        checkNotNull(account, "account must not be null");

        boolean hasMatchingAccountIds = StringUtils.equals(this.getAccountId(), account.getId());
        if (!hasMatchingAccountIds) {
            LOGGER.error("Current account id is {} but target account id is {}", account.getId(), this.getAccountId());
            throw new OperationException("Account id mismatch");
        }

        BigDecimal startBalance = account.getBalance();
        if (startBalance == null) {
            throw new OperationException("Account balance is not initialized");
        }

        ValidationResult<DepositOperation> validationResult = VALIDATOR.validate(this);
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getErrors();
            errors.forEach(error -> LOGGER.error("{}", error));
            throw new OperationException("Validation errors: " + errors);
        }

        BigDecimal endBalance = startBalance.add(this.getAmount());

        account.setBalance(endBalance);

        Statement statement = new Statement.Builder(account.getId(), startBalance, this)
                .endBalance(endBalance)
                .instant(Instant.now())
                .build();

        account.getStatements().add(statement);
        account.setLastOperationId(id);
    };

    public DepositOperation(String accountId, Currency currency, BigDecimal amount) {
        super(new Builder(accountId, OperationType.DEPOSIT, currency)
                .amount(amount));
        this.consumer = depositConsumer;
    }

    @Override
    public String toString() {
        return "DepositOperation{" +
                "amount=" + amount +
                ", currency=" + currency +
                ", id='" + id + '\'' +
                ", accountId='" + accountId + '\'' +
                ", type=" + type +
                '}';
    }
}
