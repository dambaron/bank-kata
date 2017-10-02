package com.github.dambaron.bank.validation;

import com.github.dambaron.bank.model.operation.OperationType;
import com.github.dambaron.bank.model.operation.WithdrawalOperation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WithdrawalOperationValidatorTest {

    @Mock
    private WithdrawalOperation withdrawalOperationMock;

    @Test
    public void test_isValidOperationType_no_withdrawal_operation() {
        // given
        WithdrawalOperationValidator withdrawalValidator = new WithdrawalOperationValidator();

        Arrays.stream(OperationType.values())
                .filter(operationType -> !OperationType.WITHDRAWAL.equals(operationType))
                .forEach(operationType -> {

                    // when
                    // the operation type is NOT WITHDRAWAL
                    boolean isValidOperationType = withdrawalValidator.isValidOperationType(operationType);

                    // then
                    assertThat(isValidOperationType).isFalse();
                });
    }

    @Test
    public void test_isValidOperationType() {
        // given
        WithdrawalOperationValidator withdrawalValidator = new WithdrawalOperationValidator();

        // when
        // the operation type is WITHDRAWAL
        boolean isValidOperationType = withdrawalValidator.isValidOperationType(OperationType.WITHDRAWAL);

        // then
        assertThat(isValidOperationType).isTrue();
    }

    @Test
    public void test_isValidAmount_null_amount() {
        // given
        WithdrawalOperationValidator withdrawalValidator = new WithdrawalOperationValidator();

        // when
        // validating a null amount
        boolean isValidAmount = withdrawalValidator.isValidAmount(null);

        // then
        assertThat(isValidAmount).isFalse();
    }

    @Test
    public void test_isValidAmount_negative_amount() {
        // given
        WithdrawalOperationValidator withdrawalValidator = new WithdrawalOperationValidator();

        // when
        // validating a negative amount
        boolean isValidAmount = withdrawalValidator.isValidAmount(BigDecimal.ONE.negate());

        // then
        assertThat(isValidAmount).isFalse();
    }

    @Test
    public void test_isValidAmount() {
        // given
        WithdrawalOperationValidator withdrawalValidator = new WithdrawalOperationValidator();

        List<BigDecimal> amounts = Arrays.asList(BigDecimal.ZERO,
                BigDecimal.ONE,
                new BigDecimal(123456),
                new BigDecimal(123.456));

        amounts.forEach(amount -> {
            // when
            // validating amount
            boolean isValidAmount = withdrawalValidator.isValidAmount(amount);

            // then
            assertThat(isValidAmount).isTrue();
        });
    }

    @Test
    public void test_validate_null_operation() {
        // given
        WithdrawalOperationValidator withdrawalValidator = new WithdrawalOperationValidator();

        // when
        // operation is null

        // then
        // expect a NullPointerException to be thrown
        assertThatThrownBy(() -> withdrawalValidator.validate(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("operation must not be null");
    }

    @Test
    public void test_validate_wrong_operation_type() {
        // given
        WithdrawalOperationValidator withdrawalValidator = new WithdrawalOperationValidator();

        when(withdrawalOperationMock.getType()).thenReturn(OperationType.DEPOSIT);
        when(withdrawalOperationMock.getAmount()).thenReturn(BigDecimal.ZERO);

        // when
        ValidationResult<WithdrawalOperation> validationResult = withdrawalValidator.validate(withdrawalOperationMock);

        // then
        assertThat(validationResult.hasErrors()).isTrue();
        assertThat(validationResult.getErrors()).hasSize(1).containsExactly("Wrong operation type");
    }

    @Test
    public void test_validate_wrong_operation_amount() {
        // given
        WithdrawalOperationValidator withdrawalValidator = new WithdrawalOperationValidator();

        when(withdrawalOperationMock.getType()).thenReturn(OperationType.WITHDRAWAL);
        when(withdrawalOperationMock.getAmount()).thenReturn(BigDecimal.ONE.negate());

        // when
        ValidationResult<WithdrawalOperation> validationResult = withdrawalValidator.validate(withdrawalOperationMock);

        // then
        assertThat(validationResult.hasErrors()).isTrue();
        assertThat(validationResult.getErrors()).hasSize(1).containsExactly("Wrong operation amount");
    }
}