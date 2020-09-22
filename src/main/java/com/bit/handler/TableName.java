package com.bit.handler;

import org.apache.catalina.LifecycleState;

import java.util.ArrayList;
import java.util.List;

public class TableName extends RelatedName{
    public enum JoinType{
        noJoin,outJoin,innerJoin,leftJoin,rightJoin,naturalJoin
    }
    JoinType joinType = JoinType.noJoin;
    JoinType beJoinType = JoinType.noJoin;

    TableName next = null;
    TableName before = null;

    List<SubCommandOfWhere> joinExpression = new ArrayList<>();

    public void addJoinExpression(List<SubCommandOfWhere> joinExpression) {
        this.joinExpression.addAll(joinExpression);
    }
    public void addJoinExpression(SubCommandOfWhere joinExpression){
        this.joinExpression.add(joinExpression);
    }

    public List<SubCommandOfWhere> getJoinExpression() {
        return joinExpression;
    }
    public SubCommandOfWhere getJoinExpression(int idx){
        return joinExpression.get(idx);
    }

    public void setJoin(JoinType joinType, TableName next){
        this.joinType = joinType;
        this.next = next;
        next.setBefore(this);
    }

    public void setBeJoinType(JoinType beJoinType) {
        this.beJoinType = beJoinType;
    }

    public JoinType getJoinType(){
        return joinType;
    }
    public JoinType getBeJoinType(){
        return beJoinType;
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
