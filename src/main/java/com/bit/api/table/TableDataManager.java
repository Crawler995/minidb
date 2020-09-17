package com.bit.api.table;

import com.bit.model.*;
import com.bit.utils.FileUtil;
import com.bit.utils.KryoUtil;
import com.esotericsoftware.kryo.io.Input;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import javax.sound.sampled.DataLine;
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

    public void insert(TableData tableData) {
        byte[] dataBytes = KryoUtil.serialize(tableData);
        long length = new File(dataFilePath).length();
        // dataBytes小于1M
        if (length%(1024*1024)+dataBytes.length < 1024*1024) {
            FileOutputStream fileOutputStream = FileUtil.getFileOutputStream(dataFilePath, false);
            try {
                fileOutputStream.write(dataBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileUtil.closeOutputSteam(fileOutputStream);
        }
        else {
            Long pageNum = length/(1024*1024)+1;
            FileUtil.writeFileByte(dataFilePath, pageNum, dataBytes, 1024*1024);
        }
        for (ColumnInfo columnInfo : table.getColumnInfo()) {
            if (columnInfo.getHasIndex()) {
                //建立索引
            }
        }
    }

    public void delete(TableData deleteTableData) {
        Long pageNum = 0L;
        String indexName = getIndex(deleteTableData);
        if (indexName != null) {
            //通过索引获取页号
        } else {
            Long length = new File(dataFilePath).length();
            for (int i = 0; i <= length/(1024*1024); i++) {
                byte[] bytes = FileUtil.getFileByte(dataFilePath, (long) i, 1024 * 1024);
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
                } catch (Exception ignored){
                }
                Boolean flag = false;
                Iterator<TableData> iterator = tableDataList.iterator();
                while (iterator.hasNext()){
                    TableData tableData = iterator.next();
                    if (compare(tableData, deleteTableData)) {
                        iterator.remove();
                        flag = true;
                    }
                }

                if (flag) {
                    byte[] newBytes = KryoUtil.serialize(tableDataList);
                    byte[] writeByte = Arrays.copyOf(newBytes, 1024*1024);
                    FileUtil.writeFileByte(dataFilePath, (long) i, writeByte, 1024*1024);
                }
            }
        }
    }

    public List<TableData> select(TableData selectTableData) {
        Long pageNum = 0L;
        String indexName = getIndex(selectTableData);
        List<TableData> selectTableDataList = new LinkedList<>();
        if (indexName != null) {
            //通过索引获取页号
        } else {
            Long length = new File(dataFilePath).length();
            for (int i = 0; i <= length/(1024*1024); i++) {
                byte[] bytes = FileUtil.getFileByte(dataFilePath, (long) i, 1024 * 1024);
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
                } catch (Exception ignored){
                }
                for (TableData tableData : tableDataList) {
                    if (compare(tableData, selectTableData)) {
                        selectTableDataList.add(tableData);
                    }
                }
            }
        }
        return selectTableDataList;
    }

    public void update(TableData originTableData, TableData updateTableData) {
        Long pageNum = 0L;
        String indexName = getIndex(updateTableData);
        if (indexName != null) {
            //通过索引获取页号
        } else {
            Long length = new File(dataFilePath).length();
            for (int i = 0; i <= length/(1024*1024); i++) {
                byte[] bytes = FileUtil.getFileByte(dataFilePath, (long) i, 1024 * 1024);
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
                } catch (Exception ignored){
                }
                for (TableData tableData : tableDataList) {
                    if (compare(tableData, originTableData)) {
                        updateTableData(tableData, updateTableData);
                    }
                }
                byte[] newBytes = KryoUtil.serialize(tableDataList);
                byte[] writeByte = Arrays.copyOf(newBytes, 1024*1024);
                FileUtil.writeFileByte(dataFilePath, (long) i, writeByte, 1024*1024);
            }
        }
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

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }
}
