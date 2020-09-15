package com.bit.model;

import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author aerfafish
 * @date 2020/9/15 8:22 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Database {
    String databaseName;

    String filePath;
}
