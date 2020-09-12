package com.bit.bplustree.mytree;

import com.bit.utils.FileUtil;
import com.bit.utils.KryoUtil;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/10 5:53 下午
 */
public class BplusTree {


    private String filePath = "/tmp/bPlus";

    /**
     * 根节点
     */
    protected AbstractNode root;

    /**
     * 阶数，M值
     */
    protected int nodeOrder = 4;

    /**
     * 叶子结点储存的索引的个数
     */
    protected int leafOrder = 4;

    /**
     * 叶子节点的链表头
     */
    protected AbstractNode head;

    protected Map<Long, AbstractNode> nodeCache = new HashMap<>();


    public BplusTree(String filePath, int nodeOrder, int leafOrder) {
        if (nodeOrder < 3 || leafOrder < 3) {
            System.out.print("order must be greater than 2");
            System.exit(0);
        }
        this.filePath = filePath;
        this.nodeOrder = nodeOrder;
        this.leafOrder = leafOrder;

        File file = new File(filePath);
        long size = file.length();
        // 创建根节点
        if (size == 0) {
            root = new LeafNode(-1L);
            FileOutputStream fileOutputStream = FileUtil.getFileOutputStream(filePath);
            Output output = new Output(fileOutputStream);
            KryoUtil.serialize(root, output);
            FileUtil.closeOutputSteam(fileOutputStream);
        } else {
            //读取根节点
            FileInputStream fileInputStream = FileUtil.getFileInputStream(filePath);
            Node node = (Node) KryoUtil.deserialize(fileInputStream);
            FileUtil.closeInputSteam(fileInputStream);
            while (node.parent != -1) {
                node = (Node) getNode(node.parent);
            }
            root = node;
            nodeCache.put(0L, root);
        }

        head = root;
    }

    public int getNodeOrder() {
        return nodeOrder;
    }

    public void setNodeOrder(int nodeOrder) {
        this.nodeOrder = nodeOrder;
    }

    public int getLeafOrder() {
        return leafOrder;
    }

    public void setLeafOrder(int leafOrder) {
        this.leafOrder = leafOrder;
    }

    public AbstractNode getNode(Long num) {
        if (nodeCache.get(num) == null) {
            loadAbstractNode(num);
        }
        AbstractNode node = nodeCache.get(num);
        return node;
    }

    private void loadAbstractNode(Long num) {
        // 页数
        FileInputStream fileInputStream = FileUtil.getFileInputStream(filePath);
        AbstractNode node = null;
        try {
            Input input = new Input(fileInputStream);
            input.setPosition((int) (num * 4096));
            node = (AbstractNode) KryoUtil.deserialize(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        nodeCache.put(num, node);
    }

    public Long newNode(Long parent) {
        File file = new File(filePath);
        long size = file.length();
        // 创建根节点
        FileOutputStream fileOutputStream = FileUtil.getFileOutputStream(filePath);
        Output output = new Output(fileOutputStream);
        Node node = new Node(parent);
        output.setPosition((int) ((size / 4096 + 1) * 4096));
        KryoUtil.serialize(node, output);
        return 0L;
    }

    public Long newLeaf(Long parent) {
        File file = new File(filePath);
        long size = file.length();
        // 创建根节点
        FileOutputStream fileOutputStream = FileUtil.getFileOutputStream(filePath);
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "w");
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Output output = new Output(fileOutputStream);
        LeafNode leafNode = new LeafNode(parent);
        output.setPosition((int) ((size / 4096 + 1) * 4096));
        KryoUtil.serialize(leafNode, output);
        nodeCache.put((size / 4096), leafNode);
        return 0L;
    }

    public Long getNum(AbstractNode node) {
        for (Map.Entry<Long, AbstractNode> entry : nodeCache.entrySet()) {
            if (entry.getValue() == node) {
                return entry.getKey();
            }
        }
        return -1L;
    }

    public Long get(Comparable key) {
        return root.get(key, this);
    }

    public void remove(Comparable key) {

    }

    public void insert(Point point) {
        root.insert(point, this);
    }

    public void updateToFile(Long num) {
        if (nodeCache.get(num) != null) {
            AbstractNode node = nodeCache.get(num);
            FileOutputStream fileOutputStream = FileUtil.getFileOutputStream(filePath);
            Output output = new Output(fileOutputStream);
            output.setPosition((int) (num*4096));
            KryoUtil.serialize(node, output);
            FileUtil.closeOutputSteam(fileOutputStream);
        }
    }
}
