package com.bit.bplustree;

import com.bit.api.model.Criteria;
import com.bit.api.model.IndexQuery;
import lombok.Data;

import java.util.*;

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
    public Set<Long> get(Comparable key, BplusTree tree) {
        Set<Long> resultList = new HashSet<>();
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
            if (next == -1) {
                return resultList;
            }
            resultList.addAll(tree.getNode(next).get(key, tree));
            return resultList;
        }
        // 如果一个都没找到返回空值
        return Collections.singleton(-1L);
    }

    public Set<Long> get(IndexQuery query, BplusTree tree) {
        Set<Long> resultList = new HashSet<>();
        LeafNode node = this;
        boolean flag = false;
        while (node != null) {
            for (Point point : node.points) {
                if (compare(point.getKey(), query) > 0) {
                    return resultList;
                }
                // 如果找到该值
                if (compare(point.getKey(), query) == 0) {
                    resultList.add(point.getValue());
                    flag = true;
                }
            }
            if (next == -1) {
                return resultList;
            }
            resultList.addAll(tree.getNode(next).get(query, tree));
            return resultList;
        }
        // 如果一个都没找到返回空值
        return Collections.singleton(-1L);
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
        }
//        tree.updateToFile(tree.getNum(this));
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
            if (prev != -1
                    && ((LeafNode) tree.getNode(prev)).points.size() > ((tree.getLeafOrder() + 1) / 2 - 1)) {
                LeafNode leftLeafNode = (LeafNode) tree.getNode(prev);
                Point removePoint = leftLeafNode.points.remove(points.size() - 1);
                points.add(0, removePoint);
                tree.updateToFile(prev);
            } else if (next != -1
                    && ((LeafNode) tree.getNode(next)).points.size() > ((tree.getLeafOrder() + 1) / 2 - 1)) {
                LeafNode rightLeafNode = (LeafNode) tree.getNode(next);
                Point removePoint = rightLeafNode.points.remove(points.size() - 1);
                points.add(removePoint);
                tree.updateToFile(next);
            } else {
                // 如果没多余的，则合并
                if (prev != -1) {
                    LeafNode leftLeafNode = (LeafNode) tree.getNode(prev);
                    leftLeafNode.points.addAll(points);
                    leftLeafNode.next = next;
                    tree.updateToFile(prev);
                    Node parentNode = (Node) tree.getNode(parent);
                    parentNode.deletePoint(tree.getNum(this), tree);
                } else if (next != -1) {
                    LeafNode rightLeafNode = (LeafNode) tree.getNode(next);
                    rightLeafNode.points.addAll(0, points);
                    rightLeafNode.prev = prev;
                    tree.updateToFile(next);
                    Node parentNode = (Node) tree.getNode(parent);
                    parentNode.deletePoint(tree.getNum(this), tree);
                }

            }
            tree.updateToFile(tree.getNum(this));
        }
    }

    @Override
    public void updatePoint(Comparable key, Long value, Long newValue, BplusTree tree) {
        LeafNode node = this;
        while (node != null) {
            for (Point point : node.points) {
                if (point.getKey().compareTo(key) > 0) {
                    return;
                }
                // 如果找到该值
                if (point.getKey().compareTo(key) == 0 && point.getValue().equals(value)) {
                    point.setValue(newValue);
                }
            }
            if (next == -1) {
                return;
            }
            tree.getNode(next).updatePoint(key, value, newValue, tree);
            return;
        }
    }

    private int compare(Comparable comparable, IndexQuery query) {
        Criteria criteria = query.getCriteria();
        if (criteria.getCriteria().size() == 0) {
            return 0;
        }
        if (comparable.compareTo(query.getLowKey()) < 0) {
            return -1;
        }
        if (comparable.compareTo(query.getLowKey()) == 0 && !query.isEq()) {
            return -1;
        }

        for (Map.Entry<String, Comparable> entry : criteria.getCriteria().entrySet()) {
            if (entry.getKey().equals("gt")) {
                // 如果比他小于等于
                if (comparable.compareTo(criteria) <= 0) {
                    return -1;
                }
            }
            if (entry.getKey().equals("$gte")) {
                if (comparable.compareTo(criteria) < 0) {
                    return -1;
                }
            }
            if (entry.getKey().equals("$lt")) {
                if (comparable.compareTo(criteria) >= 0) {
                    return 1;
                }
            }
            if (entry.getKey().equals("$lte")) {
                if (comparable.compareTo(criteria) > 0) {
                    return 1;
                }
            }
            if (entry.getKey().equals("$ne")) {
                if (comparable.compareTo(criteria) == 0) {
                    return 1;
                }
            }
        }
        return 0;
    }

}
