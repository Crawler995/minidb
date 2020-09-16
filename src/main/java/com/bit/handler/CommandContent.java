package com.bit.handler;

import com.bit.model.Table;
import javafx.scene.control.Tab;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandContent {
    public enum Operation{
        create,delete,select
    }

    Operation operation = null;
    List<ColumnName> columnNames = new ArrayList<>();
    List<TableName> tableNames = new ArrayList<>();
    Map<String,Boolean> config = new HashMap<>();

    List<String> tempString = new ArrayList<>();

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public void addColumnName(String name, String aliasName, String belongedTableName, String databaseName){
        ColumnName columnName = new ColumnName(name, aliasName, belongedTableName,databaseName);
        columnNames.add(columnName);
    }
    public void addColumnName(List<ColumnName> names){
        columnNames.addAll(names);
    }
    public void addColumnName(ColumnName columnName){
        columnNames.add(columnName);
    }
    public void addColumnName(String columnName){
        columnNames.add(new ColumnName(columnName, null,null,null));
    }

    public List<ColumnName> getColumnNames() {
        return columnNames;
    }

    public void addTableName(String name, String aliasName, String databaseName){
        TableName tableName = new TableName(name, aliasName, databaseName);
        tableNames.add(tableName);
    }
    public void addTableName(List<TableName> tableNames){
        this.tableNames.addAll(tableNames);
    }
    public void addTableName(TableName tableName){
        this.tableNames.add(tableName);
    }
    public void addTableName(String tableName){
        tableNames.add(new TableName(tableName,null,null));
    }
    public List<TableName> getTableNames(){
        return tableNames;
    }

    public void addConfig(String config){
        this.config.put(config,true);
    }

    public void addTempString(String tempString) {
        this.tempString.add(tempString);
    }
    public List<String> getTempString(){
        return this.tempString;
    }
    public String getTempString(int i){
        return this.tempString.get(i);
    }
}
