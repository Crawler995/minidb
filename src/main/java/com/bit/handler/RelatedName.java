package com.bit.handler;

public class RelatedName {
    protected String databaseName;
    protected String tableName;
    protected String columnName;
    protected String aliasName;

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getAliasName() {
        return aliasName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getTableName() {
        return tableName;
    }
}
