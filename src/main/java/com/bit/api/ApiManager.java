package com.bit.api;

import com.bit.api.manager.DatabaseManager;
import com.bit.api.model.Query;
import com.bit.api.model.Update;
import com.bit.model.Database;
import com.bit.model.Table;
import com.bit.model.TableData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/19 5:28 下午
 */

@Component
public class ApiManager {

    @Autowired
    DatabaseManager databaseManager;

    String currentDatabase = null;


    /**
     * 指定当前使用的数据库 如果不存在会抛出异常
     * @param databaseName
     * @throws Exception
     */
    public void useDatabase(@NonNull String databaseName) throws Exception {
        databaseManager.getTableManager(databaseName);
        currentDatabase = databaseName;
    }

    /**
     * 获取当前数据库名，未使用use <database>则返回null
     * @return
     */
    public String getCurrentDatabase() throws Exception {
        if (currentDatabase == null) {
            throw new Exception("未指定当前数据库");
        }
        return currentDatabase;
    }

    /**
     * 获取当前所有数据库名 返回他的集合
     */
    public List<String> showDatabases() {
        return databaseManager.showDatabases();
    }

    /**
     * 创建一个数据库
     * @param database
     * <databaseName> 不能为空
     * <filePath> 可以为空（最好直接为空） 会添加默认路径
     * @throws Exception
     */
    public void createDatabase(@NonNull Database database) throws Exception {
        databaseManager.createDatabase(database);
    }

    /**
     * 删除指定数据库名的数据库
     * @param databaseName
     * @throws Exception
     */
    public void deleteDatabase(@NonNull String databaseName) throws Exception {
        databaseManager.deleteDatabase(databaseName);
    }

    /**
     * 更新数据库
     * @param database
     * @param newDatabase
     * <database> 中filePath直接指定为空， <newDatabase> 中必须指定databaseName
     * @throws Exception
     */
    public void updateDatabase(@NonNull Database database, @NonNull Database newDatabase) throws Exception {
        databaseManager.updateDatabase(database, newDatabase);
    }

    /**
     * 取当前数据库中所有表名，若未指定数据库抛出异常
     * @throws Exception
     */
    public List<String> showTables() throws Exception {
        if (currentDatabase == null) {
            throw new Exception("未指定当前数据库");
        }
        return databaseManager.getTableManager(currentDatabase).getTables();
    }

    /**
     * 在当前指定的数据库创建一个表
     * @param table
     * tableName 必须有值
     * Table中的List<columnInfo>中至少存在一组columnInfo
     * columnInfo中必须指定 columnName和type filePath可以不指定， hasIndex不指定默认为false
     * Table中的filePath可以不指定
     * @throws Exception
     */
    public void createTable(@NonNull Table table) throws Exception {
        if (currentDatabase == null) {
            throw new Exception("未指定当前数据库");
        }
        databaseManager.getTableManager(currentDatabase).createTable(table);
    }

    /**
     * 删除当前数据库的表
     * @param tableName
     * 如果数据库不存在该表会抛出异常
     * todo:会同时删除对应的索引
     * @throws Exception
     */
    public void deleteTable(@NonNull String tableName) throws Exception {
        if (currentDatabase == null) {
            throw new Exception("未指定当前数据库");
        }
        databaseManager.getTableManager(currentDatabase).deleteTable(tableName);
    }

    /**
     * 更新当前数据库中的表，但由于表更新未对应处理数据，所以暂时不推荐使用该接口
     * @param table
     * @param newTable
     * @throws Exception
     */
    public void updateTables(@NonNull Table table, @NonNull Table newTable) throws Exception {
        if (currentDatabase == null) {
            throw new Exception("未指定当前数据库");
        }
        databaseManager.getTableManager(currentDatabase).updateTable(table, newTable);
    }

    /**
     * 创建索引，如果没有对应的表或字段会抛出异常
     * 会同时对数据库中所有数据对应的列创建索引，在数据较大时可能时间较长
     * @param tableName
     * @param columnName
     * @throws Exception
     */
    public void createIndex(@NonNull String tableName,@NonNull String columnName) throws Exception {
        if (currentDatabase == null) {
            throw new Exception("未指定当前数据库");
        }
        databaseManager.getTableManager(currentDatabase).createIndex(tableName, columnName, null);
    }

    /**
     * 删除索引，如果不存在对应的表或字段会抛出异常
     * @param tableName
     * @param columnName
     * @throws Exception
     */
    public void deleteIndex(@NonNull String tableName,@NonNull String columnName) throws Exception {
        if (currentDatabase == null) {
            throw new Exception("未指定当前数据库");
        }
        databaseManager.getTableManager(currentDatabase).deleteIndex(tableName, columnName);
    }

    /**
     * 插入数据
     * @param update
     * @param tableName
     * update中至少set一个列，否则会抛出异常
     * @throws Exception
     */
    public void insertData(@NonNull Update update, @NonNull String tableName) throws Exception {
        if (currentDatabase == null) {
            throw new Exception("未指定当前数据库");
        }
        Map<String, Comparable> modifyData = update.getModifyData();
        if (modifyData == null || modifyData.size() == 0) {
            throw new Exception("未添加列");
        }
        TableData tableData = new TableData();
        tableData.setData(modifyData);
        databaseManager.getTableManager(currentDatabase).getTableDataManager(tableName).insert(tableData);
    }

    /**
     * 删除满足查询条件的所有数据
     * @param query
     * @param tableName
     * query中至少有一个Criteria 否则会全部删除
     * 使用query.addCriteria(Criteria.where().is()).addCriteria(Criteria.where().lt()).addCriteria(Criteria.where().lt())
     * ……
     * @throws Exception
     */
    public void deleteData(@NonNull Query query, @NonNull String tableName) throws Exception {
        if (currentDatabase == null) {
            throw new Exception("未指定当前数据库");
        }
        databaseManager.getTableManager(currentDatabase).getTableDataManager(tableName).delete(query);
    }

    /**
     * 查询数据
     * @param query
     * @param tableName
     * query参数与上述一样
     * @return
     * @throws Exception
     */
    public List<TableData> selectData(@NonNull Query query, @NonNull String tableName) throws Exception {
        if (currentDatabase == null) {
            throw new Exception("未指定当前数据库");
        }
        return databaseManager.getTableManager(currentDatabase).getTableDataManager(tableName).select(query);
    }

    /**
     * 更新数据
     * @param query
     * @param update
     * @param tableName
     * query和update参数和上述一样
     * @throws Exception
     */
    public void updateData(@NonNull Query query, @NonNull Update update, @NonNull String tableName) throws Exception {
        if (currentDatabase == null) {
            throw new Exception("未指定当前数据库");
        }
        databaseManager.getTableManager(currentDatabase).getTableDataManager(tableName).update(query, update);
    }


}
