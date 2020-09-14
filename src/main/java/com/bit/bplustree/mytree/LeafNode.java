package com.bit.bplustree.mytree;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author aerfafish
 * @date 2020/9/11 1:56 下午
 */
@Data
public class LeafNode extends AbstractNode {

    /**
     * 叶节点的前节点
     */
    protected Long prev = -1L;

    /**
     * 叶节点的后节点
     */
    protected Long next = -1L;

    /**
     * 页节点的关键字
     */
    protected List<Point> points = new ArrayList<Point>();
    ;

    public LeafNode() {
    }

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
    public List<Long> get(Comparable key, BplusTree tree) {
        List<Long> resultList = new ArrayList<>();
        LeafNode node = this;
        boolean flag = false;
        while (node != null) {
            for (Point point : node.points) {
                if (point.getKey().compareTo(key) > 0) {
                    return resultList;
                }
                // 如果找到该值
                if (point.getKey().compareTo(key) == 0) {
                    resultList.add(point.getValue());
                    flag = true;
                }
            }
            // 一个都没找到
            if (!flag) {
                return Collections.singletonList(-1L);
            } else {
                if (next == -1) {
                    return resultList;
                }
                resultList.addAll(tree.getNode(next).get(key, tree));
                return resultList;
            }
        }
        // 如果一个都没找到返回空值
        return Collections.singletonList(-1L);
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
                tree.root = root;
                Long rightNodeNum = tree.newLeaf(rootNode);
                LeafNode rightNode = (LeafNode) tree.getNode(rightNodeNum);
                parent = rootNode;
                rightNode.prev = tree.getNum(this);
                rightNode.next = next;
                next = rightNodeNum;
                List<Point> subPoints = new ArrayList<>();
                subPoints.addAll(points.subList(0, points.size() / 2));
                List<Point> subRightPoints = new ArrayList<>();
                subRightPoints.addAll(points.subList(points.size() / 2, points.size()));
                points = subPoints;
                rightNode.setPoints(subRightPoints);
                root.addPoint(tree.getNum(this), new Point(-1L, tree.getNum(this)), tree);
                root.addPoint(tree.getNum(this), new Point(middlePoint.getKey(), rightNodeNum), tree);
                tree.updateToFile(rightNodeNum);
            } else {
                Long leafNum = tree.newLeaf(parent);
                LeafNode newLeafNode = (LeafNode) tree.getNode(leafNum);
                List<Point> subPoints = new ArrayList<>();
                subPoints.addAll(points.subList(0, points.size() / 2));
                List<Point> subNewPoints = new ArrayList<>();
                subNewPoints.addAll(points.subList(points.size() / 2, points.size()));
                newLeafNode.setPoints(subNewPoints);
                newLeafNode.setNext(next);
                newLeafNode.setPrev(tree.getNum(this));
                points = subPoints;
                next = leafNum;
                Node parentNode = (Node) tree.getNode(parent);
                parentNode.addPoint(tree.getNum(this), new Point(middlePoint.getKey(), leafNum), tree);
                tree.updateToFile(leafNum);
            }
            tree.updateToFile(tree.getNum(this));
        }
    }

    @Override
    public void delete(Point point, BplusTree tree) {
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).getKey().compareTo(point.getKey()) == 0
                    && points.get(i).getValue().equals(point.getValue())) {
                points.remove(i);
                break;
            }
        }
        if (parent == -1) {
            return;
        }
        // 合并
        if (points.size() < (tree.getLeafOrder() + 1) / 2 - 1) {
            LeafNode leftLeafNode = (LeafNode) tree.getNode(prev);
            LeafNode rightLeafNode = (LeafNode) tree.getNode(next);
            if (leftLeafNode.points.size() > ((tree.getLeafOrder() + 1) / 2 - 1)) {
                Point removePoint = leftLeafNode.points.remove(points.size() - 1);
                points.add(0, removePoint);
            } else if (rightLeafNode.points.size() > ((tree.getLeafOrder() + 1) / 2 - 1)) {
                Point removePoint = rightLeafNode.points.remove(points.size() - 1);
                points.add(removePoint);
            } else {
                // 如果没多余的，则合并
                leftLeafNode.points.addAll(points);
                Node parentNode = (Node) tree.getNode(parent);
                parentNode.deletePoint(tree.getNum(this), tree);
            }
        }
    }

    @Override
    public void updatePoint(Comparable key, Long value, Long newValue, BplusTree tree) {
        for (Point point : points) {
            if (point.getKey().compareTo(key) == 0 && point.getValue().equals(value)) {
                point.setValue(newValue);
                break;
            }
        }
    }

}
