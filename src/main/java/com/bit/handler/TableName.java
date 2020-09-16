package com.bit.handler;

public class TableName extends RelatedName{
    public enum JoinType{
        noJoin,join,outJoin,innerJoin
    }
    JoinType joinType = JoinType.noJoin;

    TableName next;
    TableName before;
    public void setJoin(JoinType joinType,TableName next){
        this.joinType = joinType;
        this.next = next;
        next.setBefore(this);
    }

    TableName(String tableName, String aliasName, String databaseName) {
        if(aliasName != null)
            setAliasName(aliasName);
        setTableName(tableName);
        if(databaseName != null)
            setDatabaseName(databaseName);

        before = next = null;
    }

    public void setNext(TableName next){
        this.next = next;
    }

    public TableName getNext() {
        return next;
    }

    public void setBefore(TableName before) {
        this.before = before;
    }

    public TableName getBefore() {
        return before;
    }
}
