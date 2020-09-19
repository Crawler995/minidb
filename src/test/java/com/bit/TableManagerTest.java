package com.bit;

import com.bit.api.manager.DatabaseManager;
import com.bit.api.manager.TableDataManager;
import com.bit.api.manager.TableManager;
import com.bit.constance.DataType;
import com.bit.exception.SameNameDatabaseException;
import com.bit.exception.SameNameTableException;
import com.bit.model.ColumnInfo;
import com.bit.model.Database;
import com.bit.model.Table;
import com.bit.model.TableData;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aerfafish
 * @date 2020/9/15 8:00 下午
 */

@SpringBootTest
public class TableManagerTest {

    @Autowired
    DatabaseManager databaseManager;

    @Test
    public void createDatabase() throws SameNameDatabaseException {
        databaseManager.createDatabase(new Database("database0", null));
    }

    @Test
    public void createTable() throws SameNameDatabaseException {
        TableManager tableManager = databaseManager.getTableManager("database0");
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
        TableManager tableManager = databaseManager.getTableManager("database0");
        TableDataManager tableDataManager = tableManager.getTableDataManager("table0");
        TableData tableData = new TableData();
        tableData.getData().put("id", 123L);
        tableDataManager.insert(tableData);
    }

    @Test
    public void selectData() {
        TableManager tableManager = databaseManager.getTableManager("database0");
        TableDataManager tableDataManager = tableManager.getTableDataManager("table0");
        TableData tableData = new TableData();
        tableData.getData().put("id", 123L);
        List<TableData> select = tableDataManager.select(tableData);
        System.out.println(select);
    }

}
