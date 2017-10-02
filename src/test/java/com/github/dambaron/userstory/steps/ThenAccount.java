package com.github.dambaron.userstory.steps;

import com.github.dambaron.bank.exception.OperationException;
import com.github.dambaron.bank.model.Account;
import com.github.dambaron.bank.model.Statement;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ThenAccount extends Stage<ThenAccount> {

    @ExpectedScenarioState
    private Account account;

    @ExpectedScenarioState
    private OperationException expectedOperationException;

    @As("the following error should be thrown: $")
    public ThenAccount an_error_should_be_thrown_with_message(String errorMessage) {

        assertThat(expectedOperationException).isNotNull();
        assertThat(expectedOperationException.getMessage()).isEqualTo(errorMessage);
        return self();
    }

    public ThenAccount the_account_balance_should_be_$(BigDecimal balance) {

        assertThat(account).isNotNull();
        assertThat(account.getBalance()).isEqualTo(balance);
        return self();
    }

    public ThenAccount no_statement_should_be_added() {

        assertThat(account).isNotNull();
        assertThat(account.getStatements()).hasSize(0);
        return self();
    }

    @As("account should have $ statement(s)")
    public ThenAccount account_should_have_$_statements(int statementSize) {
        assertThat(account).isNotNull();
        assertThat(account.getStatements()).hasSize(statementSize);
        return self();
    }

//    @As("$ statement(s) should be added")
//    public ThenAccount $_statement_should_be_added(int addedStatements) {
//
//        assertThat(account).isNotNull();
//        assertThat(account.getStatements()).isNotEmpty();
//
//        List<Statement> actualStatements = account.getStatements();
//
//        List<Statement> matchingStatements = actualStatements.stream()
//                .filter(actualStatement -> StringUtils.equals(actualStatement.getId(), expectedStatement.getId()))
//                .collect(Collectors.toList());
//
//        assertThat(matchingStatements).hasSize(1);
//        assertThat(matchingStatements.get(0)).isEqualToComparingFieldByField(expectedStatement);
//        return self();
//    }
}
