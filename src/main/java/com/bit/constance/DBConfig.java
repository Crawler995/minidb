package com.bit.constance;

import com.bit.utils.PathUtil;

/**
 * @author aerfafish
 * @date 2020/9/7 8:16 下午
 */
public class DBConfig {
    public static boolean isWin = System.getProperty("os.name").toLowerCase().startsWith("win");

    private static final String DB_POSITION_STR = "/tmp/minidb";

    public static String DB_POSITION = PathUtil.getSystemAdaptedPath(DB_POSITION_STR);

    public static String DATABASE_CONFIG = PathUtil.getSystemAdaptedPath(DB_POSITION_STR+"/db.conf");

    public static String DATABASE_POSITION = PathUtil.getSystemAdaptedPath(DB_POSITION_STR+"/database");

    public static String TABLE_POSITION = PathUtil.getSystemAdaptedPath(DB_POSITION_STR + "/table");

    public static String Index_POSITION = PathUtil.getSystemAdaptedPath(DB_POSITION_STR + "/index");

    public static String COLUMN_POSITION = PathUtil.getSystemAdaptedPath(DB_POSITION_STR + "/column");
}
