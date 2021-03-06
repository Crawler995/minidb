package com.bit.api.manager;

import com.bit.constance.DBConfig;
import com.bit.exception.NoNameTableException;
import com.bit.exception.SameNameTableException;
import com.bit.model.ColumnInfo;
import com.bit.model.Table;
import com.bit.model.TableInfo;
import com.bit.utils.FileUtil;
import com.bit.utils.KryoUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/7 7:49 下午
 */
public class TableManager {

    public TableManager() {
    }

    public TableManager(String databaseName, String filePath) {
        this.tableInfoPath = filePath;
        this.databaseName = databaseName;
        initTableInfo();
    }

    private String tableInfoPath = "";

    private String databaseName = "";

    private Map<String, TableDataManager> tableCache = new HashMap<>();

    TableInfo tableInfo = null;

    public TableDataManager getTableDataManager(String tableName) throws Exception {
        TableDataManager tableDataManager = tableCache.get(tableName);
        if (tableDataManager == null) {
            for (Table table : tableInfo.getTables()) {
                if (table.getTableName().equals(tableName)) {
                    tableDataManager = new TableDataManager(table);
                    tableCache.put(tableName, tableDataManager);
                }
            }
        }
        if (tableDataManager == null) {
            throw new Exception("Table '"+databaseName+"."+tableName+"' doesn't exist");
        }
        return tableDataManager;
    }

    public void createTable(Table table) throws Exception {
        if (containTable(table.getTableName())) {
            throw new SameNameTableException("Table '"+table.getTableName()+"' already exists");
        }
        if (table.getColumnInfo().size() == 0) {
            throw new Exception("请添加至少一列");
        }
        for (ColumnInfo columnInfo : table.getColumnInfo()) {
            if (columnInfo.getColumnName() == null) {
                throw new Exception("缺少列名，请指定");
            }
        }
        if (table.getFilePath() == null) {
            table.setFilePath(DBConfig.TABLE_POSITION + "/" + databaseName + "/" + table.getTableName());
        }
        TableDataManager tableDataManager = new TableDataManager(table);
        tableCache.put(table.getTableName(), tableDataManager);
        tableInfo.getTables().add(table);
        // 创建ColumnManager
        storeToFile();
    }

    public void deleteTable(String tableName) throws NoNameTableException {
        // 如果当前存在该数据库
        Table table = removeTable(tableName);
        if (table == null) {
            throw new NoNameTableException("不存在该表，无法删除");
        }
        // todo: 删除数据
        String filePath = table.getFilePath();
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        storeToFile();
    }

    public void updateTable(Table originTable, Table newTable) throws NoNameTableException {
        // 如果当前存在该数据库
        Table table = removeTable(originTable.getTableName());
        if (table == null) {
            throw new NoNameTableException("不存在该数据库，无法修改");
        }
        // 修改全局储存文件中的数据库名
        tableInfo.getTables().add(newTable);
        /**
         * todo: 无法适应字段改变
         */
        // 移除columnManager 并加入一个新的
        storeToFile();
    }

    /**
     * Description： 获取所有表,并缓存在内存中
     *
     * @return
     */
    public List<String> getTables() {
        List<String> tables = new ArrayList<>();
        for (Table table : tableInfo.getTables()) {
            String tableName = table.getTableName();
            if (tableName != null && !"".equals(tableName)) {
                tables.add(tableName);
            }
        }
        return tables;
    }

    public void createIndex(String tableName, String columnName, String filePath) throws Exception {

        getTableDataManager(tableName).createIndex(columnName, filePath);
        storeToFile();
    }

    public void deleteIndex(String tableName, String columnName) throws Exception {
        getTableDataManager(tableName).deleteIndex(columnName);
        storeToFile();
    }

    private void storeToFile() {
        byte[] bytes = KryoUtil.serialize(tableInfo);
        FileOutputStream fileOutputStream = FileUtil.getFileOutputStream(tableInfoPath);
        FileUtil.writeFileByte(fileOutputStream, bytes);
        FileUtil.closeOutputSteam(fileOutputStream);
    }

    private Boolean containTable(String tableName) {
        List<Table> tables = tableInfo.getTables();
        for (Table table : tables) {
            if (table.getTableName().equals(tableName)) {
                return true;
            }
        }
        return false;
    }

    private Table getTable(String tableName) {
        List<Table> tables = tableInfo.getTables();
        for (Table table : tables) {
            if (table.getTableName().equals(tableName)) {
                return table;
            }
        }
        return null;
    }

    private Table removeTable(String tableName) {
        List<Table> tables = tableInfo.getTables();
        for (Table table : tables) {
            if (table.getTableName().equals(tableName)) {
                tables.remove(table);
                return table;
            }
        }
        return null;
    }

    private void initTableInfo() {
        File file = new File(tableInfoPath);
        if (!file.exists() || file.length() == 0) {
            FileUtil.createNewFile(tableInfoPath);
        }
        // 读取该数据库中数据
        FileInputStream fileInputStream = FileUtil.getFileInputStream(tableInfoPath);
        try {
            tableInfo = (TableInfo) KryoUtil.deserialize(fileInputStream);
        } catch (Exception e) {
//            e.printStackTrace();
            tableInfo = new TableInfo();
        }
    }
}
