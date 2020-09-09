package com.bit.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;

public class PathUtil {

    /**
     * 是否为Windows环境。
     */
    public static final boolean isWin = System.getProperty("os.name").toLowerCase().startsWith("win");

    /**
     * e.g.
     * getSystemAdaptedPath("/tmp/minidb") = "C:\\User\\xxxx\\tmp\\minidb" (Windows下）
     *                                     = "/tmp/minidb" （非Windows下）
     */
    public static String getSystemAdaptedPath(String filepath) {
        if(!isWin) return filepath;

        // C:\\fa\\s/tmp/db -> C:\\fa\\s\\tmp\\db
        return (System.getProperty("user.home") + filepath).replaceAll("/", Matcher.quoteReplacement("\\"));
    }

    /**
     * e.g.
     * ensureDir("C:\\fa\\s\\tmp\\db") -> 确保C:\\fa\\s\\tmp文件夹存在
     */
    public static void ensureDir(String filepath) {
        String[] t = filepath.split("[/\\\\]");
        t[t.length - 1] = "";

        String dirPath = String.join(isWin ? "\\" : "/", t);
        File dir = new File(dirPath);
        dir.mkdirs();
    }

    /**
     * e.g.
     * ensureDir("C:\\fa\\s\\tmp\\db") -> 确保C:\\fa\\s\\tmp\\db文件存在
     */
    public static void ensureFile(String filepath) {
        ensureDir(filepath);
        File file = new File(filepath);
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
