package com.bit.table;

import com.bit.constance.DataType;
import com.bit.utils.FormatUtil;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/7 7:49 下午
 */
public class TableStore {

    private TableStore() {
    }

    private static TableStore tableStore;

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

//    public void getTables(String key, Map<String, Integer> value) {
//        TableMessage.Table.Builder builder = TableMessage.Table.newBuilder();
//
//        for (Map.Entry<String, Integer> entry : value.entrySet()) {
//            TableMessage.Type type = TableMessage.Type.newBuilder()
//                    .setKey(entry.getKey())
//                    .setType(entry.getValue()).build();
//            builder.addType(type);
//        }
//        builder.setName(key);
//        TableMessage.Table table = builder.build();
//        System.out.println(table.toByteArray().length);
//        FileOutputStream fileOutputStream = null;
//        try {
//            fileOutputStream = new FileOutputStream(new File("/tmp/table.db"), true);
//            table.writeDelimitedTo(fileOutputStream);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (fileOutputStream != null) {
//                try {
//                    fileOutputStream.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    public static void main(String[] args) {
//        Map<String, Integer> map = new HashMap();
//        map.put("name", DataType.STRING.ordinal());
//        TableStore.getInstance().createTable("老师", map);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File("/tmp/table.db"));
            TableMessage.Table table = TableMessage.Table.parseDelimitedFrom(fileInputStream);
            System.out.println(FormatUtil.bytes2String(table.getName().toByteArray()));
            table = TableMessage.Table.parseDelimitedFrom(fileInputStream);
            System.out.println(FormatUtil.bytes2String(table.getName().toByteArray()));
            table = TableMessage.Table.parseDelimitedFrom(fileInputStream);
            System.out.println(FormatUtil.bytes2String(table.getName().toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
