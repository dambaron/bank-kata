package com.github.dambaron.bank.model.operation;

import com.github.dambaron.bank.exception.OperationException;
import com.github.dambaron.bank.model.Account;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LogOperationTest {

    private static final BigDecimal DEFAULT_ACCOUNT_BALANCE = new BigDecimal(1000);

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

        LogOperation logOperation = new LogOperation(randomAccountId);

        //when
        //act upon the account

        //then
        assertThatThrownBy(() -> logOperation.actUpon(account))
                .isInstanceOf(OperationException.class)
                .hasMessage("Account id mismatch");

        assertThat(account).isNotNull();
        assertThat(account.getId()).isEqualTo(accountId);
        assertThat(account.getBalance()).isEqualTo(DEFAULT_ACCOUNT_BALANCE);
        assertThat(account.getStatements()).isEmpty();
    }
}