package com.bit.handler;

import java.util.List;

public class SqlCommandVisitor extends MySqlParserBaseVisitor<CommandContent> {
    private List<CommandContent> commandContents;
    SqlCommandVisitor(List<CommandContent> contents){
        commandContents = contents;
    }

    /**
     * data manipulation language statement
     * here to add CommandContent List
     */

    /**
     * select statement
     * @param ctx
     * @return null
     */
    @Override
    public CommandContent visitSimpleSelect(MySqlParser.SimpleSelectContext ctx) {
        CommandContent commandContent = visit(ctx.querySpecification());
        commandContents.add(commandContent);
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

    /**
     * data manipulation language statement details
     * here to create CommandContent object
     */

    /**
     * querySpecification
     * @param ctx
     * @return CommandContent
     */
    @Override
    public CommandContent visitQuerySpecification(MySqlParser.QuerySpecificationContext ctx) {
        CommandContent content = new CommandContent();
        content.setOperation(CommandContent.Operation.select);

        for(MySqlParser.SelectSpecContext c : ctx.selectSpec()){
            content.addConfig(c.getText());
        }
        content.addColumnName(visit(ctx.selectElements()).getColumnNames());



        return content;
    }

    /**
     * detail's detail
     * @param ctx
     * @return CommandContent
     */
    @Override
    public CommandContent visitSelectElements(MySqlParser.SelectElementsContext ctx) {
        CommandContent content = new CommandContent();

        if(ctx.star.getText().equals("*")){
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
        List<String> tempString = visit(ctx.fullColumnName()).getTempString();
        String columnName;
        String tableName;
        String aliasName;
        String databaseName;
        switch (tempString.size()){
            case 1:
                columnName = tempString.get(0);
                tableName = null;
                databaseName = null;
                break;
            case 2:
                columnName = tempString.get(1);
                tableName = tempString.get(0);
                databaseName = null;
                break;
            case 3:
                columnName = tempString.get(2);
                tableName = tempString.get(1);
                databaseName = tempString.get(0);
                break;
            default:
                columnName = null;
                tableName = null;
                databaseName = null;
                /**
                 * error!
                 */
                break;
        }
        if(ctx.uid() != null){
            aliasName = visit(ctx.uid()).getTempString().get(0);
        }
        else{
            aliasName = null;
        }
        content.addColumnName(columnName,aliasName,tableName,databaseName);
        return content;
    }

    /**
     * TODO:visitSelectFunctionElement,visitExpressionElement
    */

    @Override
    public CommandContent visitFromClause(MySqlParser.FromClauseContext ctx) {
        CommandContent content = new CommandContent();
        content.addTableName(visit(ctx.tableSources()).getTableNames());
        return content;
    }

    /**
     * common component
     */
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
        content.addTempString(visit(ctx.uid()).getTempString(0));
        if(ctx.dottedId() != null){
            switch (ctx.dottedId().size()){
                case 1:
                    content.addTempString(visit(ctx.dottedId(0)).getTempString(0));
                    break;
                case 2:
                    content.addTempString(visit(ctx.dottedId(0)).getTempString(0));
                    content.addTempString(visit(ctx.dottedId(1)).getTempString(0));
                    break;
                default:
                    break;
            }
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
        List<String> tempString = visit(ctx.tableName()).getTempString();
        String tableName;
        String databaseName;
        String aliasName;
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
        if(ctx.uid()!=null){
            aliasName = visit(ctx.uid()).getTempString().get(0);
        }
        else{
            aliasName = null;
        }
        content.addTableName(tableName,aliasName,databaseName);
        return content;
    }

    @Override
    public CommandContent visitTableName(MySqlParser.TableNameContext ctx) {
        return visit(ctx.fullId());
    }
}
