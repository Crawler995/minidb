package com.bit.handler;

import java.util.ArrayList;
import java.util.List;

public class IndexName extends RelatedName{
    List<String> columnNames = new ArrayList<>();
    IndexName(String indexName,List<String> columnName){
        this.columnNames = columnName;
        this.indexName = indexName;
    }
    IndexName(String indexName,String tableName,String databaseName,List<String> columnName){
        this.columnNames = columnName;
        this.indexName = indexName;
        this.tableName = tableName;
        this.databaseName = databaseName;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }
}
