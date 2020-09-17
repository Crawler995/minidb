package com.bit;

import com.bit.api.table.DatabaseManager;
import com.bit.api.table.TableDataManager;
import com.bit.api.table.TableManager;
import com.bit.bplustree.BplusTree;
import com.bit.bplustree.Point;
import com.bit.constance.DataType;
import com.bit.exception.SameNameDatabaseException;
import com.bit.exception.SameNameTableException;
import com.bit.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author aerfafish
 * @date 2020/9/17 1:02 上午
 */
public class DataTest {

    public static void main(String[] args) {
        BplusTree tree = null;
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
                    DatabaseManager.getInstance().createDatabase(new Database(databaseName, null));
                } catch (SameNameDatabaseException e) {
                    e.printStackTrace();
                }
            }
            if (command.equals("create table")) {
                System.out.print("database > ");
                String databaseName = scanner.nextLine();
                TableManager tableManager = DatabaseManager.getInstance().getTableManager(databaseName);
                System.out.print("table > ");
                String tableName = scanner.nextLine();
                Table table = new Table();
                table.setTableName(tableName);
                List<ColumnInfo> columnInfos = new ArrayList<>();
                while (true) {
                    System.out.print("columnName > ");
                    String columnName = scanner.nextLine();
                    if (columnName.equals("exit")) {
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
                table.setFilePath("/tmp/table/"+tableName);
                try {
                    tableManager.createTable(table);
                } catch (SameNameTableException e) {
                    e.printStackTrace();
                }
            }
            if (command.equals("insert data")) {
                System.out.print("database > ");
                String databaseName = scanner.nextLine();
                TableManager tableManager = DatabaseManager.getInstance().getTableManager(databaseName);
                System.out.print("table > ");
                String tableName = scanner.nextLine();
                TableDataManager tableDataManager = tableManager.getTableDataManager(tableName);
                TableData tableData = new TableData();
                while (true) {
                    System.out.print("columnName > ");
                    String columnName = scanner.nextLine();
                    if (columnName.equals("exit")) {
                        break;
                    }
                    System.out.print("value > ");
                    String value = scanner.nextLine();
                    Table table = tableDataManager.getTable();
                    for (ColumnInfo columnInfo : table.getColumnInfo()) {
                        if (columnInfo.getColumnName().equals(columnName)) {
                            if (columnInfo.getType() == DataType.LONG) {
                                Long columnLongValue = Long.parseLong(value);
                                tableData.getData().put(columnName, columnLongValue);
                                break;
                            }
                            if (columnInfo.getType() == DataType.DOUBLE) {
                                Double columnDoubleValue = Double.parseDouble(value);
                                tableData.getData().put(columnName, columnDoubleValue);
                                break;
                            }
                            if (columnInfo.getType() == DataType.STRING) {
                                tableData.getData().put(columnName, value);
                                break;
                            }
                        }
                    }
                }
                tableDataManager.insert(tableData);
            }
            if (command.equals("select data")) {
                System.out.print("database > ");
                String databaseName = scanner.nextLine();
                TableManager tableManager = DatabaseManager.getInstance().getTableManager(databaseName);
                if (tableManager == null) {
                    System.out.println("数据库不存在");
                }
                System.out.print("table > ");
                String tableName = scanner.nextLine();
                TableDataManager tableDataManager = tableManager.getTableDataManager(tableName);
                if (tableDataManager == null) {
                    System.out.println("表不存在");
                    break;
                }
                TableData tableData = new TableData();
                while (true) {
                    System.out.print("columnName > ");
                    String columnName = scanner.nextLine();
                    if (columnName.equals("exit")) {
                        break;
                    }
                    System.out.print("value > ");
                    String value = scanner.nextLine();
                    Table table = tableDataManager.getTable();
                    if (table == null) {
                        System.out.println("不存在该表");
                        break;
                    }
                    for (ColumnInfo columnInfo : table.getColumnInfo()) {
                        if (columnInfo.getColumnName().equals(columnName)) {
                            if (columnInfo.getType() == DataType.LONG) {
                                Long columnLongValue = Long.parseLong(value);
                                tableData.getData().put(columnName, columnLongValue);
                                break;
                            }
                            if (columnInfo.getType() == DataType.DOUBLE) {
                                Double columnDoubleValue = Double.parseDouble(value);
                                tableData.getData().put(columnName, columnDoubleValue);
                                break;
                            }
                            if (columnInfo.getType() == DataType.STRING) {
                                tableData.getData().put(columnName, value);
                                break;
                            }
                        }
                    }
                }
                System.out.println(tableDataManager.select(tableData));
            }
            if (command.equals("select tables")) {
                System.out.print("database > ");
                String databaseName = scanner.nextLine();
                TableManager tableManager = DatabaseManager.getInstance().getTableManager(databaseName);
                System.out.println(tableManager.getTables());
            }
        }
    }
}
