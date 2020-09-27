package com.bit.handler;

import com.bit.api.ApiManager;
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
import org.antlr.v4.runtime.tree.ParseTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/7 3:38 下午
 */
@Component
public class CommandHandler {

    @Autowired
    ApiManager apiManager;

    public CommandHandler() {}

    public List<HandlerResult> handle(String command) throws Exception {
        System.out.println("处理命令：" + command);
        if ("exit".equals(command)) {
            System.exit(0);
        }

        /**
         * 增加其他处理
         */
        List<CommandContent> commandContents = new ArrayList<>();

        CharStream stream = CharStreams.fromString(command.toUpperCase()); // 将命令读入字节符号流
        MySqlLexer lexer = new MySqlLexer(stream);//新建一个词法分析器，处理输入的字节流
        CommonTokenStream token = new CommonTokenStream(lexer);//新建一个词法符号的缓冲区，用于存储词法分析器将生成的词法符号
        MySqlParser parser = new MySqlParser(token);//新建一个语法分析器，处理词法符号缓冲区的内容
        parser.removeErrorListeners();
        parser.addErrorListener(new ErrorListener());
        ParseTree tree = parser.root();//针对root规则，开始语法分析，生成一颗树

        SqlCommandVisitor sqlCommandVisitor = new SqlCommandVisitor(commandContents);
        sqlCommandVisitor.visit(tree);


        /**
         * subCommandOfWhere no logical operator deal!!!!
         */
        List<HandlerResult> results = new ArrayList<>();
        SelectManager selectManager = new SelectManager(apiManager);
        for(CommandContent content : commandContents){
            long startTime = System.currentTimeMillis();
            HandlerResult handlerResult = new HandlerResult();
            List<String> columns = new ArrayList<>();
            List<Object> data = new ArrayList<>();
            switch (content.getOperation()) {
                case errorCommand:
                    throw new Exception("Not supported yet");
                case createDatabase:
                    apiManager.createDatabase(new Database(content.getDatabaseName(), null));
                    break;
                case createIndex:
                    for(String s : content.getIndexName().getColumnNames()) {
                        apiManager.createIndex(content.getIndexName().getTableName(), s);
                    }
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
                    String columnName = content.getIndexName().getColumnNames().get(0);
                    if (tableName != null && columnName != null) {
                        apiManager.deleteIndex(tableName, columnName);
                    }
                    break;
                case showDatabases:
                    columns.add("Database Name");
                    handlerResult.setColumns(columns);
                    List<String> databases = apiManager.showDatabases();
                    for(String t :databases){
                        Map<String,Comparable> tempMap = new HashMap<>();
                        tempMap.put("Database Name",t);
                        data.add(tempMap);
                    }
                    data.addAll(databases);
                    handlerResult.setData(data);
                    break;
                case showTables:
                    columns.add("Table Name");
                    handlerResult.setColumns(columns);
                    List<String> tables = apiManager.showTables();

                    for(String t :tables){
                        Map<String,Comparable> tempMap = new HashMap<>();
                        tempMap.put("Table Name",t);
                        data.add(tempMap);
                    }
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
                    Query query = selectManager.getQuery(content.getSubCommandOfWheres());
                    apiManager.deleteData(query, content.getTableNames().get(0).getTableName());
                    break;
                case update:
                    query = selectManager.getQuery(content.getSubCommandOfWheres());
                        /* if (subCommandOfWhere.getRightIsColumn() || subCommandOfWhere.getLeftIsValue() || subCommandOfWhere.getValueSecond() != null) {
                                throw new Exception("not supported yet");}
                          此处默认了一定是 column = value 的形式
                           */
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
                    List<TableData> result;
                    switch (content.getTableNames().size()){
                        case 2:
                            result = selectManager.join(content.getTableNames().get(0).getTableName(),
                                    content.getTableNames().get(0).getTableName(),
                                    content.getSubCommandOfWheres(),
                                    TableName.JoinType.innerJoin);
                            break;
                        case 1:
                            TableName temp = content.getTableNames().get(0);
                            if(temp.getNext() != null){
                                List<SubCommandOfWhere> temp1 = new ArrayList<>();
                                if(temp.getNext().getJoinExpression()!=null) {
                                    List<SubCommandOfWhere> temp2 = temp.getNext().getJoinExpression();
                                    SubCommandOfWhere temp3 = temp2.get(temp2.size() - 1);
                                    if(content.getSubCommandOfWheres().size() != 0){
                                        temp3.setLogicalOperation("AND");
                                    }
                                    temp1.addAll(temp2);
                                }
                                if(content.getSubCommandOfWheres() != null){
                                    temp1.addAll(content.getSubCommandOfWheres());
                                }
                                result = selectManager.join(temp.getTableName(),
                                        temp.getNext().getTableName(),
                                        temp1,
                                        temp.getJoinType());
                            }
                            else{
                                result = selectManager.single(content.getTableNames().get(0).getTableName(),
                                        content.getSubCommandOfWheres());
                            }
                            break;
                        default:
                            throw new Exception("Not Supported yet");
                    }



                    handlerResult =  selectManager.getResult(result,content);
                    break;
            }
            //handlerResult here
            long stopTime = System.currentTimeMillis();
            handlerResult.setTotalTime(stopTime - startTime);
            results.add(handlerResult);
        }

        return results;
    }


}
