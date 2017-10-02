package com.github.dambaron.bank.model.operation;

import com.github.dambaron.bank.exception.OperationException;
import com.github.dambaron.bank.model.Account;
import org.junit.Test;

import java.util.Currency;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BalanceOperationTest {

    private static final Currency EURO_CURRENCY = Currency.getInstance("EUR");
    private static final Currency USD_CURRENCY = Currency.getInstance("USD");

    @Test
    public void test_checkApplicability_currency_mismatch() {
        //given
        Account account = new Account.Builder(UUID.randomUUID().toString(), EURO_CURRENCY).build();

        BalanceOperation balanceOperation =
                new BalanceOperation.Builder(account.getId(), OperationType.DEPOSIT, USD_CURRENCY).build();

        //when
        //checking the operation applicability on the account

        //then
        //expect an OperationException to be thrown
        assertThatThrownBy(() -> balanceOperation.checkApplicability(account))
                .isInstanceOf(OperationException.class)
                .hasMessage("operation currency and account currency must match");
    }
}