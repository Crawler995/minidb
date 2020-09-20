package com.bit.handler;

import com.sun.org.apache.xml.internal.security.c14n.implementations.Canonicalizer11_OmitComments;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class SqlCommandVisitor extends MySqlParserBaseVisitor<CommandContent> {
    List<CommandContent> commandContents;
    String rawCommand = null;
    SqlCommandVisitor(List<CommandContent> contents){
        commandContents = contents;
    }

    @Override
    public CommandContent visitSqlStatement(MySqlParser.SqlStatementContext ctx) {
        rawCommand = ctx.getText();
        if(rawCommand.equals("SHOW DATABASES")){
            CommandContent content = new CommandContent();
            content.setOperation(CommandContent.Operation.showDatabases);
            content.setRawCommand(rawCommand);
            commandContents.add(content);
            return null;
        }
        if(rawCommand.equals("SHOW TABLES")){
            CommandContent content = new CommandContent();
            content.setOperation(CommandContent.Operation.showTables);
            content.setRawCommand(rawCommand);
            commandContents.add(content);
            return null;
        }
        if(ctx.children.size() <= 0){
            CommandContent content = new CommandContent();
            content.setOperation(CommandContent.Operation.errorCommand);
            content.setRawCommand(rawCommand);
            commandContents.add(content);
            return null;
        }
        visit(ctx.getChild(0));
        return null;
    }

    /*** data define language ********************* here to add commandContent list     */
    @Override
    public CommandContent visitCreateDatabase(MySqlParser.CreateDatabaseContext ctx) {
        CommandContent content = new CommandContent();
        if(ctx.dbFormat.getText().equals("DATABASE")){
            content.setOperation(CommandContent.Operation.createDatabase);
            content.setDatabaseName(visit(ctx.uid()).getTempString().get(0));
            for(MySqlParser.CreateDatabaseOptionContext c : ctx.createDatabaseOption()){
                content.addConfig(c.getText());
            }
            if(ctx.ifNotExists() != null){
                content.addConfig(ctx.ifNotExists().getText());
            }
        }
        else{
           content.setOperation(CommandContent.Operation.errorCommand);
        }
        content.setRawCommand(rawCommand);
        commandContents.add(content);
        return null;
    }

    @Override
    public CommandContent visitCreateIndex(MySqlParser.CreateIndexContext ctx) {
        CommandContent content = new CommandContent();
        String indexName;
        List<String> columnName;
        content.setOperation(CommandContent.Operation.createIndex);
        if(ctx.intimeAction != null){
            content.addConfig(ctx.intimeAction.getText());
        }
        if(ctx.indexCategory != null){
            content.addConfig(ctx.indexCategory.getText());
        }
        indexName = visit(ctx.uid()).getTempString(0);
        TableName tableName = visit(ctx.tableName()).getTableNames().get(0);
        columnName = visit(ctx.indexColumnNames()).getTempString();
        content.setRawCommand(rawCommand);
        content.setIndexName(indexName,tableName.getTableName(),tableName.getDatabaseName(),columnName);
        commandContents.add(content);
        return null;
    }

    @Override
    public CommandContent visitColumnCreateTable(MySqlParser.ColumnCreateTableContext ctx) {
        CommandContent content = new CommandContent();
        content.setOperation(CommandContent.Operation.createTable);
        if(ctx.ifNotExists() != null){
            content.addConfig(ctx.ifNotExists().getText());
        }
        String tableName;
        String databaseName;
        content.addTableName(visit(ctx.tableName()).getTableNames());
        content.addTableInfo(visit(ctx.createDefinitions()).getTableCreateInfo());
        content.setRawCommand(rawCommand);
        commandContents.add(content);
        return null;
    }

    @Override
    public CommandContent visitDropDatabase(MySqlParser.DropDatabaseContext ctx) {
        CommandContent content = new CommandContent();
        if(ctx.dbFormat.getText().equals("DATABASE")){
            content.setOperation(CommandContent.Operation.dropDatabase);
            content.setDatabaseName(visit(ctx.uid()).getTempString().get(0));
            if(ctx.ifExists() != null){
                content.addConfig(ctx.ifExists().getText());
            }
        }
        else{
            content.setOperation(CommandContent.Operation.errorCommand);

        }
        content.setRawCommand(rawCommand);
        commandContents.add(content);
        return null;
    }

    @Override
    public CommandContent visitDropTable(MySqlParser.DropTableContext ctx) {
        CommandContent content = new CommandContent();
        content.setOperation(CommandContent.Operation.dropTable);
        content.setRawCommand(rawCommand);
        if(ctx.ifExists() != null){
            content.addConfig(ctx.ifExists().getText());
        }
        for(MySqlParser.TableNameContext c:  ctx.tables().tableName()){
            String tableName;
            String databaseName;
            List<String> tempString = visit(c).getTempString();
            switch(tempString.size()){
                case 1:
                    tableName = tempString.get(0);
                    databaseName = null;
                    break;
                case 2:
                    tableName = tempString.get(1);
                    databaseName = tempString.get(0);
                    break;
                default:
                    tableName = databaseName = null;
                    /**
                     * error!
                     */
                    break;
            }
            content.addTableName(tableName,null,databaseName);
        }
        commandContents.add(content);
        return null;
    }

    @Override
    public CommandContent visitDropIndex(MySqlParser.DropIndexContext ctx) {
        CommandContent content = new CommandContent();
        String indexName;
        indexName = visit(ctx.uid()).getTempString(0);
        TableName tableName = visit(ctx.tableName()).getTableNames().get(0);
        content.setRawCommand(rawCommand);
        content.setOperation(CommandContent.Operation.dropIndex);
        content.setIndexName(indexName,tableName.getTableName(),tableName.getDatabaseName(),null);
        commandContents.add(content);
        return  null;
    }

    @Override
    public CommandContent visitUseStatement(MySqlParser.UseStatementContext ctx) {
        CommandContent content = new CommandContent();
        String databaseName = visit(ctx.uid()).getTempString(0);

        content.setDatabaseName(databaseName);
        content.setOperation(CommandContent.Operation.use);
        content.setRawCommand(rawCommand);
        commandContents.add(content);
        return null;
    }

    /** data manipulation language statement ***************** here to add CommandContent List*/


    /**
     * select statement
     */
    @Override
    public CommandContent visitQuerySpecification(MySqlParser.QuerySpecificationContext ctx) {
        CommandContent content = new CommandContent();
        content.setOperation(CommandContent.Operation.select);

        for(MySqlParser.SelectSpecContext c : ctx.selectSpec()){
            content.addConfig(c.getText());
        }
        content.addColumnName(visit(ctx.selectElements()).getColumnNames());
        content.addTableName(visit(ctx.fromClause()).getTableNames());
        CommandContent temp = visit(ctx.fromClause());
        if(temp.getOperation() != null){
            content.setOperation(CommandContent.Operation.errorCommand);
        }
        else{
            content.addSubCommandOfWheres(temp.getSubCommandOfWheres());
            content.setOperation(CommandContent.Operation.select);
        }

        content.setRawCommand(rawCommand);
        commandContents.add(content);
        return null;
    }

    @Override
    public CommandContent visitParenthesisSelect(MySqlParser.ParenthesisSelectContext ctx) {
        return null;
    }

    @Override
    public CommandContent visitUnionSelect(MySqlParser.UnionSelectContext ctx) {
        return null;
    }

    @Override
    public CommandContent visitUnionParenthesisSelect(MySqlParser.UnionParenthesisSelectContext ctx) {
        return null;
    }

    /** insert*/
    @Override
    public CommandContent visitInsertStatement(MySqlParser.InsertStatementContext ctx) {
        CommandContent content;
        List<String> columnNames = new ArrayList<>();
        if(ctx.columns != null){
            for(MySqlParser.UidContext c : ctx.uidList(1).uid()){
                String name = visit(c).getTempString(0);
                columnNames.add(name);
            }
        }
        content = visit(ctx.insertStatementValue());
        if(content == null){
            content = new CommandContent();
            content.setOperation(CommandContent.Operation.errorCommand);
        }
        else{
            content.setOperation(CommandContent.Operation.insert);
            content.setInsertedColumn(columnNames);
            content.addTableName(visit(ctx.tableName()).getTableNames());
        }
        content.setRawCommand(rawCommand);
        commandContents.add(content);
        return null;
    }

    /** delete (single table)
     * no multiple tables delete support */
    @Override
    public CommandContent visitSingleDeleteStatement(MySqlParser.SingleDeleteStatementContext ctx) {
        CommandContent content = new CommandContent();
        content.addTableName(visit(ctx.tableName()).getTableNames());
        if(ctx.expression() != null){
            content.addSubCommandOfWheres(visit(ctx.expression()).getSubCommandOfWheres());
        }

        content.setOperation(CommandContent.Operation.delete);
        content.setRawCommand(rawCommand);
        commandContents.add(content);
        return null;

    }


    /** update*/
    @Override
    public CommandContent visitSingleUpdateStatement(MySqlParser.SingleUpdateStatementContext ctx) {
        CommandContent content = new CommandContent();
        content.addTableName(visit(ctx.tableName()).getTableNames());

        for(MySqlParser.UpdatedElementContext c: ctx.updatedElement()){
            CommandContent temp = visit(c);
            if(temp.getOperation() != null){
                content.setOperation(CommandContent.Operation.errorCommand);
                content.setRawCommand(rawCommand);
                return content;
            }
            content.addUpdateElement(temp.getUpdateElement().get(0));
        }

        if(ctx.expression() != null){

        }

        content.setOperation(CommandContent.Operation.update);
        content.setRawCommand(rawCommand);
        commandContents.add(content);
        return null;
    }


    @Override
    public CommandContent visitUpdatedElement(MySqlParser.UpdatedElementContext ctx) {
        CommandContent content = new CommandContent();
        CommandContent temp = visit(ctx.expression());

        if(temp == null){
            content.addUpdateElement(visit(ctx.fullColumnName()).getColumnNames().get(0),"=",ctx.expression().getText() );
            return content;
        }
        else{
             if(temp.getColumnNames().size() > 0){
                 content.addUpdateElement(visit(ctx.fullColumnName()).getColumnNames().get(0),"=",temp.getColumnNames().get(0));
             }
             else{
                 content.setOperation(CommandContent.Operation.errorCommand);
             }
        }
        return content;
    }

    @Override
    public CommandContent visitInsertStatementValue(MySqlParser.InsertStatementValueContext ctx) {
        /* no selectStatement support*/
        List<List<String>> columnValues = new ArrayList<>();
        CommandContent content = new CommandContent();
        if(ctx.selectStatement() != null){
            return null;
        }

        for(MySqlParser.ExpressionsWithDefaultsContext ct : ctx.expressionsWithDefaults()){
            List<String> columnValue = new ArrayList<>();
            for(MySqlParser.ExpressionOrDefaultContext c : ct.expressionOrDefault()){
                columnValue.add(c.getText());
            }
            columnValues.add(columnValue);
        }
        content.setInsertedColumnValue(columnValues);
        return content;
    }





    @Override
    public CommandContent visitSelectElements(MySqlParser.SelectElementsContext ctx) {
        CommandContent content = new CommandContent();

        if(ctx.star != null){
            content.addColumnName("*");
        }
        else{
            for(MySqlParser.SelectElementContext context : ctx.selectElement()) {
                content.addColumnName(visit(context).getColumnNames());
            }
        }

        return content;
    }

    @Override
    public CommandContent visitSelectStarElement(MySqlParser.SelectStarElementContext ctx) {
        CommandContent content = new CommandContent();
        List<String> tempString = visit(ctx.fullId()).getTempString();
        String tableName;
        String databaseName;
        switch (tempString.size()){
            case 1:
                tableName = tempString.get(0);
                databaseName = null;
                break;
            case 2:
                databaseName = tempString.get(0);
                tableName = tempString.get(1);
                break;
            default:
                    tableName = null;
                    databaseName = null;
                    break;
        }
        content.addColumnName("*",null,tableName,databaseName);
        return content;
    }

    @Override
    public CommandContent visitSelectColumnElement(MySqlParser.SelectColumnElementContext ctx) {
        CommandContent content = new CommandContent();
        ColumnName columnName = visit(ctx.fullColumnName()).getColumnNames().get(0);
        String aliasName;
        if(ctx.uid() != null){
            aliasName = visit(ctx.uid()).getTempString().get(0);
            columnName.setAliasName(aliasName);
        }

        content.addColumnName(columnName);
        return content;
    }

    /**
     * TODO:visitSelectFunctionElement,visitExpressionElement
    */

    @Override
    public CommandContent visitFromClause(MySqlParser.FromClauseContext ctx) {
        CommandContent content = new CommandContent();
        content.addTableName(visit(ctx.tableSources()).getTableNames());
        content.addSubCommandOfWheres(visit(ctx.whereExpr).getSubCommandOfWheres());
        if(content.getSubCommandOfWheres().size() == 0){
            content.setOperation(CommandContent.Operation.errorCommand);
        }
        return content;
    }

    @Override
    public CommandContent visitLogicalExpression(MySqlParser.LogicalExpressionContext ctx) {
        CommandContent content = new CommandContent();
        content.addSubCommandOfWheres(visit(ctx.expression(0)).getSubCommandOfWheres());
        content.addSubCommandOfWheres(visit(ctx.expression(1)).getSubCommandOfWheres());

        return content;
    }

    @Override
    public CommandContent visitPredicateExpression(MySqlParser.PredicateExpressionContext ctx) {
        return  visit(ctx.predicate());
    }

    @Override
    public CommandContent visitExpressionAtomPredicate(MySqlParser.ExpressionAtomPredicateContext ctx) {
        return  visit(ctx.expressionAtom());
    }


    @Override
    public CommandContent visitFullColumnNameExpressionAtom(MySqlParser.FullColumnNameExpressionAtomContext ctx) {
        CommandContent content = new CommandContent();
        content.addColumnName(visit(ctx.fullColumnName()).getColumnNames());

        return content;
    }

    @Override
    public CommandContent visitBinaryComparasionPredicate(MySqlParser.BinaryComparasionPredicateContext ctx) {
        CommandContent content = new CommandContent();
        ColumnName columnName = null;
        String valueL = null;
        String operation = ctx.comparisonOperator().getText();

        if(visit(ctx.left) == null){
            valueL = ctx.left.getText();
        }
        else{
            columnName = visit(ctx.left).getColumnNames().get(0);
        }

        if(visit(ctx.right) != null){
            ColumnName columnNameR = visit(ctx.right).getColumnNames().get(0);
                content.addSubCommandOfWheres(columnName,operation,columnNameR);
        }
        else{
            String valueR = ctx.right.getText();
            if(columnName != null){
                content.addSubCommandOfWheres(columnName,operation,valueR);
            }
            else{
                content.addSubCommandOfWheres(valueL,operation,valueR);
            }
        }
        return content;
    }

    @Override
    public CommandContent visitLikePredicate(MySqlParser.LikePredicateContext ctx) {
        CommandContent content = new CommandContent();
        ColumnName columnName = visit(ctx.predicate(0)).getColumnNames().get(0);
        String operation;
        if(ctx.NOT() != null){
            operation = "NOT LIKE";
        }
        else{
            operation = "LIKE";
        }
        String value = ctx.predicate(1).getText();
        content.addSubCommandOfWheres(columnName,operation,value);

        return content;
    }

    @Override
    public CommandContent visitBetweenPredicate(MySqlParser.BetweenPredicateContext ctx) {
        CommandContent content = new CommandContent();
        ColumnName columnName = visit(ctx.predicate(0)).getColumnNames().get(0);
        String operation = "BETWEEN";
        String value = ctx.predicate(1).getText();
        String value1 = ctx.predicate(2).getText();
        content.addSubCommandOfWheres(columnName,operation,value,value1);

        return content;
    }

    @Override
    public CommandContent visitFullId(MySqlParser.FullIdContext ctx) {
        CommandContent content = new CommandContent();
        content.addTempString(visit(ctx.uid(0)).getTempString(0));
        if(ctx.uid().size() > 1){
            content.addTempString(visit(ctx.uid(1)).getTempString(0));
        }
        else{
            if(ctx.DOT_ID() != null){
                content.addTempString(ctx.DOT_ID().getText().substring(1));
            }
        }
        return content;
    }

    @Override
    public CommandContent visitFullColumnName(MySqlParser.FullColumnNameContext ctx) {
        CommandContent content = new CommandContent();
        List<String> tempString = new ArrayList<>();

        tempString.add(visit(ctx.uid()).getTempString(0));
        for(MySqlParser.DottedIdContext c : ctx.dottedId()){
            tempString.add(visit(c).getTempString(0));
        }
        switch (tempString.size()){
            case 1:
                content.addColumnName(tempString.get(0),null,null,null);
                break;
            case 2:
                content.addColumnName(tempString.get(1),null,tempString.get(0),null);
                break;
            case 3:
                content.addColumnName(tempString.get(2),null,tempString.get(1),tempString.get(0));
        }
        return content;
    }

    @Override
    public CommandContent visitUid(MySqlParser.UidContext ctx) {
        CommandContent content = new CommandContent();
        if(ctx.simpleId() != null){
            content.addTempString(ctx.simpleId().getText());
        }
        return content;
    }

    @Override
    public CommandContent visitDottedId(MySqlParser.DottedIdContext ctx) {
        CommandContent content = new CommandContent();
        if(ctx.DOT_ID() != null){
            content.addTempString(ctx.DOT_ID().getText().substring(1));
        }
        else{
            content.addTempString(visit(ctx.uid()).getTempString(0));
        }
        return content;
    }

    /**
     * be used: FROM tableSources (WHERE whereExpr=expression)
     * tableSource(','tableSource)*
     */
    @Override
    public CommandContent visitTableSources(MySqlParser.TableSourcesContext ctx) {
        CommandContent content = new CommandContent();

        for(MySqlParser.TableSourceContext c : ctx.tableSource()){
            content.addTableName(visit(c).getTableNames().get(0));
        }

        return content;
    }

    /**
     * tableSourceItem joinPart*
     */
    @Override
    public CommandContent visitTableSourceBase(MySqlParser.TableSourceBaseContext ctx) {
        CommandContent content = new CommandContent();
        TableName left = visit(ctx.tableSourceItem()).getTableNames().get(0);
        content.addTableName(left);
        /**
         * Todo:joinPart
         */
        if(ctx.joinPart() != null){
            TableName right;
        }

        return content;
    }

    @Override
    public CommandContent visitAtomTableItem(MySqlParser.AtomTableItemContext ctx) {
        CommandContent content = new CommandContent();
        TableName tableName = visit(ctx.tableName()).getTableNames().get(0);
        String aliasName;
        if(ctx.uid()!=null){
            tableName.setAliasName(visit(ctx.uid()).getTempString().get(0));
        }
        content.addTableName(tableName);
        return content;
    }

    @Override
    public CommandContent visitTableName(MySqlParser.TableNameContext ctx) {
        CommandContent content = new CommandContent();
        List<String> tempString = visit(ctx.fullId()).getTempString();
        switch(tempString.size()){
            case 1:
                content.addTableName(tempString.get(0),null,null);
                break;
            case 2:
                content.addTableName(tempString.get(1),null,tempString.get(0));
                break;
        }
        return content;
    }

    @Override
    public CommandContent visitIndexColumnNames(MySqlParser.IndexColumnNamesContext ctx) {
        CommandContent content = new CommandContent();
        for(MySqlParser.IndexColumnNameContext c : ctx.indexColumnName()){
            content.addTempString(visit(c).getTempString(0));
        }
        return content;
    }

    @Override
    public CommandContent visitIndexColumnName(MySqlParser.IndexColumnNameContext ctx) {
        CommandContent content = new CommandContent();
        content.addTempString(visit(ctx.uid()).getTempString(0));
        return content;
    }

    @Override
    public CommandContent visitCreateDefinitions(MySqlParser.CreateDefinitionsContext ctx) {
        CommandContent content = new CommandContent();
        for(MySqlParser.CreateDefinitionContext c: ctx.createDefinition()){
            String columnName = visit(c).getTempString(0);
            String type = visit(c).getTempString(1);
            content.addTableInfo(columnName,type);
        }
        return content;
    }

    @Override
    public CommandContent visitColumnDeclaration(MySqlParser.ColumnDeclarationContext ctx) {
        CommandContent content = new CommandContent();
        String columnName = visit(ctx.uid()).getTempString(0);
        String type = visit(ctx.columnDefinition()).getTempString(0);

        content.addTempString(columnName);
        content.addTempString(type);

        return content;
    }

    @Override
    public CommandContent visitColumnDefinition(MySqlParser.ColumnDefinitionContext ctx) {
        String type = ctx.dataType().getText();
        CommandContent content = new CommandContent();
        content.addTempString(type);
        return content;
    }
}
