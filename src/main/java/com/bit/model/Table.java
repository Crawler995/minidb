package com.bit.model;

import lombok.Data;

import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/8 12:08 下午
 */

@Data
public class Table {
    /**
     * 表名
     */
    String name;

    /**
     * 表各个列名和对应的类型
     */
    Map<String, Integer> type;
}
