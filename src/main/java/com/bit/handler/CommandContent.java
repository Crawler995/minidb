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
        createDatabase,createTable,createIndex,
        dropDatabase,dropTable,dropIndex,
        showDatabases,showTables,
        use,
        insert,delete,update,select,
        errorCommand
    }

    Operation operation = null;

    List<ColumnName> columnNames = new ArrayList<>();
    List<TableName> tableNames = new ArrayList<>();

    String databaseName = null; // create/drop/use database
    List<TableCreateInfo> tableCreateInfo = new ArrayList<>(); // create table
    IndexName indexName = null;// create index

    List<SubCommandOfWhere> subCommandOfWheres = new ArrayList<>();

    List<String> insertedColumn = new ArrayList<>();
    List<List<String>> insertedColumnValue = new ArrayList<>();

    Map<String,Boolean> config = new HashMap<>();
    String rawCommand = null;

    List<String> tempString = new ArrayList<>();


    public void setOperation(Operation operation) {
        this.operation = operation;
    }
    public Operation getOperation() {
        return operation;
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

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
    public String getDatabaseName() {
        return databaseName;
    }

    public void setIndexName(String indexName,String tableName,String databaseName,List<String> columnName){
        this.indexName = new IndexName(indexName,tableName,databaseName,columnName);
    }
    public IndexName getIndexName() {
        return indexName;
    }

    public void setRawCommand(String rawCommand) {
        this.rawCommand = rawCommand;
    }
    public String getRawCommand() {
        return rawCommand;
    }

    public void addConfig(String config){
        this.config.put(config,true);
    }
    public Map<String, Boolean> getConfig() {
        return config;
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

    public void addTableInfo(String columnName,String type){
        tableCreateInfo.add(new TableCreateInfo(columnName,type));
    }
    public void addTableInfo(List<TableCreateInfo> tableCreateInfo){
        this.tableCreateInfo.addAll(tableCreateInfo);
    }
    public List<TableCreateInfo> getTableCreateInfo() {
        return tableCreateInfo;
    }

    public void setInsertedColumn(List<String> insertedColumn) {
        this.insertedColumn = insertedColumn;
    }
    public List<String> getInsertedColumn() {
        return insertedColumn;
    }

    public void setInsertedColumnValue(List<List<String>> insertedColumnValue) {
        this.insertedColumnValue = insertedColumnValue;
    }
    public List<List<String>> getInsertedColumnValue() {
        return insertedColumnValue;
    }

    public void addSubCommandOfWheres(ColumnName columnName,String operation,String value){
        subCommandOfWheres.add(new SubCommandOfWhere(columnName,operation,value));
    }
    public void addSubCommandOfWheres(ColumnName columnName,String operation,ColumnName value){
        subCommandOfWheres.add(new SubCommandOfWhere(columnName,operation,value));
    }
    public void addSubCommandOfWheres(ColumnName columnName,String operation,String value_first,String value_second){
        subCommandOfWheres.add(new SubCommandOfWhere(columnName,operation,value_first,value_second));
    }
    public void addSubCommandOfWheres(SubCommandOfWhere subCommandOfWhere){
        subCommandOfWheres.add(subCommandOfWhere);
    }
    public void addSubCommandOfWheres(List<SubCommandOfWhere> subCommandOfWheres){
        this.subCommandOfWheres.addAll(subCommandOfWheres);
    }
    public List<SubCommandOfWhere> getSubCommandOfWheres() {
        return subCommandOfWheres;
    }
}
