package com.bit.exception;

/**
 * @author aerfafish
 * @date 2020/9/23 3:30 下午
 */
public class SqlErrorException extends Exception{

    public SqlErrorException(int row, int column, String text) {
        this.row = row;
        this.column = column;
        this.text = text;
    }

    int row;

    int column;

    String text = "";

    String type = "error";

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }
}
