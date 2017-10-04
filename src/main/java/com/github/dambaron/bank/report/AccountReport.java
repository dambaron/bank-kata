package com.github.dambaron.bank.report;

import com.github.dambaron.bank.model.Account;
import com.github.dambaron.bank.model.Statement;
import com.github.dambaron.bank.model.operation.DepositOperation;
import com.github.dambaron.bank.model.operation.OperationType;
import com.github.dambaron.bank.model.operation.WithdrawalOperation;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.commons.lang3.StringUtils;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class AccountReport extends TableReport {

    private static final int MAX_LINE_LENGTH = 120;

    private Locale locale;

    private Currency reportCurrency = Currency.getInstance(Locale.getDefault());

    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

    private Account account;

    private Table<Integer, Integer, TableCell> reportTable = HashBasedTable.create();

    private AccountReport() {
        //DO NOTHING
    }

    public AccountReport(Account account, Locale locale) {
        this.account = checkNotNull(account, "account must not be null");
        this.locale = checkNotNull(locale, "locale must not be null");

        this.reportCurrency = Currency.getInstance(locale);
        this.currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        this.currencyFormatter.setRoundingMode(RoundingMode.HALF_UP);
        this.currencyFormatter.setCurrency(reportCurrency);
    }

    public String toStringReport() {
        Integer rowId = 0;

        addRuler(rowId);
        rowId++;

        addAccountId(rowId, account.getId());
        rowId++;

        addOwnerId(rowId, account.getOwnerId());
        rowId++;

        addRuler(rowId);
        rowId++;

        addStatementHeader(rowId);
        rowId++;

        addRuler(rowId);
        rowId++;

        List<Statement> statements = Optional.ofNullable(account.getStatements()).orElse(new ArrayList<>());
        for (Statement statement : statements) {
            addStatement(rowId, statement);
            rowId++;
        }

        addRuler(rowId);
        rowId++;

        addAccountBalance(rowId);
        rowId++;

        addRuler(rowId);

        StringBuilder sb = new StringBuilder();
        reportTable.rowMap().forEach(
                (row, columns) -> {
                    columns.values().forEach(cell -> sb.append(cell.toReportString()));
                    sb.append("\n");
                }
        );
        return sb.toString();
    }

    private void addRuler(Integer rowId) {
        CellFormat format = new CellFormat(MAX_LINE_LENGTH, CellAlign.LEFT, CellBorder.SIDES);
        String value = StringUtils.rightPad("", format.getCapacity(), "-");

        TableCell cell = new TableCell(rowId, 0, value, format);

        reportTable.put(rowId, 0, cell);
    }

    private void addAccountId(Integer rowId, String accountId) {
        CellFormat format = new CellFormat(MAX_LINE_LENGTH, CellAlign.LEFT, CellBorder.SIDES);
        String value = "Account: " + accountId;

        TableCell cell = new TableCell(rowId, 0, value, format);

        reportTable.put(rowId, 0, cell);
    }

    private void addOwnerId(Integer rowId, String ownerId) {
        CellFormat format = new CellFormat(MAX_LINE_LENGTH, CellAlign.LEFT, CellBorder.SIDES);
        String value = "Owner: " + ownerId;

        TableCell cell = new TableCell(rowId, 0, value, format);

        reportTable.put(rowId, 0, cell);
    }

    private void addStatementHeader(Integer rowId) {
        CellFormat operationDateFormat = new CellFormat(20, CellAlign.LEFT, CellBorder.SIDES);
        CellFormat operationIdFormat = new CellFormat(40, CellAlign.LEFT, CellBorder.SIDES);
        CellFormat depositFormat = new CellFormat(30, CellAlign.RIGHT, CellBorder.SIDES);
        CellFormat withdrawalFormat = new CellFormat(30, CellAlign.RIGHT, CellBorder.SIDES);

        String operationDate = "Date";
        String operationId = "Operation";
        String deposit = "Deposit";
        String withdrawal = "Withdrawal";

        TableCell operationDateCell = new TableCell(rowId, 0, operationDate, operationDateFormat);
        TableCell operationIdCell = new TableCell(rowId, 1, operationId, operationIdFormat);
        TableCell depositCell = new TableCell(rowId, 2, deposit, depositFormat);
        TableCell withdrawalCell = new TableCell(rowId, 3, withdrawal, withdrawalFormat);

        reportTable.put(rowId, 0, operationDateCell);
        reportTable.put(rowId, 1, operationIdCell);
        reportTable.put(rowId, 2, depositCell);
        reportTable.put(rowId, 3, withdrawalCell);
    }

    private void addStatement(Integer rowId, Statement statement) {
        CellFormat operationDateFormat = new CellFormat(20, CellAlign.LEFT, CellBorder.SIDES);
        CellFormat operationIdFormat = new CellFormat(40, CellAlign.LEFT, CellBorder.SIDES);
        CellFormat depositAmountFormat = new CellFormat(30, CellAlign.RIGHT, CellBorder.SIDES);
        CellFormat withdrawalAmountFormat = new CellFormat(30, CellAlign.RIGHT, CellBorder.SIDES);

        //Operation date in ISO format
        LocalDateTime operationLocalDateTime = LocalDateTime.ofInstant(statement.getInstant(), ZoneId.systemDefault());
        String operationDate = DateTimeFormatter.ISO_LOCAL_DATE.format(operationLocalDateTime);

        //Operation id
        String operationId = statement.getOperation().getId();

        //Rounded operation amount using default system locale
        String depositAmount = "";
        String withdrawalAmount = "";

        if (statement.getOperation() != null && statement.getOperation().getType() != null) {
            OperationType operationType = statement.getOperation().getType();
            switch (operationType) {
                case DEPOSIT:
                    DepositOperation deposit = (DepositOperation) statement.getOperation();
                    depositAmount = currencyFormatter.format(deposit.getAmount());

                    withdrawalAmount = StringUtils.rightPad("", withdrawalAmountFormat.getCapacity());
                    break;
                case WITHDRAWAL:
                    depositAmount = StringUtils.rightPad("", depositAmountFormat.getCapacity());

                    WithdrawalOperation withdrawal = (WithdrawalOperation) statement.getOperation();
                    withdrawalAmount = currencyFormatter.format(withdrawal.getAmount());
                    break;
            }
        }

        TableCell operationDateCell = new TableCell(rowId, 0, operationDate, operationDateFormat);
        TableCell operationIdCell = new TableCell(rowId, 1, operationId, operationIdFormat);
        TableCell depositAmountCell = new TableCell(rowId, 2, depositAmount, depositAmountFormat);
        TableCell withdrawalAmountCell = new TableCell(rowId, 3, withdrawalAmount, withdrawalAmountFormat);

        reportTable.put(rowId, 0, operationDateCell);
        reportTable.put(rowId, 1, operationIdCell);
        reportTable.put(rowId, 2, depositAmountCell);
        reportTable.put(rowId, 3, withdrawalAmountCell);
    }

    private void addAccountBalance(Integer rowId) {
        CellFormat balanceFormat = new CellFormat(90, CellAlign.LEFT, CellBorder.SIDES);
        CellFormat balanceAmountFormat = new CellFormat(30, CellAlign.RIGHT, CellBorder.SIDES);

        String balance = "Balance";
        String balanceAmount = currencyFormatter.format(account.getBalance());

        TableCell balanceCell = new TableCell(rowId, 0, balance, balanceFormat);
        TableCell balanceAmountCell = new TableCell(rowId, 1, balanceAmount, balanceAmountFormat);

        reportTable.put(rowId, 0, balanceCell);
        reportTable.put(rowId, 1, balanceAmountCell);
    }
}
