package com.bit;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * @author aerfafish
 * @date 2020/9/7 4:26 下午
 */
public class MyTest {

    @Test
    public void writeTest() {
        final int size = 4 * 1024;
        File file = new File("/tmp/test");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            for (int i = 0; i < size; i++) {
                fileOutputStream.write(new Random().nextInt(65535));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Test
    public void readTest() {
        File file = new File("/tmp/test");
        FileInputStream fileInputStream = null;
        long startTime = System.nanoTime();
        int size = (int) file.length();
        for (int i = 0 ; i < 1; i++) {
            try {
                fileInputStream = new FileInputStream(file);
                byte[] bytes = new byte[size];
                fileInputStream.read(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        long endTime = System.nanoTime();
        System.out.println("消耗时间：" + (endTime - startTime));
    }

    @Test
    public void readNioTest() {
        File file = new File("/tmp/test");
        FileInputStream fileInputStream = null;

        long startTime = System.currentTimeMillis();
        int size = (int) file.length();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        for (int i = 0 ; i < 2000; i++) {
            try {
                byteBuffer.clear();
                fileInputStream = new FileInputStream(file);
                fileInputStream.getChannel().read(byteBuffer);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("消耗时间：" + (endTime - startTime));
    }

    @Test
    public void readBufferTest() {
        File file = new File("/tmp/test");
        BufferedInputStream bufferedInputStream = null;
        long startTime = System.currentTimeMillis();
        int size = (int) file.length();
        for (int i = 0 ; i < 2000; i++) {
            try {
                byte[] bytes = new byte[size];
                bufferedInputStream = new BufferedInputStream(new FileInputStream("/tmp/test"));
                bufferedInputStream.read(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bufferedInputStream != null) {
                    try {
                        bufferedInputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("消耗时间：" + (endTime - startTime));
    }

    @Test
    public void readReaderTest() {
        File file = new File("/tmp/test");
        BufferedReader bufferedReader = null;
        long startTime = System.currentTimeMillis();
        int size = (int) file.length();
        for (int i = 0 ; i < 2000; i++) {
            try {
                bufferedReader = new BufferedReader(new FileReader("/tmp/test"));
                bufferedReader.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("消耗时间：" + (endTime - startTime));
    }
}
