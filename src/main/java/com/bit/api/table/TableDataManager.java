package com.bit.api.table;

import com.bit.model.TableData;

import java.util.HashMap;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/15 9:17 下午
 */
public class TableDataManager {

    private String dataFilePath;

    public TableDataManager(String filePath) {
        this.dataFilePath = filePath;
    }

    private Map<Long, TableData> tableDataCache = new HashMap<>();

    private void storeToFile() {

    }
}
