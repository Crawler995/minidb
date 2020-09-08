package com.bit.table;

import java.util.List;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/8 10:58 上午
 */
public interface TableService {
    List<TableMessage.Table> getTables();

    TableMessage.Table getTable(String name);

    void createTable(String name, Map<String, Integer> type);
}
