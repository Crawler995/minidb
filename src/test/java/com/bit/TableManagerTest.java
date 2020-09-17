package com.bit;

import com.bit.api.table.DatabaseManager;
import com.bit.api.table.TableDataManager;
import com.bit.api.table.TableManager;
import com.bit.constance.DataType;
import com.bit.exception.SameNameDatabaseException;
import com.bit.exception.SameNameTableException;
import com.bit.model.ColumnInfo;
import com.bit.model.Database;
import com.bit.model.Table;
import com.bit.model.TableData;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/15 8:00 下午
 */
public class TableManagerTest {

    @Test
    public void createDatabase() throws SameNameDatabaseException {
        DatabaseManager.getInstance().createDatabase(new Database("database0", null));
    }

    @Test
    public void createTable() throws SameNameDatabaseException {
        TableManager tableManager = DatabaseManager.getInstance().getTableManager("database0");
        List<ColumnInfo> columnInfos = new ArrayList<>();
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setColumnName("id");
        columnInfo.setHasIndex(false);
        columnInfo.setType(DataType.LONG);
        columnInfos.add(columnInfo);
        Table table = new Table();
        table.setFilePath("/tmp/minidb/table/table0");
        table.setTableName("table0");
        table.setColumnInfo(columnInfos);
        try {
            tableManager.createTable(table);
        } catch (SameNameTableException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void insertData() {
        TableManager tableManager = DatabaseManager.getInstance().getTableManager("database0");
        TableDataManager tableDataManager = tableManager.getTableDataManager("table0");
        TableData tableData = new TableData();
        tableData.getData().put("id", 123L);
        tableDataManager.insert(tableData);
    }

    @Test
    public void selectData() {
        TableManager tableManager = DatabaseManager.getInstance().getTableManager("database0");
        TableDataManager tableDataManager = tableManager.getTableDataManager("table0");
        TableData tableData = new TableData();
        tableData.getData().put("id", 123L);
        List<TableData> select = tableDataManager.select(tableData);
        System.out.println(select);
    }

}
