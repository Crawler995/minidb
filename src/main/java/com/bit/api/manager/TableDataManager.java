package com.bit.api.manager;

import com.bit.api.model.Criteria;
import com.bit.api.model.IndexQuery;
import com.bit.api.model.Query;
import com.bit.api.model.Update;
import com.bit.constance.DBConfig;
import com.bit.constance.DataType;
import com.bit.exception.IndexExistException;
import com.bit.model.ColumnInfo;
import com.bit.model.Table;
import com.bit.model.TableData;
import com.bit.utils.FileUtil;
import com.bit.utils.KryoUtil;
import com.bit.utils.QueryUtil;
import com.esotericsoftware.kryo.io.Input;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public List<String> getTableColumns() {
        List<String> columnName = new ArrayList<>();
        for (ColumnInfo columnInfo : table.getColumnInfo()) {
            columnName.add(columnInfo.getColumnName());
        }
        return columnName;
    }

    public List<ColumnInfo> getColumnInfo() {
        return table.getColumnInfo();
    }

    public void insert(TableData tableData) throws Exception {
        transferTableData(tableData);
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

    public void delete(Query query) throws Exception {
        Set<Long> pageNumList = new HashSet<>();
        long length = new File(dataFilePath).length();
        String indexName = getIndex(query);
        IndexManager indexManager = null;
        transferQuery(query);
        IndexQuery indexQuery = QueryUtil.getKey(query.getCriteria().get(indexName));
        if (indexQuery.getLowKey() != null) {
            for (ColumnInfo columnInfo : table.getColumnInfo()) {
                if (columnInfo.getColumnName().equals(indexName)) {
                    //建立索引
                    indexManager = indexCache.get(indexName);
                    if (indexManager == null) {
                        String indexFilePath = columnInfo.getIndexFilePath();
                        indexManager = new IndexManager(indexFilePath);
                    }
                    indexCache.put(columnInfo.getColumnName(), indexManager);
                    pageNumList = indexManager.select(indexQuery);
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
                if (compare(tableData, query)) {
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

    public List<TableData> select(Query query) throws Exception {
        return select(query, true);
    }

    public List<TableData> select(Query query, Boolean useIndex) throws Exception {
        List<TableData> selectTableDataList = new LinkedList<>();
        Set<Long> pageNumList = new HashSet<>();
        long length = new File(dataFilePath).length();
        String indexName = getIndex(query);
        IndexManager indexManager = null;
        transferQuery(query);
        IndexQuery indexQuery = QueryUtil.getKey(query.getCriteria().get(indexName));
        if (indexQuery.getLowKey() != null && useIndex) {
            for (ColumnInfo columnInfo : table.getColumnInfo()) {
                String columnName = columnInfo.getColumnName();
                if (columnName.equals(indexName)) {
                    indexManager = indexCache.get(indexName);
                    if (indexManager == null) {
                        String indexFilePath = columnInfo.getIndexFilePath();
                        indexManager = new IndexManager(indexFilePath);
                    }
                    indexCache.put(columnName, indexManager);
                    pageNumList = indexManager.select(indexQuery);
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
                if (compare(tableData, query)) {
                    selectTableDataList.add(tableData);
                }
            }
        }
        return selectTableDataList;
    }

    public void update(Query query, Update update) throws Exception {
        Set<Long> pageNumList = new HashSet<>();
        long length = new File(dataFilePath).length();
        String indexName = getIndex(update);
        IndexManager indexManager = null;
        transferUpdate(update);
        transferQuery(query);
        IndexQuery indexQuery = QueryUtil.getKey(query.getCriteria().get(indexName));
        if (indexQuery.getLowKey() != null) {
            for (ColumnInfo columnInfo : table.getColumnInfo()) {
                if (columnInfo.getColumnName().equals(indexName)) {
                    //建立索引
                    indexManager = indexCache.get(indexName);
                    if (indexManager == null) {
                        String indexFilePath = columnInfo.getIndexFilePath();
                        indexManager = new IndexManager(indexFilePath);
                    }
                    Comparable comparable = update.getModifyData().get(columnInfo.getColumnName());
                    indexCache.put(columnInfo.getColumnName(), indexManager);
                    pageNumList = indexManager.select(indexQuery);
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
                if (compare(tableData, query)) {
                    updateTableData(tableData, update);
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
                    indexManager.updateToFile();
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

    public void clearIndexCache() {
        for (Map.Entry<String, IndexManager> entry : indexCache.entrySet()) {
            entry.getValue().clearCache();
        }
    }

    private String getIndex(TableData tableData) throws Exception {
        for (Map.Entry<String, Comparable> entry : tableData.getData().entrySet()) {
            if (entry.getKey() != null && Objects.requireNonNull(getColumnInfo(entry.getKey())).getHasIndex()) {
                return entry.getKey();
            }
        }
        return null;
    }

    private String getIndex(Query query) throws Exception {
        for (Map.Entry<String, Criteria> entry : query.getCriteria().entrySet()) {
            if (getColumnInfo(entry.getKey()).getHasIndex()) {
                // 如果存在索引
                return entry.getKey();
            }
        }
        return null;
    }

    private String getIndex(Update update) throws Exception {
        for (Map.Entry<String, Comparable> entry : update.getModifyData().entrySet()) {
            if (getColumnInfo(entry.getKey()).getHasIndex()) {
                // 如果存在索引
                return entry.getKey();
            }
        }
        return null;
    }

    private Boolean compare(TableData tableData1, TableData tableData2) {
        if (tableData2 == null) {
            return true;
        }
        for (Map.Entry<String, Comparable> entry : tableData2.getData().entrySet()) {
            if (!tableData1.getData().get(entry.getKey()).equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    private Boolean compare(TableData tableData, Query query) {
        if (query.getCriteria().size() == 0) {
            return true;
        }
        for (Map.Entry<String, Criteria> entry : query.getCriteria().entrySet()) {
            if (!compare((Comparable) tableData.getData().get(entry.getKey()), entry.getValue())) {
                return false;
            }
        }
        return true;
    }

//    private Boolean compare(TableData tableData, Criteria criteria) {
//        if (criteria.getKey().equals("$or")) {
//
//        }
//        if (criteria.getKey().equals("$nor")) {
//
//        }
//        if (criteria.getKey().equals("$and")) {
//            List<Criteria> criteriaList = (List<Criteria>) criteria.getIsValue();
//
//            compare(tableData, )
//        }
//        if (criteria.getIsValue() != Criteria.NOT_SET) {
//            return comparable.compareTo(Objects.requireNonNull(criteria.getIsValue())) == 0;
//        }
//
//        if (criteria.getCriteria().size() == 0) {
//            return true;
//        }
//        for (Map.Entry<String, Comparable> entry : criteria.getCriteria().entrySet()) {
//            if (entry.getKey().equals("$gt")) {
//                // 如果比他小于等于
//                if (comparable.compareTo(entry.getValue()) <= 0) {
//                    return false;
//                }
//            }
//            if (entry.getKey().equals("$gte")) {
//                if (comparable.compareTo(entry.getValue()) < 0) {
//                    return false;
//                }
//            }
//            if (entry.getKey().equals("$lt")) {
//                if (comparable.compareTo(entry.getValue()) >= 0) {
//                    return false;
//                }
//            }
//            if (entry.getKey().equals("$lte")) {
//                if (comparable.compareTo(entry.getValue()) > 0) {
//                    return false;
//                }
//            }
//            if (entry.getKey().equals("$ne")) {
//                if (comparable.compareTo(entry.getValue()) == 0) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }
    private Boolean compare(Comparable comparable, Criteria criteria) {
        Object value = criteria.getIsValue();
        if (value != Criteria.NOT_SET) {
            if (value instanceof Pattern && comparable instanceof String) {
                Matcher matcher = ((Pattern) value).matcher((String) comparable);
                if (matcher.find()) {
                    return true;
                }
                return false;
            }
            return comparable.compareTo(Objects.requireNonNull(value)) == 0;
        }

        if (criteria.getCriteria().size() == 0) {
            return true;
        }
        for (Map.Entry<String, Comparable> entry : criteria.getCriteria().entrySet()) {
            if (entry.getKey().equals("$gt")) {
                // 如果比他小于等于
                if (comparable.compareTo(entry.getValue()) <= 0) {
                    return false;
                }
            }
            if (entry.getKey().equals("$gte")) {
                if (comparable.compareTo(entry.getValue()) < 0) {
                    return false;
                }
            }
            if (entry.getKey().equals("$lt")) {
                if (comparable.compareTo(entry.getValue()) >= 0) {
                    return false;
                }
            }
            if (entry.getKey().equals("$lte")) {
                if (comparable.compareTo(entry.getValue()) > 0) {
                    return false;
                }
            }
            if (entry.getKey().equals("$ne")) {
                if (comparable.compareTo(entry.getValue()) == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private Boolean compare(TableData tableData, Update update) {
        if (update.getModifyData().size() == 0) {
            return true;
        }
        for (Map.Entry<String, Comparable> entry : update.getModifyData().entrySet()) {
            if (entry.getValue().compareTo(tableData.getData().get(entry.getKey())) != 0) {
                return false;
            }
        }
        return true;
    }

    private void updateTableData(TableData target, TableData updateData) {
        for (Map.Entry<String, Comparable> entry : updateData.getData().entrySet()) {
            if (entry.getValue() != null) {
                target.getData().put(entry.getKey(), entry.getValue());
            }
        }
    }

    private void updateTableData(TableData target, Update update) {
        for (Map.Entry<String, Comparable> entry : update.getModifyData().entrySet()) {
            if (entry.getValue() != null) {
                target.getData().put(entry.getKey(), entry.getValue());
            }
        }
    }

    private ColumnInfo getColumnInfo(String columnName) throws Exception {
        for (ColumnInfo columnInfo : table.getColumnInfo()) {
            if (columnInfo.getColumnName().equals(columnName)) {
                return columnInfo;
            }
        }
        throw new Exception("不存在该列 "+columnName);
    }

    public void transferTableData(TableData tableData) throws Exception {
        for (Map.Entry<String, Comparable> entry : tableData.getData().entrySet()) {
            DataType type = getColumnInfo(entry.getKey()).getType();
            entry.setValue(transferString((String) entry.getValue(), type));
        }
    }

    private Comparable transferString(String str, DataType type) throws Exception {
        if (type == DataType.DOUBLE) {
            return Double.parseDouble(str);
        }
        if (type == DataType.LONG) {
            return Long.parseLong(str);
        }
        if (type == DataType.INT) {
            return Integer.parseInt(str);
        }
        if (type == DataType.FLOAT) {
            return Float.parseFloat(str);
        }
        if (type == DataType.STRING) {
            if (str.indexOf("\"") == 0 && str.lastIndexOf("\"") == str.length()-1) {
                return str.substring(1, str.length()-1);
            } else {
                return str;
            }
        }
        if (type == DataType.DATETIME) {
            try {
                if (str.indexOf("\"") == 0 && str.lastIndexOf("\"") == str.length()-1) {
                    str = str.substring(1, str.length()-1);
                }
                str = str.trim();
                SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                if (str.length() < 12) {
                    str = str + " 00:00:00";
                }
                parser.parse(str);
                return str;
            } catch (Exception e) {
                throw new Exception("日期格式不正确 请更改为yyyy-mm-dd hh:mm:ss 或 yyyy-mm-dd");
            }

        }
        throw new Exception("不存在该类型");
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
        if (type == DataType.DATETIME) {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                return parser.parse((String) object);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void transferQuery(Query query) throws Exception {
        for (Map.Entry<String, Criteria> entry : query.getCriteria().entrySet()) {
            DataType type = getColumnInfo(entry.getKey()).getType();
            transferQuery(entry.getValue(), type);
        }
    }

    private void transferQuery(Criteria criteria, DataType type) throws Exception {
        if (criteria.getIsValue() != Criteria.NOT_SET && !(criteria.getIsValue() instanceof Pattern)) {
            criteria.setIsValue(transferString((String) criteria.getIsValue(), type));
            return;
        }
        for (Map.Entry<String, Comparable> entry : criteria.getCriteria().entrySet()) {
            if (type == DataType.DOUBLE) {
                String value = (String) entry.getValue();
                entry.setValue(Double.parseDouble(value));
            }
            if (type == DataType.LONG) {
                String value = (String) entry.getValue();
                entry.setValue(Long.parseLong(value));
            }
            if (type == DataType.INT) {
                String value = (String) entry.getValue();
                entry.setValue(Integer.parseInt(value));
            }
            if (type == DataType.FLOAT) {
                String value = (String) entry.getValue();
                entry.setValue(Float.parseFloat(value));
            }
            if (type == DataType.STRING) {
                String value = (String) entry.getValue();
                if (value.indexOf("\"") == 0 && value.lastIndexOf("\"") == value.length()-1) {
                    entry.setValue(value.substring(1, value.length()-1));
                } else {
                    entry.setValue(value);
                }

            }
            if (type == DataType.DATETIME) {
                try {
                    String str = (String) entry.getValue();
                    if (str.indexOf("\"") == 0 && str.lastIndexOf("\"") == str.length()-1) {
                        str = str.substring(1, str.length()-1);
                    }
                    str = str.trim();
                    SimpleDateFormat parser = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                    if (str.length() < 12) {
                        str = str + " 00:00:00";
                    }
                    Date date = parser.parse(str);
                    entry.setValue(str);
                } catch (Exception e) {
                    throw new Exception("日期格式不正确 请更改为yyyy-mm-dd hh:mm:ss 或 yyyy-mm-dd");
                }
            }
        }
    }
    private void transferUpdate(Update update) throws Exception {
        for (Map.Entry<String, Comparable> entry : update.getModifyData().entrySet()) {
            DataType type = getColumnInfo(entry.getKey()).getType();
            if (type == DataType.DOUBLE) {
                String value = (String) entry.getValue();
                entry.setValue(Double.parseDouble(value));
            }
            if (type == DataType.LONG) {
                String value = (String) entry.getValue();
                entry.setValue(Long.parseLong(value));
            }
            if (type == DataType.INT) {
                String value = (String) entry.getValue();
                entry.setValue(Integer.parseInt(value));
            }
            if (type == DataType.FLOAT) {
                String value = (String) entry.getValue();
                entry.setValue(Float.parseFloat(value));
            }
            if (type == DataType.STRING) {
                String value = (String) entry.getValue();
                if (value.indexOf("\"") == 0 && value.lastIndexOf("\"") == value.length()-1) {
                    entry.setValue(value.substring(1, value.length()-1));
                } else {
                    entry.setValue(value);
                }
            }
            if (type == DataType.DATETIME) {
                try {
                    String str = (String) entry.getValue();
                    if (str.indexOf("\"") == 0 && str.lastIndexOf("\"") == str.length()-1) {
                        str = str.substring(1, str.length()-1);
                    }
                    str = str.trim();
                    SimpleDateFormat parser = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                    if (str.length() < 12) {
                        str = str + " 00:00:00";
                    }
                    Date date = parser.parse(str);
                    entry.setValue(str);
                } catch (Exception e) {
                    throw new Exception("日期格式不正确 请更改为yyyy-mm-dd hh:mm:ss 或 yyyy-mm-dd");
                }
            }
        }
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
