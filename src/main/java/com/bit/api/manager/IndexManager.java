package com.bit.api.manager;

import com.bit.bplustree.BplusTree;
import com.bit.bplustree.Point;
import com.bit.api.model.IndexQuery;

import java.util.Set;

/**
 * @author aerfafish
 * @date 2020/9/18 5:16 下午
 */
public class IndexManager {

    public IndexManager(String filePath) {
        this.filePath = filePath;
        initIndexManager();
    }

    private String filePath;

    private BplusTree tree;

    public void insert(Comparable key, Long pageNum) {
        tree.insert(new Point(key, pageNum));
    }

    public void insert(Comparable key, Long pageNum, Boolean isStore) {
        tree.insert(new Point(key, pageNum));
    }

    public Set<Long> select(Comparable key) {
        return tree.get(key);
    }

    public Set<Long> select(IndexQuery query) {
        return tree.get(query);
    }

    public void delete(Comparable key, Long pageNum) {
        tree.remove(new Point(key, pageNum));
    }

    public void update(Comparable key, Long originPageNum, Long newPageNum) {
        tree.update(key, originPageNum, newPageNum);
    }

    public void updateToFile() {
        tree.updateToFile();
    }

    private void initIndexManager() {
        tree = new BplusTree(filePath, 100, 100);
    }

    public void clearCache() {
        tree.clearCache();
    }
}
