package com.bit.handler;

public class TableCreateInfo {
    String columnName;
    String  type;

    TableCreateInfo(String columnName,String type){
        this.columnName = columnName;
        this.type = type;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getType() {
        return type;
    }
}
