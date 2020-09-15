package com.bit.model;

import com.bit.constance.DataType;
import lombok.Data;

/**
 * @author aerfafish
 * @date 2020/9/15 6:51 下午
 */
@Data
public class ColumnInfo {
    DataType type = DataType.STRING;

    Boolean hasIndex = false;
}
