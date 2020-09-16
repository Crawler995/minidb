package com.bit.handler;

public class ColumnName extends RelatedName{
    ColumnName(String name,String aliasName,String belongedTableName,String databaseName){
        setColumnName(name);
        if(aliasName != null)
            setAliasName(aliasName);
        if(belongedTableName != null)
            setTableName(belongedTableName);
        if(databaseName != null)
            setDatabaseName(databaseName);
    }
}
