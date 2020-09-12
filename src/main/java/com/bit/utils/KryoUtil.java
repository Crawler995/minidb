package com.bit.utils;

import com.bit.bplustree.mytree.Point;
import com.bit.constance.DataType;
import com.bit.model.Table;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/9 4:11 下午
 */
public class KryoUtil {
    static Kryo kryo = new Kryo();

    static {
        kryo.register(Point.class);
    }

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

    public static Object deserialize(InputStream inputStream) {
        return deserialize(inputStream, 4096);
    }

    public static Object deserialize(InputStream inputStream, int buffSize) {
        Input input = new Input(inputStream, buffSize);
        return deserialize(input);
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
//        serialTest();
        deSerialTest();
    }

    private static void serialTest() {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream("/tmp/test.kryo", true);
            Output output = new Output(fileOutputStream);
//            Table table = new Table();
//            table.setName("cat");
//            Map<String, Integer> typeMap = new HashMap<>();
//            typeMap.put("age", DataType.INT.ordinal());
//            table.setType(typeMap);
            Node node0 = new Node();
            node0.value = 4;
            Node node1 = new Node();
            node1.value = 3;
            node0.next = node1;
            node1.prev = node0;
            byte[] bytes = serialize(node0, output);
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

    static class Node {
        public Node prev = null;

        public Node next = null;

        public int value;

    }
}
