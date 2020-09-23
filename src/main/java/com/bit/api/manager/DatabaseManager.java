package com.bit.api.manager;

import com.bit.constance.DBConfig;
import com.bit.exception.NoNameDatabaseException;
import com.bit.exception.SameNameDatabaseException;
import com.bit.model.Database;
import com.bit.model.DatabaseInfo;
import com.bit.utils.FileUtil;
import com.bit.utils.KryoUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/15 6:53 下午
 */
@Component
public class DatabaseManager {

    public DatabaseManager() {}

    private static DatabaseInfo databaseInfo = null;

    private static Map<String, TableManager> databaseCache = new HashMap<>();

    @PostConstruct
    public void initConfig() {
        FileInputStream fileInputStream = FileUtil.getFileInputStream(DBConfig.DATABASE_CONFIG);
        // todo: buff size
        try {
            DatabaseManager.databaseInfo = (DatabaseInfo) KryoUtil.deserialize(fileInputStream);
        } catch (Exception e) {
//            e.printStackTrace();
            databaseInfo = new DatabaseInfo();
        }
        FileUtil.closeInputSteam(fileInputStream);
    }

    public void createDatabase(Database database) throws SameNameDatabaseException {
        // 如果当前存在该数据库
        if (containDatabase(database.getDatabaseName())) {
            throw new SameNameDatabaseException("Can't create database '"+database.getDatabaseName()+"'; database exists\n");
        }
        String filePath = database.getFilePath();
        if (filePath == null) {
            filePath = DBConfig.DATABASE_POSITION+"/"+database.getDatabaseName();
        }
        database.setFilePath(filePath);
        TableManager tableManager = new TableManager(database.getDatabaseName(), filePath);
        databaseCache.put(database.getDatabaseName(), tableManager);
        databaseInfo.getDatabases().add(database);
        storeToFile();
    }

    public void updateDatabase(Database originDatabase, Database newDatabase) throws NoNameDatabaseException {
        // 如果当前存在该数据库
        Database database = removeDatabase(originDatabase.getDatabaseName());
        if (database == null) {
            throw new NoNameDatabaseException("Unknown database "+ "'" +originDatabase.getDatabaseName()+ "'");
        }
        // 修改全局储存文件中的数据库名
        if (newDatabase.getFilePath() == null) {
            newDatabase.setFilePath(database.getFilePath());
        }
        databaseInfo.getDatabases().add(newDatabase);
        TableManager tableManager = databaseCache.remove(originDatabase.getDatabaseName());
        databaseCache.put(newDatabase.getDatabaseName(), tableManager);
        storeToFile();
    }

    public void deleteDatabase(String databaseName) throws NoNameDatabaseException {
        // 如果当前存在该数据库
        Database database = removeDatabase(databaseName);
        if (database == null) {
            throw new NoNameDatabaseException("Unknown database "+ "'" +databaseName+ "'");
        }
        // todo:删除数据库中的表和数据
        String filePath = database.getFilePath();
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        storeToFile();
    }

    public void storeToFile() {
        byte[] bytes = KryoUtil.serialize(databaseInfo);
        FileOutputStream fileOutputStream = FileUtil.getFileOutputStream(DBConfig.DATABASE_CONFIG);
        FileUtil.writeFileByte(fileOutputStream, bytes);
        FileUtil.closeOutputSteam(fileOutputStream);
    }

    public List<String> showDatabases() {
        List<String> databases = new ArrayList<>();
        for (Database database : databaseInfo.getDatabases()) {
            String databaseName = database.getDatabaseName();
            if (databaseName != null && !"".equals(databaseName)) {
                databases.add(databaseName);
            }
        }
        return databases;
    }

    public TableManager getTableManager(String databaseName) throws Exception {
        TableManager tableManager = databaseCache.get(databaseName);
        if (tableManager == null) {
            for (Database database : databaseInfo.getDatabases()) {
                if (database.getDatabaseName().equals(databaseName)) {
                    tableManager = new TableManager(databaseName, database.getFilePath());
                    databaseCache.put(databaseName, tableManager);
                }
            }
        }
        if (tableManager == null) {
            throw new Exception("Unknown database "+ "'" +databaseName+ "'");
        }
        return tableManager;
    }



    private Boolean containDatabase(String databaseName) {
        List<Database> databases = databaseInfo.getDatabases();
        for (Database database : databases) {
            if (database.getDatabaseName().equals(databaseName)) {
                return true;
            }
        }
        return false;
    }

    private Database getDatabase(String databaseName) {
        List<Database> databases = databaseInfo.getDatabases();
        for (Database database : databases) {
            if (database.getDatabaseName().equals(databaseName)) {
                return database;
            }
        }
        return null;
    }

    private Database removeDatabase(String databaseName) {
        List<Database> databases = databaseInfo.getDatabases();
        for (Database database : databases) {
            if (database.getDatabaseName().equals(databaseName)) {
                databases.remove(database);
                return database;
            }
        }
        return null;
    }
}
