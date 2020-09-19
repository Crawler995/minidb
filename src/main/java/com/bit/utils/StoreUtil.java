package com.bit.utils;

import java.io.FileOutputStream;

/**
 * @author aerfafish
 * @date 2020/9/15 9:23 下午
 */
public class StoreUtil {

    public static void storeToFile(Object obj, String filePath) {
        byte[] bytes = KryoUtil.serialize(obj);
        FileOutputStream fileOutputStream = FileUtil.getFileOutputStream(filePath);
        FileUtil.writeFileByte(fileOutputStream, bytes);
        FileUtil.closeOutputSteam(fileOutputStream);
    }
}
