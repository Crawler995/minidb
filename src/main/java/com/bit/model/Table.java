package com.bit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/8 12:08 下午
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Table {
    /**
     * 表名
     */
    String tableName;

    /**
     * 表各个列名和对应的类型
     */
    Map<String, ColumnInfo> columnInfo;

    /**
     * 表内容的储存位置
     */
    String filePath;
}
