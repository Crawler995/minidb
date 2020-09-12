package com.bit.bplustree.mytree;

/**
 * @author aerfafish
 * @date 2020/9/10 4:30 下午
 */

import java.util.List;

/**
 * 存在问题 如何通过页号找到页
 */
public class Node extends AbstractNode {

    /**
     * 子节点
     */
    protected List<Point> children;


    public Node(Long parent) {
        super(parent, false);
    }


    @Override
    public Long get(Comparable key, BplusTree tree) {
        for (int i = 0; i <= children.size(); i++) {
            if (key.compareTo(children.get(i).getKey()) < 0) {
                AbstractNode node = tree.getNode(children.get(i - 1).getValue());
                return node.get(key, tree);
            }
        }
        return -1L;
    }

    // 从上往下
    @Override
    public void insert(Point newPoint, BplusTree tree) {
        for (int i = 0; i <= children.size(); i++) {
            if (newPoint.getKey().compareTo(children.get(i).getKey()) < 0) {
                AbstractNode node = tree.getNode(children.get(i - 1).getValue());
                node.insert(newPoint, tree);
                return;
            }
        }
    }

    // 从下往上
    public void addPoint(Point newPoint, BplusTree tree) {
        for (int i = 0; i < children.size(); i++) {
            Point point = children.get(i);
            if (newPoint.getKey().compareTo(point.getKey()) < 0) {
                children.add(i, newPoint);
                break;
            }
            if (i == children.size() - 1) {
                children.add(children.size(), newPoint);
            }
        }
        // 分裂
        if (children.size() > tree.getNodeOrder()) {
            //如果是根节点
            Point middlePoint = children.get(children.size() / 2);
            if (parent == -1L) {
                Long rootNode = tree.newNode(-1L);
                Node root = (Node) tree.getNode(rootNode);
                Long rightNodeNum = tree.newNode(0L);
                Node rightNode = (Node) tree.getNode(rightNodeNum);
                parent = 0L;
                children = children.subList(0, children.size() / 2);
                rightNode.setChildren(children.subList(children.size() / 2, children.size()));
                root.addPoint(new Point(-1, tree.getNum(this)), tree);
                root.addPoint(new Point(middlePoint.getKey(), rightNodeNum), tree);
                tree.updateToFile(rightNodeNum);
            } else {
                Long nodeNum = tree.newNode(parent);
                Node newNode = (Node) tree.getNode(nodeNum);
                newNode.setChildren(children.subList(children.size() / 2, children.size()));
                ((Node) tree.getNode(parent)).addPoint(new Point(middlePoint.getKey(), nodeNum), tree);
                tree.updateToFile(nodeNum);
            }
        }
        tree.updateToFile(tree.getNum(this));
    }

    public List<Point> getChildren() {
        return children;
    }

    public void setChildren(List<Point> children) {
        this.children = children;
    }
}
