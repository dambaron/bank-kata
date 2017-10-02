package com.github.dambaron.bank.report;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

public class TableCell {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableCell.class);

    private int rowId;
    private int columnId;
    private String value;

    private CellFormat cellFormat;

    public TableCell(int rowId, int columnId, String value, CellFormat format) {
        this.rowId = rowId;
        this.columnId = columnId;
        this.value = value;
        this.cellFormat = checkNotNull(format, "cellFormat must not be null");
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public int getColumnId() {
        return columnId;
    }

    public void setColumnId(int columnId) {
        this.columnId = columnId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toReportString() {

        int maxValueSize = cellFormat.getSize();
        int capacity = cellFormat.getSize();

        CellBorder separator = Optional.ofNullable(cellFormat.getBorder()).orElse(CellBorder.NONE);
        CellAlign align = Optional.ofNullable(cellFormat.getAlign()).orElse(CellAlign.LEFT);

        switch (separator) {
            case LEFT:
            case RIGHT:
                capacity = capacity - 1;
                break;
            case SIDES:
                capacity = capacity - 2;
                break;
            default:
                //do nothing
        }

        String format = (CellBorder.LEFT.equals(separator) || CellBorder.SIDES.equals(separator) ? "|" : "") +
                "%" +
                (CellAlign.LEFT.equals(align) ? "-" : "") +
                capacity + "s" +
                (CellBorder.RIGHT.equals(separator) || CellBorder.SIDES.equals(separator) ? "|" : "");

        String formattedValue = String.format(format, value);

        if (formattedValue.length() > maxValueSize) {
            LOGGER.error("Formatted value \"{}\" exceeds cell capacity (max {})", formattedValue, maxValueSize);
            return StringUtils.leftPad("", maxValueSize, "X");
        }

        return formattedValue;
    }

    @Override
    public String toString() {
        return "TableCell{" +
                "rowId=" + rowId +
                ", columnId=" + columnId +
                ", value='" + value + '\'' +
                ", cellFormat=" + cellFormat +
                '}';
    }
}
