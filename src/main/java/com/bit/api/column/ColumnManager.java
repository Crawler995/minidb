package com.bit.api.column;

import com.bit.constance.DBConfig;
import com.bit.model.Column;
import com.bit.model.Table;

import java.util.List;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/8 5:45 下午
 */
public class ColumnManager {
    private static ColumnManager columnManager;

    private Map<String, ColumnStore> columnsCache = null;

    private String columnPosition = DBConfig.DB_POSITION + DBConfig.COLUMN_POSITION;

    public static ColumnManager getInstance() {
        if (columnManager == null) {
            synchronized (ColumnStore.class) {
                if (columnManager == null) {
                    columnManager = new ColumnManager();
                }
            }
        }
        return columnManager;
    }

    public void insert(Table table, Column column) {
        ColumnStore columnStore = columnsCache.get(table.getName());
        if (columnStore == null) {
            columnStore = new ColumnStore(table);
            columnsCache.put(table.getName(), columnStore);
        }
        columnStore.insertColumn(column);
    }
}
