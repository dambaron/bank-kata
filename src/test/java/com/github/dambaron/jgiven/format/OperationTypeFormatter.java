package com.github.dambaron.jgiven.format;

import com.github.dambaron.bank.model.operation.Operation;
import com.tngtech.jgiven.format.ArgumentFormatter;

public class OperationTypeFormatter implements ArgumentFormatter<Operation> {
    @Override
    public String format(Operation operation, String... formatterArguments) {
        if (operation == null || operation.getType() == null) {
            return "";
        }

        return operation.getType().name();
    }
}
