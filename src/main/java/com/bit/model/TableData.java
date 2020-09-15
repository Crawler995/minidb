package com.bit.model;

import lombok.Data;

import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/8 2:57 下午
 */
@Data
public class TableData {

    /**
     * id
     */
    private Long id;

    /**
     * 列名和对应value的map集合
     */
    private Map<String, Object> data;
}
