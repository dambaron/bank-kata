package com.github.dambaron.bank.model;

import com.github.dambaron.bank.model.operation.BalanceOperation;
import com.github.dambaron.bank.model.operation.Operation;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Statement {
    private final String id = UUID.randomUUID().toString();
    private final String accountId;
    private final BigDecimal startBalance;

    private Instant instant;
    private BalanceOperation operation;
    private BigDecimal endBalance;

    private Statement(Builder builder) {
        this.accountId = builder.accountId;
        this.instant = builder.instant;
        this.operation = builder.operation;
        this.startBalance = builder.startBalance;
        this.endBalance = builder.endBalance;
    }

    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public Instant getInstant() {
        return instant;
    }

    public Operation getOperation() {
        return operation;
    }

    public BigDecimal getStartBalance() {
        return startBalance;
    }

    public BigDecimal getEndBalance() {
        return endBalance;
    }

    public static class Builder {

        private final String accountId;
        private final BalanceOperation operation;
        private final BigDecimal startBalance;

        private Instant instant;
        private BigDecimal endBalance;

        public Builder(String accountId, BigDecimal startBalance, BalanceOperation operation) {
            this.accountId = accountId;
            this.startBalance = startBalance;
            this.operation = operation;
        }

        public Builder instant(Instant instant) {
            this.instant = instant;
            return this;
        }

        public Builder endBalance(BigDecimal endBalance) {
            this.endBalance = endBalance;
            return this;
        }

        public Statement build() {
            return new Statement(this);
        }
    }
}
