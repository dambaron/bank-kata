package com.github.dambaron.bank.report;

public class CellFormat {

    public static final CellFormat TEN_CHARS_LEFT_ALIGN_NO_SEPARATOR =
            new CellFormat(10, CellAlign.LEFT, CellBorder.NONE);

    private int size;
    private CellAlign align = CellAlign.LEFT;
    private CellBorder border = CellBorder.NONE;

    private CellFormat() {
        //DO NOTHING
    }

    public CellFormat(int size, CellAlign align, CellBorder border) {
        this.size = size;
        this.align = align;
        this.border = border;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public CellAlign getAlign() {
        return align;
    }

    public void setAlign(CellAlign align) {
        this.align = align;
    }

    public CellBorder getBorder() {
        return border;
    }

    public void setBorder(CellBorder border) {
        this.border = border;
    }

    public int getCapacity() {
        if (border == null) {
            return size;
        }

        switch (border) {
            case LEFT:
            case RIGHT:
                return size -1;
            case SIDES:
                return size - 2;
            default:
                return size;
        }
    }

    @Override
    public String toString() {
        return "CellFormat{" +
                "size=" + size +
                ", align=" + align +
                ", border=" + border +
                '}';
    }
}
