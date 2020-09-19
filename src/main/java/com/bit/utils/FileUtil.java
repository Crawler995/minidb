package com.bit.utils;

import java.io.*;

/**
 * @author aerfafish
 * @date 2020/9/10 8:16 下午
 */
public class FileUtil {

    public static FileInputStream getFileInputStream(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                createNewFile(path);
            }
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
        return getFileOutputStream(path, true);
    }

    public static FileOutputStream getFileOutputStream(String path, Boolean isCover) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                createNewFile(path);
            }
            return new FileOutputStream(path, !isCover);
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

    public static byte[] getFileByte(String filePath, Long num) {
        return getFileByte(filePath, num, 4096);
    }

    public static byte[] getFileByte(String filePath, Long num, int pageSize) {
        RandomAccessFile randomAccessFile = null;
        byte[] bytes = new byte[Math.toIntExact(pageSize)];
        try {
            if (!new File(filePath).exists()) {
                FileUtil.createNewFile(filePath);
            }
            randomAccessFile = new RandomAccessFile(filePath, "r");
            randomAccessFile.seek(num * pageSize);
            randomAccessFile.read(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }

    public static void writeFileByte(String filePath, Long num, byte[] bytes) {
        writeFileByte(filePath, num, bytes, 4096);
    }

    public static void writeFileByte(String filePath, Long num, byte[] bytes, int pageSize) {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(filePath, "rw");
            randomAccessFile.seek(num * pageSize);
            randomAccessFile.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void writeFileByte(FileOutputStream fileOutputStream, byte[] bytes) {
        if (fileOutputStream == null) {
            return;
        }
        try {
            fileOutputStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createNewFile(String path) {
        File file = new File(path);
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
