package com.bit.api.column;

import com.bit.api.table.TableStore;
import com.bit.constance.DBConfig;
import com.bit.model.Table;

import java.util.List;

/**
 * @author aerfafish
 * @date 2020/9/8 2:56 下午
 */
public class ColumnStore {

    private ColumnStore() {
    }

    private static ColumnStore columnStore;

    private static List<Table> columnCache = null;

    private String columnPosition = DBConfig.DB_POSITION+DBConfig.TABLE_POSITION;

    public static ColumnStore getInstance() {
        if (columnStore == null) {
            synchronized (ColumnStore.class) {
                if (columnStore == null) {
                    columnStore = new ColumnStore();
                }
            }
        }
        return columnStore;
    }
}
