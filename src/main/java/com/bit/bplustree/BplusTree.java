package com.bit.bplustree;

import com.bit.api.model.IndexQuery;
import com.bit.utils.FileUtil;
import com.bit.utils.KryoUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        if (!file.exists()) {
            FileUtil.createNewFile(filePath);
        }
        long size = file.length();
        // 创建根节点
        if (size == 0) {
            root = new LeafNode(-1L);
            byte[] bytes = KryoUtil.serialize(root);
            FileUtil.writeFileByte(filePath, 0L, bytes);
            nodeCache.put(0L, root);
        } else {
            //读取根节点
            byte[] fileByte = FileUtil.getFileByte(filePath, 0L);
            AbstractNode node = (AbstractNode) KryoUtil.deserialize(fileByte);
            nodeCache.put(0L, node);
            while (node.parent != -1) {
                Long parent = node.parent;
                node = (Node) getNode(parent);
                nodeCache.put(parent, node);
            }
            root = node;
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
        AbstractNode node = null;
        try {
            byte[] fileByte = FileUtil.getFileByte(filePath, num);
            node = (AbstractNode) KryoUtil.deserialize(fileByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        nodeCache.put(num, node);
    }

    public Long newNode(Long parent) {
        File file = new File(filePath);
        long size = file.length();
        // 创建根节点
        Node node = new Node(parent);
        Long num = size / 4096 + 1;
        byte[] bytes = KryoUtil.serialize(node);
        FileUtil.writeFileByte(filePath, num, bytes);
        nodeCache.put(num, node);
        return num;
    }

    public Long newLeaf(Long parent) {
        File file = new File(filePath);
        long size = file.length();
        LeafNode leafNode = new LeafNode(parent);
        Long num = size / 4096 + 1;
        byte[] bytes = KryoUtil.serialize(leafNode);
        FileUtil.writeFileByte(filePath, num, bytes);
        nodeCache.put(num, leafNode);
        return num;
    }

    public Long getNum(AbstractNode node) {
        for (Map.Entry<Long, AbstractNode> entry : nodeCache.entrySet()) {
            if (entry.getValue() == node) {
                return entry.getKey();
            }
        }
        return -1L;
    }

    public Set<Long> get(Comparable key) {
        return root.get(key, this);
    }

    public Set<Long> get(IndexQuery query) {
        return root.get(query, this);
    }

    public void remove(Point point) {
        root.delete(point, this);
    }

    public void update(Comparable key, Long value, Long newValue) {
        root.updatePoint(key, value, newValue, this);
    }

    public void insert(Point point) {
        root.insert(point, this);
    }

    public void updateToFile(Long num) {
        if (nodeCache.get(num) != null) {
            AbstractNode node = nodeCache.get(num);
            byte[] bytes = KryoUtil.serialize(node);
            FileUtil.writeFileByte(filePath, num, bytes);
        }
    }

    public void updateToFile() {
        for (long i = 0; i < nodeCache.size(); i++) {
            AbstractNode node = nodeCache.get(i);
            byte[] bytes = KryoUtil.serialize(node);
            FileUtil.writeFileByte(filePath, i, bytes);
        }
    }

    public void clearCache() {
        nodeCache.clear();
    }
}
