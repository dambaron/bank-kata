package com.github.dambaron.userstory.steps;

import com.github.dambaron.bank.exception.OperationException;
import com.github.dambaron.bank.model.Account;
import com.github.dambaron.bank.model.operation.*;
import com.github.dambaron.jgiven.format.CurrencyFormatter;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

import java.math.BigDecimal;
import java.util.Currency;

public class WhenAccount extends Stage<WhenAccount> {

    @ExpectedScenarioState
    private Account account;

    @ProvidedScenarioState
    private OperationException expectedOperationException;

    @Hidden
    public WhenAccount I_do_a_$_$_balance_operation(BalanceOperation operation) {
        try {
            account.apply(operation);
        } catch (OperationException oe) {
            expectedOperationException = oe;
        }
        return self();
    }

    public WhenAccount I_do_a_$_$_withdrawal(BigDecimal amount,
                                             @Format(value = CurrencyFormatter.class) Currency currency) {

        WithdrawalOperation withdrawalOperation = new WithdrawalOperation(account.getId(), currency, amount);
        return I_do_a_$_$_balance_operation(withdrawalOperation);
    }

    public WhenAccount I_do_a_$_$_deposit(BigDecimal amount,
                                          @Format(value = CurrencyFormatter.class) Currency currency) {

        DepositOperation depositOperation = new DepositOperation(account.getId(), currency, amount);
        return I_do_a_$_$_balance_operation(depositOperation);
    }
}
