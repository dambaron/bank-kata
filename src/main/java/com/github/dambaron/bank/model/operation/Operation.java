package com.github.dambaron.bank.model.operation;

import com.github.dambaron.bank.exception.OperationException;
import com.github.dambaron.bank.model.Account;
import com.github.dambaron.bank.model.Statement;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

public class Operation {

    protected final String id = UUID.randomUUID().toString();
    protected final String accountId;
    protected final OperationType type;

    protected Consumer<Account> consumer;
    protected String previousOperationId;

    protected Operation(Builder builder) {
        this.accountId = builder.accountId;
        this.type = builder.type;
        this.consumer = builder.consumer;
        this.previousOperationId = builder.previousOperationId;
    }

    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public OperationType getType() {
        return type;
    }

    public Consumer<Account> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<Account> consumer) {
        this.consumer = consumer;
    }

    public String getPreviousOperationId() {
        return previousOperationId;
    }

    public void setPreviousOperationId(String previousOperationId) {
        this.previousOperationId = previousOperationId;
    }

    public void actUpon(Account account) {
        this.getConsumer().accept(account);
    }

    public void checkApplicability(Account account) {
        if (account == null) {
            throw new OperationException("account must not be null");
        }

        if (StringUtils.equals(id, account.getLastOperationId())) {
            throw new OperationException("operation " + id + " is the last operation applied to the account");
        }

        if (account.getStatements() != null) {
            boolean isAlreadyApplied = account.getStatements().stream()
                    .filter(Objects::nonNull)
                    .map(Statement::getOperation)
                    .filter(Objects::nonNull)
                    .map(Operation::getId)
                    .anyMatch(operationId -> StringUtils.equals(operationId, id));

            if (isAlreadyApplied) {
                throw new OperationException("operation " + id + " has already been applied to the account");
            }
        }
    }

    public static class Builder {

        private final String accountId;
        private final OperationType type;

        private Consumer<Account> consumer;
        private String previousOperationId;

        public Builder(String accountId, OperationType type) {
            checkNotNull(accountId, "accountId must not be null");
            checkNotNull(type, "operationType must not be null");

            this.accountId = accountId;
            this.type = type;
        }

        public Builder consumer(Consumer<Account> consumer) {
            this.consumer = consumer;
            return this;
        }

        public Builder previousOperationId(String previousOperationId) {
            this.previousOperationId = previousOperationId;
            return this;
        }

        public Operation build() {
            return new Operation(this);
        }
    }

    @Override
    public String toString() {
        return "Operation{" +
                "id='" + id + '\'' +
                ", accountId='" + accountId + '\'' +
                ", type=" + type + '\'' +
                ", previousOperationId=" + previousOperationId + '\'' +
                '}';
    }
}
