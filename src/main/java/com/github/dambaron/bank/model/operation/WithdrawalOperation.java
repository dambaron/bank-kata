package com.github.dambaron.bank.model.operation;

import com.github.dambaron.bank.exception.OperationException;
import com.github.dambaron.bank.model.Account;
import com.github.dambaron.bank.model.Statement;
import com.github.dambaron.bank.validation.ValidationResult;
import com.github.dambaron.bank.validation.WithdrawalOperationValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

public class WithdrawalOperation extends BalanceOperation {

    private static final Logger LOGGER = LoggerFactory.getLogger(WithdrawalOperation.class);

    private static final WithdrawalOperationValidator VALIDATOR = new WithdrawalOperationValidator();

    private final Consumer<Account> withdrawalConsumer = account -> {
        checkNotNull(account);

        boolean hasMatchingAccountIds = StringUtils.equals(this.getAccountId(), account.getId());
        if (!hasMatchingAccountIds) {
            LOGGER.error("Current account id is {} but target account id is {}", account.getId(), this.getAccountId());
            throw new OperationException("Account id mismatch");
        }

        BigDecimal startBalance = account.getBalance();
        if (startBalance == null) {
            throw new OperationException("Account balance is not initialized");
        }

        ValidationResult<WithdrawalOperation> validationResult = VALIDATOR.validate(this);
        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getErrors();
            errors.forEach(error -> LOGGER.error("{}", error));
            throw new OperationException("Validation errors: " + errors);
        }

        BigDecimal endBalance = startBalance.subtract(this.getAmount());

        BigDecimal overdraft = Optional.ofNullable(account.getOverdraft()).orElse(BigDecimal.ZERO);
        if (overdraft.equals(BigDecimal.ZERO) && endBalance.signum() < 0) {
            LOGGER.error("Negative account balance after operation");
            throw new OperationException("Negative account balance after operation");
        }

        if (BigDecimal.ZERO.compareTo(overdraft) < 0 && endBalance.compareTo(overdraft.negate()) < 0) {
            LOGGER.error("Account balance is lower than allowed overdraft after operation");
            throw new OperationException("Account balance is lower than allowed overdraft after operation");
        }

        account.setBalance(endBalance);

        Statement statement = new Statement.Builder(account.getId(), startBalance, this)
                .endBalance(endBalance)
                .instant(Instant.now())
                .build();

        account.getStatements().add(statement);
        account.setLastOperationId(id);
    };

    public WithdrawalOperation(String accountId, Currency currency, BigDecimal amount) {
        super(new Builder(accountId, OperationType.WITHDRAWAL, currency).amount(amount));
        this.consumer = withdrawalConsumer;
    }

    @Override
    public String toString() {
        return "WithdrawalOperation{" +
                "amount=" + amount +
                ", currency=" + currency +
                ", id='" + id + '\'' +
                ", accountId='" + accountId + '\'' +
                ", type=" + type +
                '}';
    }
}
