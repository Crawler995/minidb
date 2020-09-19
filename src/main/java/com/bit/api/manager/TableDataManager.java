package com.bit.api.manager;

import com.bit.constance.DBConfig;
import com.bit.constance.DataType;
import com.bit.exception.IndexExistException;
import com.bit.model.*;
import com.bit.utils.FileUtil;
import com.bit.utils.KryoUtil;
import com.esotericsoftware.kryo.io.Input;

import java.io.*;
import java.util.*;

/**
 * @author aerfafish
 * @date 2020/9/15 9:17 下午
 */
public class TableDataManager {

    private String dataFilePath = "";

    private Table table;

    public TableDataManager(Table table) {
        this.dataFilePath = table.getFilePath();
        this.table = table;

    }

    private Map<Long, TableData> tableDataCache = new HashMap<>();

    private Map<String, IndexManager> indexCache = new HashMap<>();

    public void insert(TableData tableData) {
        byte[] dataBytes = KryoUtil.serialize(tableData);
        File file = new File(dataFilePath);
        long length = file.length();
        Long pageNum = length / (1024 * 1024);
        // dataBytes小于1M
        if (length % (1024 * 1024) + dataBytes.length < 1024 * 1024) {
            FileOutputStream fileOutputStream = FileUtil.getFileOutputStream(dataFilePath, false);
            try {
                fileOutputStream.write(dataBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileUtil.closeOutputSteam(fileOutputStream);
        } else {
            pageNum = pageNum + 1;
            FileUtil.writeFileByte(dataFilePath, pageNum, dataBytes, 1024 * 1024);
        }
        for (ColumnInfo columnInfo : table.getColumnInfo()) {
            if (columnInfo.getHasIndex()) {
                //建立索引
                IndexManager indexManager = indexCache.get(columnInfo.getColumnName());
                if (indexManager == null) {
                    String indexFilePath = columnInfo.getIndexFilePath();
                    indexManager = new IndexManager(indexFilePath);
                }
                indexCache.put(columnInfo.getColumnName(), indexManager);
                Object o = tableData.getData().get(columnInfo.getColumnName());
                indexManager.insert(transferObject(o, columnInfo.getType()), pageNum);
            }
        }
    }

    public void delete(TableData deleteTableData) {
        List<Long> pageNumList = new ArrayList<>();
        long length = new File(dataFilePath).length();
        String indexName = getIndex(deleteTableData);
        IndexManager indexManager = null;
        if (indexName != null) {
            for (ColumnInfo columnInfo : table.getColumnInfo()) {
                if (columnInfo.getColumnName().equals(indexName)) {
                    //建立索引
                    indexManager = indexCache.get(indexName);
                    if (indexManager == null) {
                        String indexFilePath = columnInfo.getIndexFilePath();
                        indexManager = new IndexManager(indexFilePath);
                    }
                    indexCache.put(columnInfo.getColumnName(), indexManager);
                    Object o = deleteTableData.getData().get(columnInfo.getColumnName());
                    pageNumList = indexManager.select(transferObject(o, columnInfo.getType()));
                }
            }
        } else {
            for (int i = 0; i <= length / (1024 * 1024); i++) {
                pageNumList.add((long) i);
            }
        }
        for (Long i : pageNumList) {
            byte[] bytes = FileUtil.getFileByte(dataFilePath, i, 1024 * 1024);
            Input input = new Input(bytes);
            List<TableData> tableDataList = new LinkedList<>();
            try {
                while (true) {
                    TableData tableData = (TableData) KryoUtil.deserialize(input);
                    if (tableData == null) {
                        break;
                    }
                    tableDataList.add(tableData);
                }
            } catch (Exception ignored) {
            }
            Boolean flag = false;
            Iterator<TableData> iterator = tableDataList.iterator();
            while (iterator.hasNext()) {
                TableData tableData = iterator.next();
                if (compare(tableData, deleteTableData)) {
                    iterator.remove();
                    if (indexName != null) {
                        deleteIndex(tableData, i);
                    }
                    flag = true;
                }
            }

            if (flag) {
                byte[] newBytes = KryoUtil.serialize(tableDataList);
                byte[] writeByte = Arrays.copyOf(newBytes, 1024 * 1024);
                FileUtil.writeFileByte(dataFilePath, (long) i, writeByte, 1024 * 1024);
            }
        }

    }

    public List<TableData> select(TableData selectTableData) {
        List<TableData> selectTableDataList = new LinkedList<>();
        List<Long> pageNumList = new ArrayList<>();
        long length = new File(dataFilePath).length();
        String indexName = getIndex(selectTableData);
        IndexManager indexManager = null;
        if (indexName != null) {
            for (ColumnInfo columnInfo : table.getColumnInfo()) {
                if (columnInfo.getColumnName().equals(indexName)) {
                    //建立索引
                    indexManager = indexCache.get(indexName);
                    if (indexManager == null) {
                        String indexFilePath = columnInfo.getIndexFilePath();
                        indexManager = new IndexManager(indexFilePath);
                    }
                    indexCache.put(columnInfo.getColumnName(), indexManager);
                    Object o = selectTableData.getData().get(columnInfo.getColumnName());
                    pageNumList = indexManager.select(transferObject(o, columnInfo.getType()));
                }

            }
        } else {
            for (int i = 0; i <= length / (1024 * 1024); i++) {
                pageNumList.add((long) i);
            }
        }
        for (Long i : pageNumList) {
            byte[] bytes = FileUtil.getFileByte(dataFilePath, i, 1024 * 1024);
            Input input = new Input(bytes);
            List<TableData> tableDataList = new LinkedList<>();
            try {
                while (true) {
                    TableData tableData = (TableData) KryoUtil.deserialize(input);
                    if (tableData == null) {
                        break;
                    }
                    tableDataList.add(tableData);
                }
            } catch (Exception ignored) {
            }
            for (TableData tableData : tableDataList) {
                if (compare(tableData, selectTableData)) {
                    selectTableDataList.add(tableData);
                }
            }
        }
        return selectTableDataList;
    }

    public void update(TableData originTableData, TableData updateTableData) {
        List<TableData> selectTableDataList = new LinkedList<>();
        List<Long> pageNumList = new ArrayList<>();
        long length = new File(dataFilePath).length();
        String indexName = getIndex(originTableData);
        IndexManager indexManager = null;
        if (indexName != null) {


            for (ColumnInfo columnInfo : table.getColumnInfo()) {
                if (columnInfo.getColumnName().equals(indexName)) {
                    //建立索引
                    indexManager = indexCache.get(indexName);
                    if (indexManager == null) {
                        String indexFilePath = columnInfo.getIndexFilePath();
                        indexManager = new IndexManager(indexFilePath);
                    }
                    indexCache.put(columnInfo.getColumnName(), indexManager);
                    Object o = originTableData.getData().get(columnInfo.getColumnName());
                    pageNumList = indexManager.select(transferObject(o, columnInfo.getType()));
                }

            }
        } else {
            for (int i = 0; i <= length / (1024 * 1024); i++) {
                pageNumList.add((long) i);
            }
        }
        for (Long i : pageNumList) {
            byte[] bytes = FileUtil.getFileByte(dataFilePath, i, 1024 * 1024);
            Input input = new Input(bytes);
            List<TableData> tableDataList = new LinkedList<>();
            try {
                while (true) {
                    TableData tableData = (TableData) KryoUtil.deserialize(input);
                    if (tableData == null) {
                        break;
                    }
                    tableDataList.add(tableData);
                }
            } catch (Exception ignored) {
            }
            for (TableData tableData : tableDataList) {
                if (compare(tableData, originTableData)) {
                    updateTableData(tableData, updateTableData);
                }
            }
            byte[] newBytes = KryoUtil.serialize(tableDataList);
            byte[] writeByte = Arrays.copyOf(newBytes, 1024 * 1024);
            FileUtil.writeFileByte(dataFilePath, (long) i, writeByte, 1024 * 1024);
        }

    }

    public void createIndex(String columnName, String filePath) throws Exception {
        for (ColumnInfo columnInfo : table.getColumnInfo()) {
            if (columnInfo.getColumnName().equals(columnName)) {
                if (columnInfo.getHasIndex()) {
                    throw new IndexExistException("已经存在该索引，请先删除再创建");
                }
                columnInfo.setHasIndex(true);
                if (filePath == null) {
                    filePath = DBConfig.Index_POSITION + "/" + table.getTableName() + "/" + columnInfo.getColumnName();
                }
                if (new File(filePath).exists()) {
                    throw new Exception("文件已经存在，请删除对应文件后创建");
                }
                columnInfo.setIndexFilePath(filePath);
                IndexManager indexManager = new IndexManager(filePath);
                indexCache.put(columnName, indexManager);
                long length = new File(dataFilePath).length();
                for (Long i = 0L; i <= length/(1024*1024); i++) {
                    byte[] bytes = FileUtil.getFileByte(dataFilePath, i, 1024 * 1024);
                    Input input = new Input(bytes);
                    List<TableData> tableDataList = new LinkedList<>();
                    try {
                        while (true) {
                            TableData tableData = (TableData) KryoUtil.deserialize(input);
                            if (tableData == null) {
                                break;
                            }
                            tableDataList.add(tableData);
                        }
                    } catch (Exception ignored) {
                    }
                    for (TableData tableData : tableDataList) {
                        if (tableData == null) {
                            break;
                        }
                        Comparable key = transferObject(tableData.getData().get(columnName), columnInfo.getType());
                        indexManager.insert(key, i);
                    }
                }
                return;
            }
        }
        throw new Exception("不存在该列");
    }

    public void deleteIndex(String columnName) throws Exception {
        for (ColumnInfo columnInfo : table.getColumnInfo()) {
            if (columnInfo.getColumnName().equals(columnName)) {
                if (!columnInfo.getHasIndex()) {
                    throw new Exception("不存在该索引，无需删除");
                }
                columnInfo.setHasIndex(false);
                String filePath = columnInfo.getIndexFilePath();
                new File(filePath).delete();
                columnInfo.setIndexFilePath(null);
                indexCache.remove(columnName);
                return;
            }
        }
        throw new Exception("不存在该列");
    }

    private String getIndex(TableData tableData) {
        for (Map.Entry<String, Object> entry : tableData.getData().entrySet()) {
            if (entry.getKey() != null && Objects.requireNonNull(getColumnInfo(entry.getKey())).getHasIndex()) {
                return entry.getKey();
            }
        }
        return null;
    }

    private Boolean compare(TableData tableData1, TableData tableData2) {
        if (tableData2 == null) {
            return true;
        }
        for (Map.Entry<String, Object> entry : tableData2.getData().entrySet()) {
            if (!tableData1.getData().get(entry.getKey()).equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    private void updateTableData(TableData target, TableData updateData) {
        for (Map.Entry<String, Object> entry : updateData.getData().entrySet()) {
            if (entry.getValue() != null) {
                target.getData().put(entry.getKey(), entry.getValue());
            }
        }
    }

    private ColumnInfo getColumnInfo(String columnName) {
        for (ColumnInfo columnInfo : table.getColumnInfo()) {
            if (columnInfo.getColumnName().equals(columnName)) {
                return columnInfo;
            }
        }
        return null;
    }

    private Comparable transferObject(Object object, DataType type) {
        if (type == DataType.DOUBLE) {
            return (Double) object;
        }
        if (type == DataType.LONG) {
            return (Long) object;
        }
        if (type == DataType.INT) {
            return (Integer) object;
        }
        if (type == DataType.STRING) {
            return (String) object;
        }
        if (type == DataType.FLOAT) {
            return (Float) object;
        }
        return null;
    }

    private void deleteIndex(TableData tableData, Long pageNum) {
        for (ColumnInfo columnInfo : table.getColumnInfo()) {
            if (columnInfo.getHasIndex()) {
                String columnName = columnInfo.getColumnName();
                IndexManager indexManager = indexCache.get(columnName);
                if (indexManager == null) {
                    indexManager = new IndexManager(columnInfo.getIndexFilePath());
                    indexCache.put(columnInfo.getColumnName(), indexManager);
                }
                Object o = tableData.getData().get(columnName);
                indexManager.delete(transferObject(o, columnInfo.getType()), pageNum);
            }
        }
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }
}
