package com.github.dambaron.bank.model;

import com.github.dambaron.bank.model.operation.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class Account //implements Reportable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Account.class);

    private static final int MAX_LINE_LENGTH = 120;

    private final String id = UUID.randomUUID().toString();
    private final String ownerId;
    private final Currency currency;

    private String lastOperationId;
    private BigDecimal balance = new BigDecimal(0);
    private BigDecimal overdraft = new BigDecimal(0);

    private List<Statement> statements = new ArrayList<>();

    public Account(Builder builder) {
        this.ownerId = builder.ownerId;
        this.currency = builder.currency;
        this.balance = builder.balance;
        this.overdraft = builder.overdraft;
        this.statements = builder.statements;
        this.lastOperationId = builder.lastOperationId;
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getOverdraft() {
        return overdraft;
    }

    public void setOverdraft(BigDecimal overdraft) {
        this.overdraft = overdraft;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    public String getLastOperationId() {
        return lastOperationId;
    }

    public void setLastOperationId(String lastOperationId) {
        this.lastOperationId = lastOperationId;
    }

    public Account apply(Operation operation) {
        checkNotNull(operation, "operation must not be null");

        operation.checkApplicability(this);

        operation.actUpon(this);

        return this;
    }

    public static class Builder {

        private final String ownerId;
        private final Currency currency;

        private BigDecimal balance = new BigDecimal(0);
        private BigDecimal overdraft = new BigDecimal(0);

        private List<Statement> statements = new ArrayList<>();
        private String lastOperationId;

        public Builder(String ownerId, Currency currency) {

            checkNotNull(ownerId, "ownerId must not be null");
            checkNotNull(currency, "currency must not be null");

            this.ownerId = ownerId;
            this.currency = currency;
        }

        public Builder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public Builder overdraft(BigDecimal overdraft) {
            this.overdraft = overdraft;
            return this;
        }

        public Builder statements(List<Statement> statements) {
            this.statements = statements;
            return this;
        }

        public Builder lastOperationId(String lastOperationId) {
            this.lastOperationId = lastOperationId;
            return this;
        }

        public Account build() {
            return new Account(this);
        }
    }

}
