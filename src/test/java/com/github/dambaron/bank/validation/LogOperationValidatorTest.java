package com.github.dambaron.bank.validation;

import com.github.dambaron.bank.model.operation.LogOperation;
import com.github.dambaron.bank.model.operation.OperationType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LogOperationValidatorTest {

    @Mock
    private LogOperation logOperationMock;

    @Test
    public void test_isValidOperationType_no_deposit_operation() {
        // given
        LogOperationValidator logOperationValidator = new LogOperationValidator();

        Arrays.stream(OperationType.values())
                .filter(operationType -> !OperationType.LOG.equals(operationType))
                .forEach(operationType -> {

                    // when
                    // the operation type is NOT LOG
                    boolean isValidOperationType = logOperationValidator.isValidOperationType(operationType);

                    // then
                    assertThat(isValidOperationType).isFalse();
                });
    }

    @Test
    public void test_isValidOperationType() {
        // given
        LogOperationValidator logValidator = new LogOperationValidator();

        // when
        // the operation type is LOG
        boolean isValidOperationType = logValidator.isValidOperationType(OperationType.LOG);

        // then
        assertThat(isValidOperationType).isTrue();
    }

    @Test
    public void test_validate_null_operation() {
        // given
        LogOperationValidator logValidator = new LogOperationValidator();

        // when
        // operation is null

        // then
        // expect a NullPointerException to be thrown
        assertThatThrownBy(() -> logValidator.validate(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("operation must not be null");
    }

    @Test
    public void test_validate_wrong_operation_type() {
        // given
        LogOperationValidator logValidator = new LogOperationValidator();

        when(logOperationMock.getType()).thenReturn(OperationType.DEPOSIT);

        // when
        ValidationResult<LogOperation> validationResult = logValidator.validate(logOperationMock);

        // then
        assertThat(validationResult.hasErrors()).isTrue();
        assertThat(validationResult.getErrors()).hasSize(1).containsExactly("Wrong operation type");
    }
}