package com.bit.handler;

public class SqlCommandVisitor extends MySqlParserBaseVisitor<Integer> {
    SqlCommandVisitor(){

    }

    @Override
    public Integer visitSqlStatements(MySqlParser.SqlStatementsContext ctx) {
        return null;
    }
}
