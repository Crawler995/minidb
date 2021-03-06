package com.bit;

import com.bit.api.ApiManager;
import com.bit.api.model.Criteria;
import com.bit.api.model.Query;
import com.bit.api.model.Update;
import com.bit.constance.DataType;
import com.bit.exception.NoNameDatabaseException;
import com.bit.exception.NoNameTableException;
import com.bit.model.ColumnInfo;
import com.bit.model.Database;
import com.bit.model.Table;
import com.bit.model.TableData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author aerfafish
 * @date 2020/9/17 1:02 上午
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataTest {

    @Autowired
    ApiManager apiManager;

    @Test
    public void dataTest() throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("db > ");
            //输入命令
            String command = scanner.nextLine();
            /**
             * Deal command
             * 处理输入命令，调用指定接口
             */
            if (command.equals("create database")) {
                System.out.print("database > ");
                String databaseName = scanner.nextLine();
                try {
                    apiManager.createDatabase(new Database(databaseName, null));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (command.equals("use database")) {
                System.out.print("database > ");
                String databaseName = scanner.nextLine();
                try {
                    apiManager.useDatabase(databaseName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (command.equals("create table")) {
                System.out.print("table > ");
                String tableName = scanner.nextLine();
                Table table = new Table();
                table.setTableName(tableName);
                List<ColumnInfo> columnInfos = new ArrayList<>();
                while (true) {
                    System.out.print("columnName > ");
                    String columnName = scanner.nextLine();
                    if (columnName.equals("")) {
                        break;
                    }
                    ColumnInfo columnInfo = new ColumnInfo();
                    columnInfo.setColumnName(columnName);
                    System.out.print("type > ");
                    String type = scanner.nextLine();
                    if (type.equals("String")) {
                        columnInfo.setType(DataType.STRING);
                    }
                    if (type.equals("Long")) {
                        columnInfo.setType(DataType.LONG);
                    }
                    if (type.equals("Double")) {
                        columnInfo.setType(DataType.DOUBLE);
                    }
                    columnInfos.add(columnInfo);
                }
                table.setColumnInfo(columnInfos);
                try {
                    apiManager.createTable(table);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (command.equals("insert data")) {
                System.out.print("table > ");
                String tableName = scanner.nextLine();
                Update update = new Update();
                while (true) {
                    System.out.print("columnName > ");
                    String columnName = scanner.nextLine();
                    if (columnName.equals("")) {
                        break;
                    }
                    System.out.print("value > ");
                    String value = scanner.nextLine();
                    update.getModifyData().put(columnName, value);
                }
                try {
                    apiManager.insertData(update, tableName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (command.equals("select data")) {
                try {
                    System.out.print("table > ");
                    String tableName = scanner.nextLine();
                    Query query = new Query();
                    while (true) {
                        System.out.print("columnName > ");
                        String columnName = scanner.nextLine();
                        if (columnName.equals("")) {
                            break;
                        }
                        System.out.print("value > ");
                        String value = scanner.nextLine();
                        System.out.print("operate > ");
                        String operate = scanner.nextLine();
                        if (operate.equals(">")) {
                            query.addCriteria(Criteria.where(columnName).gt(value));
                        }
                        if (operate.equals(">=")) {
                            query.addCriteria(Criteria.where(columnName).gte(value));
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
                    long startTime = System.currentTimeMillis();
                    System.out.println(apiManager.selectData(query, tableName));
                    System.out.println("花费时间：" + (System.currentTimeMillis() - startTime));
                } catch (Exception ignored) {

                }

            }
            if (command.equals("select tables")) {
                System.out.println(apiManager.showTables());
            }

            if (command.equals("update database")) {
                System.out.print("database > ");
                String databaseName = scanner.nextLine();
                Database originDatabase = new Database();
                originDatabase.setDatabaseName(databaseName);
                System.out.print("new database > ");
                String newDatabaseName = scanner.nextLine();
                Database newDatabase = new Database();
                newDatabase.setDatabaseName(newDatabaseName);
                System.out.print("new database filePath > ");
                String filePath = scanner.nextLine();
                if (!filePath.equals("")) {
                    newDatabase.setFilePath(filePath);
                }
                try {
                    apiManager.updateDatabase(originDatabase, newDatabase);
                } catch (NoNameDatabaseException e) {
                    e.printStackTrace();
                }
            }

            if (command.equals("delete database")) {
                System.out.print("database > ");
                String databaseName = scanner.nextLine();
                try {
                    apiManager.deleteDatabase(databaseName);
                } catch (NoNameDatabaseException e) {
                    e.printStackTrace();
                }
            }

            if (command.equals("delete table")) {
                System.out.print("table > ");
                String tableName = scanner.nextLine();
                try {
                    apiManager.deleteTable(tableName);
                } catch (NoNameTableException e) {
                    e.printStackTrace();
                }
            }

            if (command.equals("delete data")) {
                System.out.print("table > ");
                String tableName = scanner.nextLine();
                Query query = new Query();
                while (true) {
                    System.out.print("columnName > ");
                    String columnName = scanner.nextLine();
                    if (columnName.equals("")) {
                        break;
                    }
                    System.out.print("value > ");
                    String value = scanner.nextLine();
                    System.out.print("operate > ");
                    String operate = scanner.nextLine();
                    if (operate.equals(">")) {
                        query.addCriteria(Criteria.where(columnName).gt(value));
                    }
                    if (operate.equals(">=")) {
                        query.addCriteria(Criteria.where(columnName).gte(value));
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
                apiManager.deleteData(query, tableName);
            }

            if (command.equals("update data")) {
                System.out.print("table > ");
                String tableName = scanner.nextLine();

                Update update = new Update();
                Query query = new Query();
                while (true) {
                    System.out.print("columnName > ");
                    String columnName = scanner.nextLine();
                    if (columnName.equals("")) {
                        break;
                    }
                    System.out.print("value > ");
                    String value = scanner.nextLine();
                    System.out.print("operate > ");
                    String operate = scanner.nextLine();
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
                while (true) {
                    System.out.print("columnName > ");
                    String columnName = scanner.nextLine();
                    if (columnName.equals("")) {
                        break;
                    }
                    System.out.print("value > ");
                    String value = scanner.nextLine();
                    update.getModifyData().put(columnName, value);
                }
                apiManager.updateData(query, update, tableName);
            }

            if (command.equals("create index")) {
                System.out.print("table > ");
                String tableName = scanner.nextLine();
                TableData tableData = new TableData();
                System.out.print("columnName > ");
                String columnName = scanner.nextLine();
                System.out.print("filePath > ");
                String filePath = scanner.nextLine();
                if (filePath.equals("")) {
                    filePath = null;
                }
                try {
                    apiManager.createIndex(tableName, columnName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (command.equals("delete index")) {
                System.out.print("table > ");
                String tableName = scanner.nextLine();
                System.out.print("columnName > ");
                String columnName = scanner.nextLine();
                try {
                    apiManager.deleteIndex(tableName, columnName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void insertTest() throws Exception {
        apiManager.createDatabase(new Database("DB1", null));
        apiManager.useDatabase("DB1");

        Table table1 = new Table();
        List<ColumnInfo> columnInfos1 = new ArrayList<>();
        columnInfos1.add(new  ColumnInfo("TABLE1_ID", DataType.INT));
        columnInfos1.add(new ColumnInfo("TABLE1_LAST_NAME", DataType.STRING));
        columnInfos1.add(new ColumnInfo("TABLE1_FIRST_NAME", DataType.STRING));
        columnInfos1.add(new ColumnInfo("TABLE1_ADDRESS", DataType.STRING));
        columnInfos1.add(new ColumnInfo("TABLE1_CITY", DataType.STRING));
        columnInfos1.add(new ColumnInfo("TABLE1_AGE", DataType.INT));
        table1.setColumnInfo(columnInfos1);
        table1.setTableName("TABLE1");
        apiManager.createTable(table1);

        Table table2 = new Table();
        List<ColumnInfo> columnInfos2 = new ArrayList<>();
        columnInfos2.add(new ColumnInfo("TABLE2_ID", DataType.INT));
        columnInfos2.add(new ColumnInfo("TABLE2_FRIEND_ID", DataType.INT));
        columnInfos2.add(new ColumnInfo("TABLE2_FIRST_NAME", DataType.STRING));
        columnInfos2.add(new ColumnInfo("TABLE2_LAST_NAME", DataType.STRING));
        table2.setColumnInfo(columnInfos2);
        table2.setTableName("TABLE2");
        apiManager.createTable(table2);
        long startTime = System.currentTimeMillis();
        for (long i = 0; i < 20000; i++) {
            Update update = new Update();
            update.set("TABLE1_ID", i+"");
            update.set("TABLE1_LAST_NAME", "last"+(i));
            update.set("TABLE1_FIRST_NAME", "first"+(i));
            update.set("TABLE1_AGE", (20+1)+"");
            update.set("TABLE1_ADDRESS", "address"+(i+1));
            update.set("TABLE1_CITY", "city"+(i+1));
            apiManager.insertData(update, "TABLE1");
        }
        for (long i = 0; i < 20000; i++) {
            Update update = new Update();
            update.set("TABLE2_ID", i+"");
            update.set("TABLE2_FRIEND_ID", i+"");
            update.set("TABLE2_LAST_NAME", "LAST"+(i));
            update.set("TABLE2_FIRST_NAME", "FIRST"+(i));
            apiManager.insertData(update, "TABLE2");
        }
        long endTime = System.currentTimeMillis();
        System.out.println("插入数据花费时间：" + (endTime-startTime));

//        startTime = System.currentTimeMillis();
//        Query query = new Query();
//        query.addCriteria(Criteria.where("id").is("23000"));
//        System.out.println(apiManager.selectData(query, "student"));
//        endTime = System.currentTimeMillis();
//        System.out.println("没有索引查询花费时间：" + (endTime-startTime));
//
//        startTime = System.currentTimeMillis();
//        apiManager.createIndex("student", "id");
//        endTime = System.currentTimeMillis();
//        System.out.println("建立索引花费时间：" + (endTime-startTime));
//
//        startTime = System.currentTimeMillis();
//        query = new Query();
//        query.addCriteria(Criteria.where("id").is("122000"));
//        System.out.println(apiManager.selectData(query, "student"));
//        endTime = System.currentTimeMillis();
//        System.out.println("存在索引查询花费时间：" + (endTime-startTime));


    }
}
