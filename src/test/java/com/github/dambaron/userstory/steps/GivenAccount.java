package com.github.dambaron.userstory.steps;

import com.github.dambaron.bank.model.Account;
import com.github.dambaron.jgiven.format.CurrencyFormatter;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public class GivenAccount extends Stage<GivenAccount> {

    @ProvidedScenarioState
    private Account account;

    @ExtendedDescription("A bank account")
    public GivenAccount an_account_with_$_currency(@Format(value = CurrencyFormatter.class) Currency currency) {
        account = new Account.Builder(UUID.randomUUID().toString(), currency).build();
        return this;
    }

    public GivenAccount the_allowed_overdraft_is_$(BigDecimal overdraft) {

        account.setOverdraft(overdraft);
        return this;
    }

    public GivenAccount the_balance_is_$(BigDecimal balance) {

        account.setBalance(balance);
        return this;
    }
}
