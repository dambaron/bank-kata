package com.github.dambaron.bank.model.operation;

import com.github.dambaron.bank.exception.OperationException;
import com.github.dambaron.bank.model.Account;
import com.github.dambaron.bank.model.Statement;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.fail;

public class OperationTest {

    private static final String DEFAULT_ACCOUNT_ID = "defaultAccountId";
    private static final String DEFAULT_OWNER_ID = "defaultOwnerId";

    private static final BigDecimal DEFAULT_ACCOUNT_BALANCE = new BigDecimal(1000);
    private static final BigDecimal DEFAULT_OPERATION_AMOUNT = new BigDecimal(100);

    private static final Currency EURO_CURRENCY = Currency.getInstance("EUR");

    @Test
    public void test_checkApplicability_null_account() {
        //given
        Operation operation = new Operation.Builder(DEFAULT_ACCOUNT_ID, OperationType.DEPOSIT).build();

        //when
        //checking applicability on null account

        //then
        assertThatThrownBy(() -> operation.checkApplicability(null))
                .isInstanceOf(OperationException.class)
                .hasMessage("account must not be null");
    }

    @Test
    public void test_checkApplicability_account_with_no_statements() {
        //given
        Operation operation = new Operation.Builder(DEFAULT_ACCOUNT_ID, OperationType.DEPOSIT).build();

        Account account = new Account.Builder(DEFAULT_OWNER_ID, EURO_CURRENCY).build();

        //when
        //statements are null
        account.setStatements(null);

        //then
        //expect no Exception to be thrown
        operation.checkApplicability(account);

        //when
        //statements are empty
        account.setStatements(new ArrayList<>());

        //then
        //expect no Exception to be thrown
        operation.checkApplicability(account);
    }

    @Test
    public void test_checkApplicability_operation_already_in_account_statements() {
        //given
        DepositOperation depositOperation =
                new DepositOperation(DEFAULT_ACCOUNT_ID, EURO_CURRENCY, DEFAULT_OPERATION_AMOUNT);

        Account account = new Account.Builder(DEFAULT_OWNER_ID, EURO_CURRENCY).build();

        String accountId = account.getId();

        Statement statement = new Statement.Builder(accountId, DEFAULT_ACCOUNT_BALANCE, depositOperation).build();

        account.setStatements(Collections.singletonList(statement));

        //when

        //then
        String expectedErrorMessage =
                "operation " + depositOperation.getId() + " has already been applied to the account";

        assertThatThrownBy(() -> depositOperation.checkApplicability(account))
                .isInstanceOf(OperationException.class)
                .hasMessage(expectedErrorMessage);

    }

    @Test
    public void test_checkApplicability() {
        //given
        Account account = new Account.Builder(DEFAULT_OWNER_ID, EURO_CURRENCY)
                .statements(new ArrayList<>())
                .build();

        String accountId = account.getId();

        DepositOperation deposist =
                new DepositOperation(accountId, EURO_CURRENCY, DEFAULT_OPERATION_AMOUNT);

        WithdrawalOperation withdrawal =
                new WithdrawalOperation(accountId, EURO_CURRENCY, DEFAULT_OPERATION_AMOUNT);

        DepositOperation d1 = new DepositOperation(accountId, EURO_CURRENCY, DEFAULT_OPERATION_AMOUNT);
        DepositOperation d2 = new DepositOperation(accountId, EURO_CURRENCY, DEFAULT_OPERATION_AMOUNT);

        WithdrawalOperation w1 = new WithdrawalOperation(accountId, EURO_CURRENCY, DEFAULT_OPERATION_AMOUNT);
        WithdrawalOperation w2 = new WithdrawalOperation(accountId, EURO_CURRENCY, DEFAULT_OPERATION_AMOUNT);

        List<BalanceOperation> operationsInStatements = Arrays.asList(d1, d2, w1, w2);
        Collections.shuffle(operationsInStatements);

        operationsInStatements.forEach(balanceOperation -> {
            Statement statement = new Statement.Builder(accountId, DEFAULT_ACCOUNT_BALANCE, balanceOperation)
                    .build();

            account.getStatements().add(statement);
        });

        //when
        deposist.checkApplicability(account);
        withdrawal.checkApplicability(account);

        //then
        //expect no exception to be thrown
    }

    @Test
    public void test_actUpon() {
        //given
        Account account = new Account.Builder(DEFAULT_OWNER_ID, EURO_CURRENCY)
                .balance(DEFAULT_ACCOUNT_BALANCE)
                .statements(new ArrayList<>())
                .build();

        String accountId = account.getId();

        DepositOperation d1 = new DepositOperation(accountId, EURO_CURRENCY, new BigDecimal(1));
        WithdrawalOperation w1 = new WithdrawalOperation(accountId, EURO_CURRENCY, new BigDecimal(2));
        DepositOperation d2 = new DepositOperation(accountId, EURO_CURRENCY, new BigDecimal(3));
        WithdrawalOperation w2 = new WithdrawalOperation(accountId, EURO_CURRENCY, new BigDecimal(4));

        //when
        try {
            d1.actUpon(account);
            Thread.sleep(250);

            w1.actUpon(account);
            Thread.sleep(250);

            d2.actUpon(account);
            Thread.sleep(250);

            w2.actUpon(account);
            Thread.sleep(250);
        } catch (InterruptedException ie) {
            fail(ie.getMessage());
        }

        //then
        BigDecimal expectedBalance = DEFAULT_ACCOUNT_BALANCE
                .add(d1.getAmount())
                .add(d2.getAmount())
                .subtract(w1.getAmount())
                .subtract(w2.getAmount());

        assertThat(account).isNotNull();
        assertThat(account.getBalance()).isEqualTo(expectedBalance);
        assertThat(account.getStatements()).hasSize(4);

        account.getStatements().forEach(statement -> {
            assertThat(statement.getId()).isNotNull().isNotBlank();
            assertThat(statement.getInstant()).isNotNull();
            assertThat(statement.getAccountId()).isEqualTo(accountId);
        });

        Statement s1 = account.getStatements().get(0);
        assertThat(s1.getOperation()).isEqualToComparingFieldByField(d1);
        assertThat(s1.getStartBalance()).isEqualTo(DEFAULT_ACCOUNT_BALANCE);
        assertThat(s1.getEndBalance()).isEqualTo(DEFAULT_ACCOUNT_BALANCE.add(d1.getAmount()));

        Statement s2 = account.getStatements().get(1);
        assertThat(s2.getInstant()).isAfter(s1.getInstant());
        assertThat(s2.getOperation()).isEqualToComparingFieldByField(w1);
        assertThat(s2.getStartBalance()).isEqualTo(s1.getEndBalance());
        assertThat(s2.getEndBalance()).isEqualTo(s1.getEndBalance().subtract(w1.getAmount()));

        Statement s3 = account.getStatements().get(2);
        assertThat(s3.getInstant()).isAfter(s2.getInstant());
        assertThat(s3.getOperation()).isEqualToComparingFieldByField(d2);
        assertThat(s3.getStartBalance()).isEqualTo(s2.getEndBalance());
        assertThat(s3.getEndBalance()).isEqualTo(s2.getEndBalance().add(d2.getAmount()));

        Statement s4 = account.getStatements().get(3);
        assertThat(s4.getInstant()).isAfter(s3.getInstant());
        assertThat(s4.getOperation()).isEqualToComparingFieldByField(w2);
        assertThat(s4.getStartBalance()).isEqualTo(s3.getEndBalance());
        assertThat(s4.getEndBalance()).isEqualTo(s3.getEndBalance().subtract(w2.getAmount()));
    }
}