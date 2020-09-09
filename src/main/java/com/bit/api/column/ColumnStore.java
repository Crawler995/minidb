package com.bit.api.column;

import com.bit.constance.DBConfig;
import com.bit.exception.NoIdColumnException;
import com.bit.model.Column;
import com.bit.model.Table;
import com.bit.utils.FormatUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/8 2:56 下午
 */
public class ColumnStore {

    public ColumnStore(Table table) {
        this.table = table;
        columnPosition = DBConfig.COLUMN_POSITION + "/" + table.getName();
    }

    private Map<Long, ColumnMessage.Column> columnCache = null;

    private String columnPosition = null;

    private Table table = null;

    File[] files = null;

    /**
     * 1、查找最后一个id
     * 2、查看插入的id是否存在，存在则报错，不存在则插入指定位置
     * 3、
     *
     * @param newColumn
     */
    public void insertColumn(Column newColumn) {
        Long id = newColumn.getId();
        Map<String, Object> columnMap = newColumn.getColumn();
        /**
         * 如果不存在则默认插到最后一个
         */
        if (id == null) {
            id = getLastId() + 1;
        }
        File file = findFileById(id);
        if (file == null) {
            file = new File(columnPosition + "/" + table.getName() + 1);
        }
        ColumnMessage.Column.Builder builder = ColumnMessage.Column.newBuilder().setId(id);
        for (Map.Entry<String, Object> entry : columnMap.entrySet()) {

        }



    }

    public Long getLastId() {
        Long id = -1L;
        File file = new File(columnPosition);
        if (!file.exists()) {
            if (!file.mkdir()) {
                System.out.println("创建失败");
            }
        }
        File[] files = file.listFiles();
        if (files == null) {
            return -1L;
        }
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(files[files.length - 1]);
            id = getLastIdByInputStream(fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return id;
    }

    private Long getLastIdByInputStream(InputStream inputStream) {
        Long id = -1L;
        try {
            while (true) {
                ColumnMessage.Column column = ColumnMessage.Column.parseDelimitedFrom(inputStream);
                if (column == null) {
                    break;
                }
                id = column.getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    private File findFileById(Long id) {
        /**
         * 如果有索引则根据索引查找，否则遍历查找
         */
        if (isExistIndex("id")) {
            /**
             * 通过索引找到文件
             */
        } else {
            /**
             * id可以根据二分查找文件
             */
            File file = new File(columnPosition);
            if (!file.exists()) {
                if (!file.mkdir()) {
                    System.out.println("创建失败");
                    return null;
                }
            }
            File[] files = file.listFiles();
            if (files == null) {
                System.out.println("不存在任何文件");
                return null;
            }
            /**
             * 二分
             */
            int i = 1;
            int j = files.length;
            int mid = (i + j) / 2;
            while (i != j) {
                // 如果中间文件大于id 往小的找
                if (getFirstIdByFileName(columnPosition + "/" + table.getName() + mid) > id) {
                    j = mid;
                    continue;
                }
                if (getFirstIdByFileName(columnPosition + "/" + table.getName() + mid) <= id) {
                    i = mid;
                }
            }
            return new File(columnPosition + "/" + table.getName() + mid);
        }
        return null;
    }

    private Long getFirstIdByFileName(String name) {
        Long id = 0L;
        File file = new File(name);
        if (!file.exists()) {
            System.out.println("文件名字不存在");
            return 0L;
        }
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            ColumnMessage.Column column = ColumnMessage.Column.parseDelimitedFrom(fileInputStream);
            if (column == null) {
                System.out.println("文件不存在任意一个Column");
                return 0L;
            }
            id = column.getId();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return id;
    }

    private Boolean isExistIndex(String name) {
        /**
         * 去索引文件查找
         */

        return true;
    }
}
