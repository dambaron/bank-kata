package com.github.dambaron.userstory;

import com.github.dambaron.jgiven.tags.FeatureDeposit;
import com.github.dambaron.jgiven.tags.Story;
import com.github.dambaron.userstory.steps.GivenAccount;
import com.github.dambaron.userstory.steps.ThenAccount;
import com.github.dambaron.userstory.steps.WhenAccount;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;

@Story({"User story #1"})
@FeatureDeposit
public class UserStory1AcceptanceTest extends ScenarioTest<GivenAccount, WhenAccount, ThenAccount> {

    private static final Currency EURO_CURRENCY = Currency.getInstance("EUR");
    private static final Currency USD_CURRENCY = Currency.getInstance("USD");

    @Test
    public void a_deposit_cannot_be_applied_to_an_account_with_a_different_currency() {

        given().an_account_with_$_currency(EURO_CURRENCY);

        when().I_do_a_$_$_deposit(BigDecimal.ONE, USD_CURRENCY);

        then().an_error_should_be_thrown_with_message("operation currency and account currency must match")
                .and().no_statement_should_be_added();
    }

    @Test
    public void a_deposit_can_be_applied_to_an_account_with_negative_balance() {

        BigDecimal startBalance = BigDecimal.ONE.negate();
        BigDecimal depositAmount = new BigDecimal(2);

        given().an_account_with_$_currency(EURO_CURRENCY)
                .and().the_balance_is_$(startBalance);

        when().I_do_a_$_$_deposit(depositAmount, EURO_CURRENCY);

        then().account_should_have_$_statements(1)
                .and().the_account_balance_should_be_$(startBalance.add(depositAmount));
    }
}
