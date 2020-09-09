package com.bit.api.table;

import com.bit.constance.DBConfig;
import com.bit.constance.DataType;
import com.bit.exception.NoNameTableException;
import com.bit.exception.SameNameTableException;
import com.bit.model.Table;
import com.bit.utils.FormatUtil;
import com.bit.utils.PathUtil;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/7 7:49 下午
 */
public class TableStore {

    private TableStore() {
    }

    private static TableStore tableStore;

    private static List<Table> tableCache = null;

    private static String tablePosition = DBConfig.TABLE_POSITION;

    public static TableStore getInstance() {
        if (tableStore == null) {
            synchronized (TableStore.class) {
                if (tableStore == null) {
                    tableStore = new TableStore();
                    PathUtil.ensureFile(tablePosition);
                }
            }
        }
        return tableStore;
    }

    /**
     * @param newTable
     * @Description 创建表 并持久化
     */
    public void createTable(Table newTable) throws SameNameTableException {
        if (tableCache == null) {
            getTables();
        }
        for (Table table : tableCache) {
            if (table.getName().equals(newTable.getName())) {
                throw new SameNameTableException(newTable.getName() + " 已经存在该名字表，无法创建");
            }
        }
        List<Table> tables = new LinkedList<>();
        tables.add(newTable);
        storeToFile(tables);
    }

    public void deleteTable(String name) throws NoNameTableException {
        if (tableCache == null) {
            getTables();
        }
        for (Table table : tableCache) {
            if (table.getName().equals(name)) {
                tableCache.remove(table);
                storeToFile(tableCache, true);
                return;
            }
        }
        throw new NoNameTableException(name + " 没有该名字表，无法删除");
    }

    public void updateTable(Table newTable) throws NoNameTableException {
        if (tableCache == null) {
            getTables();
        }
        for (Table table : tableCache) {
            if (table.getName().equals(newTable.getName())) {
                tableCache.remove(table);
                tableCache.add(newTable);
                storeToFile(tableCache, true);
                return;
            }
        }
        throw new NoNameTableException(newTable.getName() + " 没有该名字表，无法更新");
    }

    /**
     * Description： 获取所有表,并缓存在内存中
     *
     * @return
     */
    public List<Table> getTables() {
        List<Table> tables = new LinkedList<>();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(tablePosition));
            while (true) {
                Table table = new Table();
                TableMessage.Table tableMessage = TableMessage.Table.parseDelimitedFrom(fileInputStream);
                if (tableMessage == null) {
                    break;
                }
                Map<String, Integer> typeMap = new HashMap<>();
                List<TableMessage.Type> typeList = tableMessage.getTypeList();
                for (TableMessage.Type type : typeList) {
                    typeMap.put(FormatUtil.bytes2String(type.getKey().toByteArray()), type.getType());
                }
                table.setName(FormatUtil.bytes2String(tableMessage.getName().toByteArray()));
                table.setType(typeMap);
                tables.add(table);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("读取完毕");
        }
        tableCache = tables;
        return tables;
    }

    private void storeToFile(List<Table> tables) {
        storeToFile(tables, false);
    }

    /**
     * 储存table到文件
     *
     * @param tables  储存的table
     * @param isCover 是否覆盖原文件
     */
    private void storeToFile(List<Table> tables, Boolean isCover) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(tablePosition), !isCover);
            for (Table table : tables) {
                Map<String, Integer> typeMap = table.getType();
                TableMessage.Table.Builder builder = TableMessage.Table.newBuilder();

                for (Map.Entry<String, Integer> entry : typeMap.entrySet()) {
                    TableMessage.Type type = TableMessage.Type.newBuilder()
                            .setKey(ByteString.copyFrom(FormatUtil.string2Bytes(entry.getKey())))
                            .setType(entry.getValue()).build();
                    builder.addType(type);
                }
                builder.setName(ByteString.copyFrom(FormatUtil.string2Bytes(table.getName())));
                TableMessage.Table tableMessage = builder.build();
                tableMessage.writeDelimitedTo(fileOutputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Table getTable(String name) {
        if (tableCache == null) {
            getTables();
        }
        for (Table table : tableCache) {
            if (table.getName().equals(name)) {
                return table;
            }
        }
        return null;
    }

    public static void main(String[] args) throws SameNameTableException, NoNameTableException {
        Table dogTable = TableStore.getInstance().getTable("dog");

        if (dogTable != null) {
            TableStore.getInstance().deleteTable("dog");
            System.out.println("删除已存在的dog表");
        }

        // 创建dog表
        dogTable = new Table();
        dogTable.setName("dog");

        Map<String, Integer> typeMap = new HashMap<>();
        typeMap.put("age", DataType.INT.ordinal());
        dogTable.setType(typeMap);

        TableStore.getInstance().createTable(dogTable);
        System.out.println("创建dog表");
        System.out.println("目前已存在的表：");
        System.out.println(TableStore.getInstance().getTables());

        // 给dog表增加一个字段
        dogTable.getType().put("id_num", DataType.LONG.ordinal());
        TableStore.getInstance().updateTable(dogTable);
        System.out.println("为dog表增加字段id_num");
        System.out.println("目前已存在的表：");
        System.out.println(TableStore.getInstance().getTables());
    }
}
