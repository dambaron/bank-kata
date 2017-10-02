package com.github.dambaron.bank.report;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class TableCellTest {

    private static final CellFormat FOUR_CHARS_LEFT_NONE = new CellFormat(4, CellAlign.LEFT, CellBorder.NONE);
    private static final CellFormat FOUR_CHARS_RIGHT_NONE = new CellFormat(4, CellAlign.RIGHT, CellBorder.NONE);

    private static final CellFormat FOUR_CHARS_LEFT_LEFT = new CellFormat(4, CellAlign.LEFT, CellBorder.LEFT);
    private static final CellFormat FOUR_CHARS_LEFT_RIGHT = new CellFormat(4, CellAlign.LEFT, CellBorder.RIGHT);

    private static final CellFormat FOUR_CHARS_RIGHT_LEFT = new CellFormat(4, CellAlign.RIGHT, CellBorder.LEFT);
    private static final CellFormat FOUR_CHARS_RIGHT_RIGHT = new CellFormat(4, CellAlign.RIGHT, CellBorder.RIGHT);

    private static final CellFormat FOUR_CHARS_LEFT_SIDES = new CellFormat(4, CellAlign.LEFT, CellBorder.SIDES);
    private static final CellFormat FOUR_CHARS_RIGHT_SIDES = new CellFormat(4, CellAlign.RIGHT, CellBorder.SIDES);

    private static final CellFormat EIGHT_CHARS_LEFT_NONE = new CellFormat(8, CellAlign.LEFT, CellBorder.NONE);
    private static final CellFormat EIGHT_CHARS_RIGHT_NONE = new CellFormat(8, CellAlign.RIGHT, CellBorder.NONE);

    private static final CellFormat EIGHT_CHARS_LEFT_LEFT = new CellFormat(8, CellAlign.LEFT, CellBorder.LEFT);
    private static final CellFormat EIGHT_CHARS_LEFT_RIGHT = new CellFormat(8, CellAlign.LEFT, CellBorder.RIGHT);

    private static final CellFormat EIGHT_CHARS_RIGHT_LEFT = new CellFormat(8, CellAlign.RIGHT, CellBorder.LEFT);
    private static final CellFormat EIGHT_CHARS_RIGHT_RIGHT = new CellFormat(8, CellAlign.RIGHT, CellBorder.RIGHT);

    private static final CellFormat EIGHT_CHARS_LEFT_SIDES = new CellFormat(8, CellAlign.LEFT, CellBorder.SIDES);
    private static final CellFormat EIGHT_CHARS_RIGHT_SIDES = new CellFormat(8, CellAlign.RIGHT, CellBorder.SIDES);

    @Test
    public void test_toReportString_oversize_value() {
        //given
        //tables cells whose value exceeds the cell capacity after formatting
        TableCell c00 = new TableCell(0, 0, "12345", FOUR_CHARS_LEFT_NONE);
        TableCell c01 = new TableCell(0, 0, "12345", FOUR_CHARS_RIGHT_NONE);

        TableCell c10 = new TableCell(1, 0, "1234", FOUR_CHARS_LEFT_LEFT);
        TableCell c11 = new TableCell(1, 1, "1234", FOUR_CHARS_LEFT_RIGHT);
        TableCell c12 = new TableCell(1, 2, "1234", FOUR_CHARS_RIGHT_LEFT);
        TableCell c13 = new TableCell(1, 3, "1234", FOUR_CHARS_RIGHT_RIGHT);

        TableCell c20 = new TableCell(2, 0, "123", FOUR_CHARS_LEFT_SIDES);
        TableCell c21 = new TableCell(2, 1, "123", FOUR_CHARS_RIGHT_SIDES);

        List<TableCell> tableCells = Arrays.asList(c00, c01, c10, c11, c12, c13, c20, c21);

        tableCells.forEach(tableCell -> {
            //when
            String reportString = tableCell.toReportString();

            //then
            //expect all cells to display an error
            assertThat(reportString).isEqualTo("XXXX");
        });
    }

    @Test
    public void test_toReportString() {
        //given
        Map<TableCell, String> expectedOutputs = new HashMap<>();

        TableCell c00 = new TableCell(0, 0, "1234", EIGHT_CHARS_LEFT_NONE);
        expectedOutputs.put(c00, "1234    ");

        TableCell c01 = new TableCell(0, 0, "1234", EIGHT_CHARS_RIGHT_NONE);
        expectedOutputs.put(c01, "    1234");

        TableCell c10 = new TableCell(1, 0, "1234", EIGHT_CHARS_LEFT_LEFT);
        expectedOutputs.put(c10, "|1234   ");

        TableCell c11 = new TableCell(1, 1, "1234", EIGHT_CHARS_LEFT_RIGHT);
        expectedOutputs.put(c11, "1234   |");

        TableCell c12 = new TableCell(1, 2, "1234", EIGHT_CHARS_RIGHT_LEFT);
        expectedOutputs.put(c12, "|   1234");

        TableCell c13 = new TableCell(1, 3, "1234", EIGHT_CHARS_RIGHT_RIGHT);
        expectedOutputs.put(c13, "   1234|");

        TableCell c20 = new TableCell(2, 0, "1234", EIGHT_CHARS_LEFT_SIDES);
        expectedOutputs.put(c20, "|1234  |");

        TableCell c21 = new TableCell(2, 1, "1234", EIGHT_CHARS_RIGHT_SIDES);
        expectedOutputs.put(c21, "|  1234|");

        expectedOutputs.forEach((tableCell, expectedReportString) -> {
            //when
            String actualReportString = tableCell.toReportString();

            //then
            assertThat(actualReportString).isEqualTo(expectedReportString);
        });
    }

}