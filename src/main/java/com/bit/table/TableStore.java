package com.bit.table;

import com.bit.constance.DataType;

import java.io.File;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/7 7:49 下午
 */
public class TableStore {

    private TableStore() {}

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
     *
     * @param key 表名
     * @param value 表字段的名字和字段类型的map
     */
    public void createTable(String key, Map<String, Integer> value) {
        File file = new File("");
//        Table
    }

    public static void main(String[] args) {

    }
}
