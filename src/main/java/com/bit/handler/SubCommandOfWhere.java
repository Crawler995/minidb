package com.bit.handler;

public class SubCommandOfWhere {
    public enum Category{
        CR,SR
    }
    ColumnName columnName_left = null;
    ColumnName columnName_right = null;
    String operation;
    String value_first = null;
    String value_second = null;


    SubCommandOfWhere(ColumnName columnName_left,String operation,ColumnName columnName_right){
        this.operation = operation;
        this.columnName_left = columnName_left;
        this.columnName_right = columnName_right;
    }


    SubCommandOfWhere(ColumnName columnName,String operation,String value){
        this.columnName_left = columnName;
        this.operation = operation;
        this.value_first = value;
    }

    SubCommandOfWhere(ColumnName columnName,String operation,String value,String value_1){
        this.columnName_left = columnName;
        this.operation = operation;
        this.value_first = value;
        this.value_second = value_1;
    }

}
