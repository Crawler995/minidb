package com.bit.bplustree.mytree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author aerfafish
 * @date 2020/9/11 1:56 下午
 */
public class LeafNode extends AbstractNode {

    /**
     * 叶节点的前节点
     */
    protected Long prev;

    /**
     * 叶节点的后节点
     */
    protected Long next;

    /**
     * 页节点的关键字
     */
    protected List<Point> points = new ArrayList<Point>();
    ;

    public LeafNode() {}

    public LeafNode(Long parent) {
        super(parent, true);
    }

    public Long getPrev() {
        return prev;
    }

    public void setPrev(Long prev) {
        this.prev = prev;
    }

    public Long getNext() {
        return next;
    }

    public void setNext(Long next) {
        this.next = next;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    @Override
    public Long get(Comparable key, BplusTree tree) {
        for (Point point : points) {
            // 如果找到该值
            if (point.getKey().compareTo(key) == 0) {
                return point.getValue();
            }
        }
        // 如果一个都没找到返回空值
        return -1L;
    }

    @Override
    public void insert(Point insertPoint, BplusTree tree) {
        if (points.size() == 0) {
            points.add(insertPoint);
        } else {
            for (int i = 0; i < points.size(); i++) {
                if (points.get(i).getKey().compareTo(insertPoint.getKey()) > 0) {
                    points.add(i, insertPoint);
                    break;
                }
                // 如果所有的都比他小
                if (i == points.size() - 1) {
                    points.add(points.size(), insertPoint);
                    break;
                }
            }
        }
        int leafOrder = tree.getLeafOrder();
        // 如果大于等于阶数 分裂
        if (points.size() >= leafOrder) {
            // 提取出中间的结点
            Point middlePoint = points.get(points.size() / 2);
            if (parent == -1) {
                Long rootNode = tree.newNode(-1L);
                Node root = (Node) tree.getNode(rootNode);
                Long rightNodeNum = tree.newLeaf(rootNode);
                LeafNode rightNode = (LeafNode) tree.getNode(rightNodeNum);
                parent = rootNode;
                List<Point> subPoints  = new ArrayList<>();
                subPoints.addAll(points.subList(0, points.size()/2));
                List<Point> subRightPoints  = new ArrayList<>();
                subRightPoints.addAll(points.subList(points.size()/2, points.size()));
                points = subPoints;
                rightNode.setPoints(subRightPoints);
                root.addPoint(new Point(-1L, tree.getNum(this)), tree);
                root.addPoint(new Point(middlePoint.getKey(), rightNodeNum), tree);
                tree.updateToFile(rightNodeNum);
            } else {
                Long leafNum = tree.newLeaf(parent);
                LeafNode newLeafNode = (LeafNode) tree.getNode(leafNum);
                List<Point> subPoints  = new ArrayList<>();
                subPoints.addAll(points.subList(0, points.size()/2));
                List<Point> subNewPoints  = new ArrayList<>();
                subNewPoints.addAll(points.subList(points.size()/2, points.size()));
                newLeafNode.setPoints(subNewPoints);
                newLeafNode.setNext(next);
                newLeafNode.setPrev(tree.getNum(this));
                points = subPoints;
                next = leafNum;
                Node parentNode = (Node) tree.getNode(parent);
                parentNode.addPoint(new Point(middlePoint.getKey(), leafNum), tree);
                tree.updateToFile(leafNum);
            }
            tree.updateToFile(tree.getNum(this));
        }
    }
}
