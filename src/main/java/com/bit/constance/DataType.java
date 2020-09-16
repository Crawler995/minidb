package com.bit.constance;

/**
 * @author aerfafish
 * @date 2020/9/7 8:03 下午
 */
public enum DataType {
    INT,
    LONG,
    STRING,
    FLOAT,
    DOUBLE,
    ERROR;

    public static DataType valueOf(int index) {
        switch (index) {
            case 0:
                return INT;
            case 1:
                return LONG;
            case 2:
                return STRING;
            case 3:
                return FLOAT;
            case 4:
                return DOUBLE;
            default:
                return ERROR;
        }
    }
}
