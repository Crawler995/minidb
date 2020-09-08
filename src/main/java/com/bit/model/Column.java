package com.bit.model;

import lombok.Data;

/**
 * @author aerfafish
 * @date 2020/9/8 2:57 下午
 */
@Data
public class Column {
    /**
     * 列名
     */
    private String name;

    /**
     * 列名对应的值
     */
    private Object value;
}
