package com.github.dambaron.bank.report;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class TableReport {

    private Table<Integer, Integer, TableCell> reportTable = HashBasedTable.create();

    public void addTableCell(TableCell cell) {
        reportTable.put(cell.getRowId(), cell.getColumnId(), cell);
    }

    public String toReportLine(Integer rowId) {
        return "";
    }

    public String toReportColumn(Integer columnId) {
        return "";
    }

    public String toReportCell(TableCell tableCell) {
        return "";
    }

    public String toReportString() {
        String reportAsString = "";

        return reportAsString;
    }
}
