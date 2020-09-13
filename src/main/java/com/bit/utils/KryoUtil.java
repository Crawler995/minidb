package com.bit.utils;

import com.bit.bplustree.mytree.AbstractNode;
import com.bit.bplustree.mytree.LeafNode;
import com.bit.bplustree.mytree.Node;
import com.bit.bplustree.mytree.Point;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.InputStream;

/**
 * @author aerfafish
 * @date 2020/9/9 4:11 下午
 */
public class KryoUtil {
    static Kryo kryo = new Kryo();

    static {
        kryo.register(Point.class);
        kryo.register(AbstractNode.class);
        kryo.register(LeafNode.class);
        kryo.register(Node.class);

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
}
