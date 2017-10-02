package com.github.dambaron.bank.model.operation;

import com.github.dambaron.bank.exception.OperationException;
import com.github.dambaron.bank.model.Account;
import com.github.dambaron.bank.model.Statement;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DepositOperationTest {

    private static final BigDecimal DEFAULT_ACCOUNT_BALANCE = new BigDecimal(1000);
    private static final BigDecimal DEFAULT_DEPOSIT_AMOUNT = new BigDecimal(100);

    private static final String DEFAULT_OWNER_ID = "defaultOwnerId";
    private static final Currency EURO_CURRENCY = Currency.getInstance("EUR");

    @Test
    public void test_actUpon_should_throw_exception_on_account_id_mismatch() {
        //given
        Account account = new Account.Builder(DEFAULT_OWNER_ID, EURO_CURRENCY)
                .balance(DEFAULT_ACCOUNT_BALANCE)
                .statements(new ArrayList<>())
                .build();

        String accountId = account.getId();
        String randomAccountId = UUID.randomUUID().toString();

        DepositOperation depositOperation =
                new DepositOperation(randomAccountId, EURO_CURRENCY, DEFAULT_DEPOSIT_AMOUNT);

        //when
        //act upon the account

        //then
        assertThatThrownBy(() -> depositOperation.actUpon(account))
                .isInstanceOf(OperationException.class)
                .hasMessage("Account id mismatch");

        assertThat(account).isNotNull();
        assertThat(account.getId()).isEqualTo(accountId);
        assertThat(account.getBalance()).isEqualTo(DEFAULT_ACCOUNT_BALANCE);
        assertThat(account.getStatements()).isEmpty();
    }

    @Test
    public void test_actUpon_should_throw_exception_on_null_start_balance() {
        //given
        Account account = new Account.Builder(DEFAULT_OWNER_ID, EURO_CURRENCY)
                .balance(null)
                .statements(new ArrayList<>())
                .build();

        String accountId = account.getId();

        DepositOperation depositOperation =
                new DepositOperation(accountId, EURO_CURRENCY, DEFAULT_DEPOSIT_AMOUNT);

        //when
        //act upon the account

        //then
        //expect an OperationException to be thrown
        assertThatThrownBy(() -> depositOperation.actUpon(account))
                .isInstanceOf(OperationException.class)
                .hasMessage("Account balance is not initialized");

        assertThat(account).isNotNull();
        assertThat(account.getId()).isEqualTo(accountId);
        assertThat(account.getBalance()).isNull();
        assertThat(account.getStatements()).isEmpty();
    }

    @Test
    public void test_actUpon() {
        //given
        Account account = new Account.Builder(DEFAULT_OWNER_ID, EURO_CURRENCY)
                .balance(DEFAULT_ACCOUNT_BALANCE)
                .statements(new ArrayList<>())
                .build();

        String accountId = account.getId();

        DepositOperation depositOperation =
                new DepositOperation(accountId, EURO_CURRENCY, DEFAULT_DEPOSIT_AMOUNT);

        //when
        depositOperation.actUpon(account);

        //then
        assertThat(account).isNotNull();
        assertThat(account.getBalance()).isEqualTo(DEFAULT_ACCOUNT_BALANCE.add(DEFAULT_DEPOSIT_AMOUNT));
        assertThat(account.getStatements()).hasSize(1);

        Statement statement = account.getStatements().get(0);
        assertThat(statement.getId()).isNotNull().isNotBlank();
        assertThat(statement.getInstant()).isNotNull();
        assertThat(statement.getAccountId()).isEqualTo(accountId);
        assertThat(statement.getOperation()).isEqualToComparingFieldByField(depositOperation);
        assertThat(statement.getStartBalance()).isEqualTo(DEFAULT_ACCOUNT_BALANCE);
        assertThat(statement.getEndBalance()).isEqualTo(DEFAULT_ACCOUNT_BALANCE.add(DEFAULT_DEPOSIT_AMOUNT));
    }
}