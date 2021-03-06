package com.bit.bplustree;

import com.bit.api.model.IndexQuery;

import java.util.Set;

/**
 * @author aerfafish
 * @date 2020/9/11 1:41 下午
 */
public abstract class AbstractNode {

    public AbstractNode() {}

    public AbstractNode(Long parent, Boolean isLeaf) {
        this.parent = parent;
        this.isLeaf = isLeaf;
    }

    /** 父亲节点 */
    protected Long parent;

    /** 是否是叶子节点 */
    protected Boolean isLeaf;

    /** 根据key获取对应的索引（页号）
     * @return*/
    public abstract Set<Long> get(Comparable key, BplusTree tree);

    public abstract Set<Long> get(IndexQuery query, BplusTree tree);

    /** 插入指定的Point */
    public abstract void insert(Point point, BplusTree tree);

    public abstract void delete(Point point, BplusTree tree);

    public abstract void updatePoint(Comparable key, Long value, Long newValue, BplusTree tree);

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

}
