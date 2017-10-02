package com.github.dambaron.bank.model.operation;

import com.github.dambaron.bank.exception.OperationException;
import com.github.dambaron.bank.model.Account;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

public class BalanceOperation extends Operation {

    protected final BigDecimal amount;
    protected final Currency currency;

    protected BalanceOperation(Builder builder) {
        super(builder);
        this.amount = builder.amount;
        this.currency = builder.currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public static class Builder extends Operation.Builder {

        private final Currency currency;
        private BigDecimal amount = new BigDecimal(0);

        public Builder(String accountId, OperationType operationType, Currency currency) {
            super(accountId, operationType);

            checkNotNull(currency, "currency must not be null");
            this.currency = currency;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder consumer(Consumer<Account> consumer) {
            super.consumer(consumer);
            return this;
        }

        public BalanceOperation build() {
            return new BalanceOperation(this);
        }
    }

    @Override
    public void checkApplicability(Account account) {
        super.checkApplicability(account);

        if (!currency.equals(account.getCurrency())) {
            throw new OperationException("operation currency and account currency must match");
        }
    }

    @Override
    public String toString() {
        return "BalanceOperation{" +
                "amount=" + amount +
                ", currency=" + currency +
                ", id='" + id + '\'' +
                ", accountId='" + accountId + '\'' +
                ", type=" + type +
                '}';
    }
}
