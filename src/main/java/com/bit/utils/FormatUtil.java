package com.bit.utils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author aerfafish
 * @date 2020/9/8 11:39 上午
 */
public class FormatUtil {

    public static String bytes2String(byte[] buffer) {
        try {
            int length = 0;
            for (int i = 0; i < buffer.length; ++i) {
                if (buffer[i] == 0) {
                    length = i;
                    break;
                }
            }
            return new String(buffer, 0, length, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static byte[] string2Bytes(String str) {
        try {
            byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
            return Arrays.copyOf(strBytes, strBytes.length + 1);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
