package com.github.dambaron.bank.model;

import com.github.dambaron.bank.exception.OperationException;
import com.github.dambaron.bank.model.operation.BalanceOperation;
import com.github.dambaron.bank.model.operation.DepositOperation;
import com.github.dambaron.bank.model.operation.Operation;
import com.github.dambaron.bank.model.operation.WithdrawalOperation;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AccountTest {

    private static final BigDecimal DEFAULT_ACCOUNT_BALANCE = new BigDecimal(100);

    private static final Currency EURO_CURRENCY = Currency.getInstance("EUR");
    private static final Currency USD_CURRENCY = Currency.getInstance("USD");

    @Test
    public void test_apply_null_operation() {
        //given
        Account account = new Account.Builder("ownerId", EURO_CURRENCY)
                .balance(new BigDecimal(0))
                .overdraft(new BigDecimal(0))
                .build();

        //when
        //applying a null operation

        //then
        //expect a NullPointerException to be thrown
        assertThatThrownBy(() -> account.apply(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("operation must not be null");
    }

    @Test
    public void test_apply_withdrawal_on_overdrawn_account() {
        //given
        Account account = new Account.Builder("ownerId", EURO_CURRENCY)
                .balance(new BigDecimal(350).negate())
                .overdraft(new BigDecimal(350))
                .build();

        WithdrawalOperation withdrawalOperation = new WithdrawalOperation(account.getId(), EURO_CURRENCY, BigDecimal.ONE);

        //when
        //applying a withdrawal

        //then
        //expect an OperationException to be thrown
        assertThatThrownBy(() -> account.apply(withdrawalOperation))
                .isInstanceOf(OperationException.class)
                .hasMessage("Account balance is lower than allowed overdraft after operation");
    }

    @Test
    public void test_apply_withdrawal_on_zero_balance_with_overdraft() {
        //given
        BigDecimal allowedOverdraft = new BigDecimal(350);

        List<BigDecimal> withdrawalAmounts = Arrays.asList(allowedOverdraft.subtract(BigDecimal.ONE),
                allowedOverdraft,
                allowedOverdraft.add(BigDecimal.ONE));

        withdrawalAmounts.forEach(withdrawalAmount -> {

            Account account = new Account.Builder("ownerId", EURO_CURRENCY)
                    .balance(new BigDecimal(0))
                    .overdraft(allowedOverdraft)
                    .build();

            WithdrawalOperation withdrawalOperation =
                    new WithdrawalOperation(account.getId(), EURO_CURRENCY, withdrawalAmount);

            if (withdrawalAmount.compareTo(allowedOverdraft) <= 0) {
                //when
                account.apply(withdrawalOperation);

                //then

            } else {
                //when
                //applying a withdrawal

                //then
                //expect an OperationException to be thrown
                assertThatThrownBy(() -> account.apply(withdrawalOperation))
                        .isInstanceOf(OperationException.class)
                        .hasMessage("Account balance is lower than allowed overdraft after operation");
            }
        });
    }

    @Test
    public void test_apply_withdrawal_on_zero_balance_without_overdraft() {
        //given
        Account account = new Account.Builder("ownerId", EURO_CURRENCY)
                .balance(new BigDecimal(0))
                .overdraft(new BigDecimal(0))
                .build();

        WithdrawalOperation withdrawalOperation =
                new WithdrawalOperation(account.getId(), EURO_CURRENCY, new BigDecimal(1));

        //when
        //withdrawal operation is applied to account

        //then
        //expect an OperationException to be thrown
        assertThatThrownBy(() -> account.apply(withdrawalOperation))
                .isInstanceOf(OperationException.class)
                .hasMessage("Negative account balance after operation");
    }

    @Test
    public void test_apply_deposit() {
        //given
        List<BigDecimal> startBalances = Arrays.asList(BigDecimal.ONE.negate(),
                BigDecimal.ZERO,
                BigDecimal.ONE,
                new BigDecimal(1.234));

        List<BigDecimal> depositAmounts = Arrays.asList(new BigDecimal(2),
                new BigDecimal(3),
                new BigDecimal(4),
                new BigDecimal(2.456));


        for (int i = 0; i < startBalances.size() - 1; i++) {

            BigDecimal startBalance = startBalances.get(i);

            Account account = new Account.Builder("ownerId", EURO_CURRENCY)
                    .balance(startBalance)
                    .statements(new ArrayList<>())
                    .build();

            BigDecimal depositAmount = depositAmounts.get(i);
            DepositOperation depositOperation = new DepositOperation(account.getId(), EURO_CURRENCY, depositAmount);

            //when
            account.apply(depositOperation);

            //then
            assertThat(account.getBalance()).isEqualTo(startBalance.add(depositAmount));
            assertThat(account.getLastOperationId()).isEqualTo(depositOperation.getId());

            assertThat(account.getStatements()).hasSize(1);
            Statement statement = account.getStatements().get(0);
            assertThat(statement).isNotNull();
            assertThat(statement.getStartBalance()).isEqualTo(startBalance);
            assertThat(statement.getEndBalance()).isEqualTo(startBalance.add(depositAmount));
            assertThat(statement.getOperation()).isEqualTo(depositOperation);
        }
    }

    @Test
    public void test_apply_deposits_in_a_row() {
        //given
        BigDecimal startBalance = BigDecimal.ZERO;

        Account account = new Account.Builder("ownerId", EURO_CURRENCY)
                .balance(startBalance)
                .statements(new ArrayList<>())
                .build();

        List<DepositOperation> depositOperations = Arrays.asList(
                new DepositOperation(account.getId(), EURO_CURRENCY, new BigDecimal(11)),
                new DepositOperation(account.getId(), EURO_CURRENCY, new BigDecimal(2200)),
                new DepositOperation(account.getId(), EURO_CURRENCY, new BigDecimal(330000)),
                new DepositOperation(account.getId(), EURO_CURRENCY, new BigDecimal(44000000))
        );

        //when
        depositOperations.forEach(account::apply);

        //then

        //Computing grand total
        BigDecimal expectedFinalBalance = depositOperations.stream()
                .map(DepositOperation::getAmount)
                .reduce((x, y) -> x.add(y))
                .orElse(BigDecimal.ZERO);

        String expectedLastOperationId = depositOperations.get(depositOperations.size() - 1).getId();

        assertThat(account.getBalance()).isEqualTo(expectedFinalBalance);
        assertThat(account.getLastOperationId()).isEqualTo(expectedLastOperationId);

        assertThat(account.getStatements()).hasSize(depositOperations.size());

        for (int i = 0; i < depositOperations.size(); i++) {
            Statement statement = account.getStatements().get(i);
            DepositOperation expectedDepositOperation = depositOperations.get(i);
            Operation actualOperation = statement.getOperation();

            assertThat(statement).isNotNull();
            assertThat(actualOperation).isEqualTo(expectedDepositOperation);

            //the first statement must start with the account initial balance
            if (i == 0) {
                assertThat(statement.getStartBalance()).isEqualTo(startBalance);
            }

            if (i > 0) {
                Statement previousStatement = account.getStatements().get(i - 1);

                //the end balance of the previous statement must be the start balance of the next statement
                assertThat(statement.getStartBalance()).isEqualTo(previousStatement.getEndBalance());

                //the end balance must be the start balance plus the deposit amount
                BigDecimal expectedEndBalance =
                        previousStatement.getEndBalance().add(expectedDepositOperation.getAmount());
                assertThat(statement.getEndBalance()).isEqualTo(expectedEndBalance);
            }

            //the last statement balance must be the grand total
            if (i == depositOperations.size() - 1) {
                assertThat(statement.getEndBalance()).isEqualTo(expectedFinalBalance);
            }
        }
    }

    @Test
    public void test_apply_same_operation_twice() {
        //given
        Account account = new Account.Builder("ownerId", EURO_CURRENCY)
                .balance(DEFAULT_ACCOUNT_BALANCE)
                .overdraft(BigDecimal.ZERO)
                .statements(new ArrayList<>())
                .build();

        String accountId = account.getId();

        WithdrawalOperation withdrawalOperation = new WithdrawalOperation(accountId, EURO_CURRENCY, BigDecimal.ONE);
        DepositOperation depositOperation = new DepositOperation(accountId, EURO_CURRENCY, BigDecimal.ONE);

        List<BalanceOperation> balanceOperations = Arrays.asList(withdrawalOperation, depositOperation);

        balanceOperations.forEach(balanceOperation -> {
            //when
            account.apply(balanceOperation);

            //then
            //expect an OperationException to be thrown at second call
            assertThatThrownBy(() -> account.apply(balanceOperation))
                    .isInstanceOf(OperationException.class)
                    .hasMessage("operation " + balanceOperation.getId() + " is the last operation applied to the account");
        });

        assertThat(account).isNotNull();
        assertThat(account.getId()).isEqualTo(accountId);
        assertThat(account.getBalance()).isEqualTo(DEFAULT_ACCOUNT_BALANCE);
        assertThat(account.getStatements()).hasSize(2);

        List<Operation> appliedOperations = account.getStatements()
                .stream()
                .map(Statement::getOperation)
                .collect(Collectors.toList());

        assertThat(appliedOperations).hasSize(2).containsExactly(withdrawalOperation, depositOperation);
    }
}