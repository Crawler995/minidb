package com.bit.handler;

public class SubCommandOfWhere {
    Boolean rightIsColumn = false;
    Boolean leftIsValue = false;
    ColumnName columnNameLeft = null;
    ColumnName columnNameRight = null;
    String operation;
    String valueFirst = null;
    String valueSecond = null;


    SubCommandOfWhere(ColumnName columnNameLeft, String operation, ColumnName columnNameRight){
        this.operation = operation;
        this.columnNameLeft = columnNameLeft;
        this.columnNameRight = columnNameRight;
        rightIsColumn = true;
    }

    SubCommandOfWhere(String valueFirst,String operation,String valueSecond){
        this.valueFirst = valueFirst;
        this.valueSecond = valueSecond;
        this.operation = operation;
        leftIsValue = true;
    }


    SubCommandOfWhere(ColumnName columnName,String operation,String value){
        this.columnNameLeft = columnName;
        this.operation = operation;
        this.valueFirst = value;
    }

    SubCommandOfWhere(ColumnName columnName,String operation,String value,String value_1){
        this.columnNameLeft = columnName;
        this.operation = operation;
        this.valueFirst = value;
        this.valueSecond = value_1;
    }

    public String getOperation() {
        return operation;
    }

    public ColumnName getColumnNameLeft() {
        return columnNameLeft;
    }

    public ColumnName getColumnNameRight() {
        return columnNameRight;
    }

    public String getValueFirst() {
        return valueFirst;
    }

    public String getValueSecond() {
        return valueSecond;
    }

    public Boolean getLeftIsValue() {
        return leftIsValue;
    }

    public Boolean getRightIsColumn() {
        return rightIsColumn;
    }
}
