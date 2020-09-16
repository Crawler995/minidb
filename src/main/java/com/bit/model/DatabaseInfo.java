package com.bit.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/15 7:21 下午
 */
@Data
public class DatabaseInfo {
    /**
     * 当前存在的所有数据库名,和对应的储存路径
     */
    List<Database> databases = new ArrayList<>();
}
