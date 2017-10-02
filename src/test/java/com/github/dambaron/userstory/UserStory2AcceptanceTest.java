package com.github.dambaron.userstory;

import com.github.dambaron.jgiven.tags.FeatureWithdrawal;
import com.github.dambaron.jgiven.tags.Story;
import com.github.dambaron.userstory.steps.GivenAccount;
import com.github.dambaron.userstory.steps.ThenAccount;
import com.github.dambaron.userstory.steps.WhenAccount;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;

@Story({"User story #2"})
@FeatureWithdrawal
public class UserStory2AcceptanceTest extends ScenarioTest<GivenAccount, WhenAccount, ThenAccount> {

    private static final Currency EURO_CURRENCY = Currency.getInstance("EUR");
    private static final Currency USD_CURRENCY = Currency.getInstance("USD");

    @Test
    public void a_withdrawal_cannot_be_applied_to_an_account_with_negative_balance_and_no_overdraft_allowed() {

        given().an_account_with_$_currency(EURO_CURRENCY)
                .and().the_balance_is_$(BigDecimal.ONE.negate())
                .and().the_allowed_overdraft_is_$(BigDecimal.ZERO);

        when().I_do_a_$_$_withdrawal(BigDecimal.ONE, EURO_CURRENCY);

        then().an_error_should_be_thrown_with_message("Negative account balance after operation")
                .and().no_statement_should_be_added();
    }

    @Test
    public void a_withdrawal_cannot_be_applied_past_the_allowed_overdraft() {

        BigDecimal overdraft = new BigDecimal(350);

        given().an_account_with_$_currency(EURO_CURRENCY)
                .and().the_balance_is_$(BigDecimal.ZERO)
                .and().the_allowed_overdraft_is_$(overdraft);

        BigDecimal amount = overdraft.add(BigDecimal.ONE);
        when().I_do_a_$_$_withdrawal(amount, EURO_CURRENCY);

        then().an_error_should_be_thrown_with_message("Account balance is lower than allowed overdraft after operation")
                .and().no_statement_should_be_added();
    }

    @Test
    public void a_withdrawal_can_be_applied_to_an_account_with_negative_balance_when_overdraft_not_reached() {

        BigDecimal startBalance = BigDecimal.ONE.negate();
        BigDecimal overdraft = new BigDecimal(350);

        given().an_account_with_$_currency(EURO_CURRENCY)
                .and().the_balance_is_$(startBalance)
                .and().the_allowed_overdraft_is_$(overdraft);

        when().I_do_a_$_$_withdrawal(BigDecimal.ONE, EURO_CURRENCY);

        //WithdrawalOperation withdrawalOperation = new WithdrawalOperation("accountId", EURO_CURRENCY, );

        //Statement expectedStatement = new Statement.Builder("accountId", -1, )
        then().account_should_have_$_statements(1)
                .and().the_account_balance_should_be_$(startBalance.subtract(BigDecimal.ONE));
    }

    @Test
    public void a_withdrawal_cannot_be_applied_to_an_account_with_a_different_currency() {

        given().an_account_with_$_currency(EURO_CURRENCY);

        when().I_do_a_$_$_withdrawal(BigDecimal.ONE, USD_CURRENCY);

        then().an_error_should_be_thrown_with_message("operation currency and account currency must match")
                .and().no_statement_should_be_added();
    }
}
