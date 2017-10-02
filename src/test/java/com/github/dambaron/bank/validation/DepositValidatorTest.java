package com.github.dambaron.bank.validation;

import com.github.dambaron.bank.model.operation.DepositOperation;
import com.github.dambaron.bank.model.operation.OperationType;
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
public class DepositValidatorTest {

    @Mock
    private DepositOperation depositOperationMock;

    @Test
    public void test_isValidOperationType_no_deposit_operation() {
        // given
        DepositOperationValidator depositValidator = new DepositOperationValidator();

        Arrays.stream(OperationType.values())
                .filter(operationType -> !OperationType.DEPOSIT.equals(operationType))
                .forEach(operationType -> {

                    // when
                    // the operation type is NOT DEPOSIT
                    boolean isValidOperationType = depositValidator.isValidOperationType(operationType);

                    // then
                    assertThat(isValidOperationType).isFalse();
                });
    }

    @Test
    public void test_isValidOperationType() {
        // given
        DepositOperationValidator depositValidator = new DepositOperationValidator();

        // when
        // the operation type is DEPOSIT
        boolean isValidOperationType = depositValidator.isValidOperationType(OperationType.DEPOSIT);

        // then
        assertThat(isValidOperationType).isTrue();
    }

    @Test
    public void test_isValidAmount_null_amount() {
        // given
        DepositOperationValidator depositValidator = new DepositOperationValidator();

        // when
        // validating a null amount
        boolean isValidAmount = depositValidator.isValidAmount(null);

        // then
        assertThat(isValidAmount).isFalse();
    }

    @Test
    public void test_isValidAmount_negative_amount() {
        // given
        DepositOperationValidator depositValidator = new DepositOperationValidator();

        // when
        // validating a negative amount
        boolean isValidAmount = depositValidator.isValidAmount(BigDecimal.ONE.negate());

        // then
        assertThat(isValidAmount).isFalse();
    }

    @Test
    public void test_isValidAmount() {
        // given
        DepositOperationValidator depositValidator = new DepositOperationValidator();

        List<BigDecimal> amounts = Arrays.asList(BigDecimal.ZERO,
                BigDecimal.ONE,
                new BigDecimal(123456),
                new BigDecimal(123.456));

        amounts.forEach(amount -> {
            // when
            // validating amount
            boolean isValidAmount = depositValidator.isValidAmount(amount);

            // then
            assertThat(isValidAmount).isTrue();
        });
    }

    @Test
    public void test_validate_null_operation() {
        // given
        DepositOperationValidator depositOperationValidator = new DepositOperationValidator();

        // when
        // operation is null

        // then
        // expect a NullPointerException to be thrown
        assertThatThrownBy(() -> depositOperationValidator.validate(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("operation must not be null");
    }

    @Test
    public void test_validate_wrong_operation_type() {
        // given
        DepositOperationValidator depositOperationValidator = new DepositOperationValidator();

        when(depositOperationMock.getType()).thenReturn(OperationType.WITHDRAWAL);
        when(depositOperationMock.getAmount()).thenReturn(BigDecimal.ZERO);

        // when
        ValidationResult<DepositOperation> validationResult = depositOperationValidator.validate(depositOperationMock);

        // then
        assertThat(validationResult.hasErrors()).isTrue();
        assertThat(validationResult.getErrors()).hasSize(1).containsExactly("Wrong operation type");
    }

    @Test
    public void test_validate_wrong_operation_amount() {
        // given
        DepositOperationValidator depositOperationValidator = new DepositOperationValidator();

        when(depositOperationMock.getType()).thenReturn(OperationType.DEPOSIT);
        when(depositOperationMock.getAmount()).thenReturn(BigDecimal.ONE.negate());

        // when
        ValidationResult<DepositOperation> validationResult = depositOperationValidator.validate(depositOperationMock);

        // then
        assertThat(validationResult.hasErrors()).isTrue();
        assertThat(validationResult.getErrors()).hasSize(1).containsExactly("Wrong operation amount");
    }
}