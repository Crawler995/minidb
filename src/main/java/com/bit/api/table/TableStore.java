package com.bit.api.table;

import com.bit.model.Table;
import com.bit.utils.FormatUtil;
import com.google.protobuf.ByteString;

import java.io.*;
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

    public static TableStore getInstance() {
        if (tableStore == null) {
            synchronized (TableStore.class) {
                if (tableStore == null) {
                    tableStore = new TableStore();
                }
            }
        }
        return tableStore;
    }

    /**
     * @param key   表名
     * @param value 表字段的名字和字段类型的map
     * @Description 创建表 并持久化
     */
    public void createTable(String key, Map<String, Integer> value) {
        TableMessage.Table.Builder builder = TableMessage.Table.newBuilder();

        for (Map.Entry<String, Integer> entry : value.entrySet()) {
            TableMessage.Type type = TableMessage.Type.newBuilder()
                    .setKey(ByteString.copyFrom(FormatUtil.string2Bytes(entry.getKey())))
                    .setType(entry.getValue()).build();
            builder.addType(type);
        }
        builder.setName(ByteString.copyFrom(FormatUtil.string2Bytes(key)));
        TableMessage.Table table = builder.build();
        System.out.println(table.toByteArray().length);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File("/tmp/table.db"), true);
            table.writeDelimitedTo(fileOutputStream);
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

    /**
     * Description： 获取所有表,并缓存在内存中
     * @return
     */
    public List<Table> getTables() {
        List<Table> tables = new LinkedList<>();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File("/tmp/table.db"));
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

    public Table getTable(String name) {
        if (tableCache == null) {
            getTables();
        }
        for (Table table : tableCache) {
            if (table.getName() == name) {
                return table;
            }
        }
        return null;
    }

    public static void main(String[] args) {
//        Map<String, Integer> map = new HashMap();
//        map.put("name", DataType.STRING.ordinal());
//        TableStore.getInstance().createTable("老师", map);
        System.out.println(TableStore.getInstance().getTables());
    }
}
