package com.bit.bplustree.mytree;

/**
 * @author aerfafish
 * @date 2020/9/10 4:30 下午
 */

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 存在问题 如何通过页号找到页
 */
@Data
public class Node extends AbstractNode {

    /**
     * 子节点
     */
    protected List<Point> children = new ArrayList<Point>();

    public Node() {}

    public Node(Long parent) {
        super(parent, false);
    }


    @Override
    public Long get(Comparable key, BplusTree tree) {
        for (int i = 0; i < children.size(); i++) {
            if (key.compareTo(children.get(i).getKey()) < 0) {
                AbstractNode node = tree.getNode(children.get(i - 1).getValue());
                return node.get(key, tree);
            }
            if (i == children.size()-1) {
                AbstractNode node = tree.getNode(children.get(i).getValue());
                return node.get(key, tree);
            }
        }
        return -1L;
    }

    // 从上往下
    @Override
    public void insert(Point newPoint, BplusTree tree) {
        for (int i = 0; i < children.size(); i++) {
            if (newPoint.getKey().compareTo(children.get(i).getKey()) < 0) {
                AbstractNode node = tree.getNode(children.get(i - 1).getValue());
                node.insert(newPoint, tree);
                return;
            }
        }
    }

    @Override
    public void delete(Point deletePoint, BplusTree tree) {
        for ( Point point: children) {
            if (point.getKey().compareTo(deletePoint.getKey()) < 0) {
                tree.getNode(point.getValue()).delete(deletePoint, tree);
                break;
            }
        }
    }

    // 从下往上
    public void addPoint(Point newPoint, BplusTree tree) {
        if (children.size() == 0) {
            children.add(newPoint);
        } else {
            for (int i = 0; i < children.size(); i++) {
                Point point = children.get(i);
                if (newPoint.getKey().compareTo(point.getKey()) < 0) {
                    children.add(i, newPoint);
                    break;
                }
                if (i == children.size() - 1) {
                    children.add(children.size(), newPoint);
                    break;
                }
            }
        }
        // 分裂
        if (children.size() > tree.getNodeOrder()) {
            //如果是根节点
            Point middlePoint = children.get(children.size() / 2);
            if (parent == -1L) {
                Long rootNode = tree.newNode(-1L);
                Node root = (Node) tree.getNode(rootNode);
                tree.root = root;
                Long rightNodeNum = tree.newNode(rootNode);
                Node rightNode = (Node) tree.getNode(rightNodeNum);
                parent = rootNode;
                List<Point> subChildren  = new ArrayList<>();
                subChildren.addAll(children.subList(0, children.size()/2));
                List<Point> subRightChildren  = new ArrayList<>();
                subRightChildren.addAll(children.subList(children.size()/2, children.size()));
                children = subChildren;
                rightNode.setChildren(subRightChildren);
                root.addPoint(new Point(-1L, tree.getNum(this)), tree);
                root.addPoint(new Point(middlePoint.getKey(), rightNodeNum), tree);
                tree.updateToFile(rightNodeNum);
            } else {
                Long nodeNum = tree.newNode(parent);
                Node newNode = (Node) tree.getNode(nodeNum);
                List<Point> subChildren  = new ArrayList<>();
                subChildren.addAll(children.subList(0, children.size()/2));
                List<Point> subNewChildren  = new ArrayList<>();
                subNewChildren.addAll(children.subList(children.size()/2, children.size()));
                children = subChildren;
                newNode.setChildren(subNewChildren);
                ((Node) tree.getNode(parent)).addPoint(new Point(middlePoint.getKey(), nodeNum), tree);
                tree.updateToFile(nodeNum);
            }
        }
        tree.updateToFile(tree.getNum(this));
    }

    public void deletePoint(Long value, BplusTree tree) {
        for (Point point : children) {
            if (point.getValue().equals(value)) {
                children.remove(point);
                break;
            }
        }
        if (parent == -1) {
            if (children.size() == 1) {
                AbstractNode node = tree.getNode(children.get(0).getValue());
                node.parent = -1L;
                tree.root = node;
            }
            return;
        }
        Node parentNode = (Node) tree.getNode(parent);
        if (!parentNode.getExtraNode(value, tree)) {
            mergeNode(value, tree);
            parentNode.deletePoint(tree.getNum(this), tree);
        }
    }

    public void updatePoint(Comparable key, Long value, Long newValue, BplusTree tree) {
        for (Point point : children) {
            if (point.getKey().compareTo(key) < 0) {
                tree.getNode(point.getValue()).updatePoint(key, value, newValue, tree);
                break;
            }
        }
    }

    public Boolean getExtraNode(Long value, BplusTree tree) {
        for (int i = 0; i < children.size(); i++) {
            Point point = children.get(i);
            if (value.equals(point.getValue())) {
                // 左兄弟节点有富余
                if (i > 0) {
                    Node leftNode = (Node) tree.getNode(children.get(i - 1).getValue());
                    if (leftNode.children.size() > (tree.getNodeOrder()+1)/2-1) {
                        Point removePoint = leftNode.children.remove(children.size() - 1);
                        point.key = removePoint.getKey();
                        Node lakeNode = (Node) tree.getNode(value);
                        lakeNode.children.add(0, removePoint);
                        return true;
                    }
                }
                if (i < children.size()-1) {
                    Node rightNode = (Node) tree.getNode(children.get(i + 1).getValue());
                    if (rightNode.children.size() > (tree.getNodeOrder()+1)/2-1) {
                        Point removePoint = rightNode.children.remove(0);
                        point.key = rightNode.children.get(0).getKey();
                        Node lakeNode = (Node) tree.getNode(value);
                        lakeNode.children.add(removePoint);
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }

    public void mergeNode(Long value, BplusTree tree) {
        Node targetNode = (Node) tree.getNode(value);
        for (int i = 0; i < children.size(); i++) {
            Point point = children.get(i);
            if (value.equals(point.getValue())) {
                // 左兄弟节点有富余
                if (i > 0) {
                    Node leftNode = (Node) tree.getNode(children.get(i - 1).getValue());
                    leftNode.children.addAll(targetNode.children);
                    return;
                }
                if (i < children.size()-1) {
                    Node rightNode = (Node) tree.getNode(children.get(i + 1).getValue());
                    rightNode.children.addAll(0, targetNode.children);
                    return;
                }
                break;
            }
        }
    }

    public List<Point> getChildren() {
        return children;
    }

    public void setChildren(List<Point> children) {
        this.children = children;
    }
}
