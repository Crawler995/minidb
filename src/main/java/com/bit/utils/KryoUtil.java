package com.bit.utils;

import com.bit.constance.DataType;
import com.bit.model.Table;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/9 4:11 下午
 */
public class KryoUtil {
    static Kryo kryo = new Kryo();

//    static {
//        kryo.register(MusicInfo.class, new CompatibleFieldSerializer<MusicInfo>(myKryo, Media.class), 50);
//    }

    public static byte[] serialize(Object obj) {
        byte[] buffer = new byte[4096];
        Output output = new Output(buffer);
        return serialize(obj, output);
    }

    public static byte[] serialize(Object obj, Output output) {
        kryo.writeClassAndObject(output, obj);
        byte[] bs = output.toBytes();
        output.close();
        return bs;
    }

    public static Object deserialize(byte[] src) {
        Input input = new Input(src);
        Object obj = kryo.readClassAndObject(input);
        input.close();
        return obj;
    }

    public static Object deserialize(Input input) {
        Object obj = kryo.readClassAndObject(input);
        input.close();
        return obj;
    }

    public static void main(String[] args) {

        deSerialTest();
    }

    private static void serialTest() {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream("/tmp/test.kryo", true);
            Output output = new Output(fileOutputStream);
            Table table = new Table();
            table.setName("cat");
            Map<String, Integer> typeMap = new HashMap<>();
            typeMap.put("age", DataType.INT.ordinal());
            table.setType(typeMap);
            byte[] bytes = serialize(table, output);
            System.out.println(bytes);
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

    private static void deSerialTest() {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream("/tmp/test.kryo");
            Input input = new Input(fileInputStream, 1024*1024);

            while (true) {
                Object o = deserialize(input);
                System.out.println(o);
            }
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
}
