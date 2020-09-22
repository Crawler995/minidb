package com.bit.handler;

import com.bit.api.ApiManager;
import com.bit.api.model.Criteria;
import com.bit.api.model.Query;
import com.bit.api.model.Update;
import com.bit.constance.DataType;
import com.bit.model.ColumnInfo;
import com.bit.model.Database;
import com.bit.model.Table;
import com.bit.model.TableData;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author aerfafish
 * @date 2020/9/7 3:38 下午
 */
@Component
public class CommandHandler {

    @Autowired
    ApiManager apiManager;

    public CommandHandler() {}

    public HandlerResult handle(String command) throws Exception {
        System.out.println("处理命令：" + command);
        if ("exit".equals(command)) {
            System.exit(0);
        }

        /**
         * 增加其他处理
         */

       // String[] keyWord = {"select"}
        List<CommandContent> commandContents = new ArrayList<>();

        CharStream stream = CharStreams.fromString(command.toUpperCase()); // 将命令读入字节符号流
        MySqlLexer lexer = new MySqlLexer(stream);//新建一个词法分析器，处理输入的字节流
        CommonTokenStream token = new CommonTokenStream(lexer);//新建一个词法符号的缓冲区，用于存储词法分析器将生成的词法符号
        MySqlParser parser = new MySqlParser(token);//新建一个语法分析器，处理词法符号缓冲区的内容
        ParseTree tree = parser.root();//针对root规则，开始语法分析，生成一颗树

        SqlCommandVisitor sqlCommandVisitor = new SqlCommandVisitor(commandContents);
        sqlCommandVisitor.visit(tree);


        /**
         * subCommandOfWhere no logical operator deal!!!!
         */

        for(CommandContent content : commandContents){
            HandlerResult handlerResult = new HandlerResult();
            List<String> columns = new ArrayList<>();
            List<Object> data = new ArrayList<>();
            switch (content.getOperation()) {
                case errorCommand:
                    break;
                case createDatabase:
                    apiManager.createDatabase(new Database(content.getDatabaseName(), null));
                    break;
                case createIndex:
                    apiManager.createIndex(content.getIndexName().getTableName(), content.getIndexName().getColumnName());
                    break;
                case createTable:
                    List<ColumnInfo> columnInfos = new ArrayList<>();
                    List<TableCreateInfo> tableCreateInfos = content.getTableCreateInfo();
                    for (TableCreateInfo tableCreateInfo : tableCreateInfos) {
                        ColumnInfo columnInfo = new ColumnInfo();
                        columnInfo.setColumnName(tableCreateInfo.getColumnName().trim());
                        String type = tableCreateInfo.getType();
                        columnInfos.add(columnInfo);
                        switch (type) {
                            case "INT":
                                columnInfo.setType(DataType.INT);
                                break;
                            case "DOUBLE":
                                columnInfo.setType(DataType.DOUBLE);
                                break;
                            case "LONG":
                                columnInfo.setType(DataType.LONG);
                                break;
                            case "VARCHAR":
                            case "CHAR":
                                columnInfo.setType(DataType.STRING);
                                break;
                            default:
                                columnInfo.setType(DataType.ERROR);
                                break;
                        }
                    }

                    Table table = new Table();
                    table.setColumnInfo(columnInfos);
                    table.setTableName(content.getTableNames().get(0).getTableName());
                    apiManager.createTable(table);
                    break;
                case dropDatabase:
                    apiManager.deleteDatabase(content.getDatabaseName());
                    break;
                case dropTable:
                    for (int i = 0; i < content.getTableNames().size(); i++) {
                        apiManager.deleteTable(content.getTableNames().get(i).getTableName());
                    }
                    break;

                case dropIndex:
                    String tableName = content.getIndexName().getTableName();
                    String columnName = content.getIndexName().getColumnName();
                    if (tableName != null && columnName != null) {
                        apiManager.deleteIndex(tableName, columnName);
                    }
                    break;
                case showDatabases:
                    columns.add("Database Name");
                    handlerResult.setColumns(columns);
                    List<String> databases = apiManager.showDatabases();
                    data.addAll(databases);
                    handlerResult.setData(data);
                    break;
                case showTables:
                    columns.add("Database Name");
                    handlerResult.setColumns(columns);
                    List<String> tables = apiManager.showTables();
                    data.addAll(tables);
                    handlerResult.setData(data);
                    break;
                case use:
                    apiManager.useDatabase(content.getDatabaseName());
                    break;
                case insert:
                    if (content.insertedColumn.size() == 0) {
                        /**
                         * api not supported
                         */
                        throw new Exception("insert into tableName values (...): no column names");
                    }
                    List<String> insertColumn = content.getInsertedColumn();
                    for (List<String> insertValues : content.getInsertedColumnValue()) {
                        if (insertColumn.size() != insertValues.size()) {
                            throw new Exception("parse error");
                        }
                        Update update = new Update();
                        for (int i = 0; i < insertColumn.size(); i++) {
                            update.set(insertColumn.get(i), insertValues.get(i));
                        }
                        apiManager.insertData(update, content.getTableNames().get(0).getTableName());
                    }
                    break;
                case delete:
                    Query query = new Query();
                    for (SubCommandOfWhere subCommandOfWhere : content.getSubCommandOfWheres()) {
                        if (subCommandOfWhere.getRightIsColumn() || subCommandOfWhere.getLeftIsValue() || subCommandOfWhere.getValueSecond() != null) {
                            throw new Exception("not supported yet");
                        }
                        columnName = subCommandOfWhere.getColumnNameLeft().getColumnName();
                        String value = subCommandOfWhere.getValueFirst();
                        String operate = subCommandOfWhere.getOperation();
                        if (operate.equals(">")) {
                            query.addCriteria(Criteria.where(columnName).gte(value));
                        }
                        if (operate.equals(">=")) {
                            query.addCriteria(Criteria.where(columnName).lte(value));
                        }
                        if (operate.equals("=")) {
                            query.addCriteria(Criteria.where(columnName).is(value));
                        }
                        if (operate.equals("<")) {
                            query.addCriteria(Criteria.where(columnName).lt(value));
                        }
                        if (operate.equals("<=")) {
                            query.addCriteria(Criteria.where(columnName).lte(value));
                        }
                        if (operate.equals("!=")) {
                            query.addCriteria(Criteria.where(columnName).ne(value));
                        }
                    }
                    apiManager.deleteData(query, content.getTableNames().get(0).getTableName());
                    break;
                case update:
                    query = new Query();
                    if (content.getSubCommandOfWheres().size() != 0) {
                        for (SubCommandOfWhere subCommandOfWhere : content.getSubCommandOfWheres()) {
                            if (subCommandOfWhere.getRightIsColumn() || subCommandOfWhere.getLeftIsValue() || subCommandOfWhere.getValueSecond() != null) {
                                throw new Exception("not supported yet");
                            }
                            columnName = subCommandOfWhere.getColumnNameLeft().getColumnName();
                            String value = subCommandOfWhere.getValueFirst();
                            String operate = subCommandOfWhere.getOperation();
                            if (operate.equals(">")) {
                                query.addCriteria(Criteria.where(columnName).gte(value));
                            }
                            if (operate.equals(">=")) {
                                query.addCriteria(Criteria.where(columnName).lte(value));
                            }
                            if (operate.equals("=")) {
                                query.addCriteria(Criteria.where(columnName).is(value));
                            }
                            if (operate.equals("<")) {
                                query.addCriteria(Criteria.where(columnName).lt(value));
                            }
                            if (operate.equals("<=")) {
                                query.addCriteria(Criteria.where(columnName).lte(value));
                            }
                            if (operate.equals("!=")) {
                                query.addCriteria(Criteria.where(columnName).ne(value));
                            }
                        }
                    }
                    Update update = new Update();
                    for (SubCommandOfWhere updateElement : content.getUpdateElement()) {
                        if (updateElement.getLeftIsValue() || updateElement.getRightIsColumn() || updateElement.getValueSecond() != null) {
                            throw new Exception("not supported yet");
                        }
                        columnName = updateElement.getColumnNameLeft().getColumnName();
                        String value = updateElement.getValueFirst();
                        update.set(columnName, value);
                    }

                    apiManager.updateData(query, update, content.getTableNames().get(0).getTableName());
                    break;
                case select:
                    Map<String,TableData> tableDataMap = new HashMap<>();
                    for(TableName tn : content.getTableNames()){
                        if(tn.getNext() == null) {
                            tableDataMap.put(tn.getTableName(), null);
                        }
                        else{

                        }
                    }





                    query = new Query();
                    if (content.getSubCommandOfWheres().size() != 0) {
                        for (SubCommandOfWhere subCommandOfWhere : content.getSubCommandOfWheres()) {
                            if (subCommandOfWhere.getRightIsColumn() || subCommandOfWhere.getLeftIsValue() || subCommandOfWhere.getValueSecond() != null) {
                                throw new Exception("not supported yet");
                            }
                            columnName = subCommandOfWhere.getColumnNameLeft().getColumnName();
                            String value = subCommandOfWhere.getValueFirst();
                            String operate = subCommandOfWhere.getOperation();
                            if (operate.equals(">")) {
                                query.addCriteria(Criteria.where(columnName).gte(value));
                            }
                            if (operate.equals(">=")) {
                                query.addCriteria(Criteria.where(columnName).lte(value));
                            }
                            if (operate.equals("=")) {
                                query.addCriteria(Criteria.where(columnName).is(value));
                            }
                            if (operate.equals("<")) {
                                query.addCriteria(Criteria.where(columnName).lt(value));
                            }
                            if (operate.equals("<=")) {
                                query.addCriteria(Criteria.where(columnName).lte(value));
                            }
                            if (operate.equals("!=")) {
                                query.addCriteria(Criteria.where(columnName).ne(value));
                            }
                        }
                    }
                    List<String> columnNames = new ArrayList<>();
                    for(ColumnName name : content.getColumnNames()){
                        columnNames.add(name.getColumnName());
                    }
                    List<TableData> tableDatas = apiManager.selectData(query, content.getTableNames().get(0).getTableName());
                    for (TableData tableData : tableDatas) {
                        List<Object> tempData = new ArrayList<>();
                        for(int i = 0; i < columnNames.size(); i++) {
                            tempData.add(tableData.getData().get(columnNames.get(i)));
                        }
                        data.add(tempData);
                    }
                    handlerResult.setData(data);
                    handlerResult.setColumns(columnNames);
                    break;
            }
            //handlerResult here
            return handlerResult;
        }





        return null;
    }


}
