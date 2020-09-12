package com.bit.utils;

import java.io.*;

/**
 * @author aerfafish
 * @date 2020/9/10 8:16 下午
 */
public class FileUtil {

    public static FileInputStream getFileInputStream(String path) {
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void closeInputSteam(InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileOutputStream getFileOutputStream(String path) {
        try {
            return new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void closeOutputSteam(OutputStream outputStream) {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
