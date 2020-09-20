package com.bit.bplustree;

/**
 * @author aerfafish
 * @date 2020/9/10 4:30 下午
 */

import com.bit.api.model.IndexQuery;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 存在问题 如何通过页号找到页
 */
@Data
public class Node extends AbstractNode {

    /**
     * 子节点
     */
    protected List<Point> children = new ArrayList<Point>();

    public Node() {
    }

    public Node(Long parent) {
        super(parent, false);
    }


    @Override
    public Set<Long> get(Comparable key, BplusTree tree) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).getKey().compareTo(key) >= 0) {
                AbstractNode node = tree.getNode(children.get(i - 1).getValue());
                return node.get(key, tree);
            }
            if (i == children.size() - 1) {
                AbstractNode node = tree.getNode(children.get(i).getValue());
                return node.get(key, tree);
            }
        }
        return Collections.singleton(-1L);
    }

    @Override
    public Set<Long> get(IndexQuery query, BplusTree tree) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).getKey().compareTo(query.getLowKey()) >= 0) {
                AbstractNode node = tree.getNode(children.get(i - 1).getValue());
                return node.get(query.getLowKey(), tree);
            }
            if (i == children.size() - 1) {
                AbstractNode node = tree.getNode(children.get(i).getValue());
                return node.get(query.getLowKey(), tree);
            }
        }
        return Collections.singleton(-1L);
    }

    // 从上往下
    @Override
    public void insert(Point newPoint, BplusTree tree) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).getKey().compareTo(newPoint.getKey()) >= 0) {
                AbstractNode node = tree.getNode(children.get(i - 1).getValue());
                node.insert(newPoint, tree);
                return;
            }
        }
        tree.getNode(children.get(children.size() - 1).getValue()).insert(newPoint, tree);
    }

    @Override
    public void delete(Point deletePoint, BplusTree tree) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).getKey().compareTo(deletePoint.getKey()) >= 0) {
                tree.getNode(children.get(i - 1).getValue()).delete(deletePoint, tree);
                return;
            }
        }
        tree.getNode(children.get(children.size() - 1).getValue()).delete(deletePoint, tree);
    }

    // 从下往上
    public void addPoint(Long originValue, Point newPoint, BplusTree tree) {
        if (children.size() == 0) {
            children.add(newPoint);
        } else {
            for (int i = 0; i < children.size(); i++) {
                Point point = children.get(i);
                if (point.getValue().equals(originValue)) {
                    children.add(i+1, newPoint);
                    break;
                }
            }
        }
        // 分裂
        if (children.size() > tree.getNodeOrder()) {
            //如果是根节点
            Point middlePoint = children.get(children.size() / 2);
            if (parent.equals(-1L)) {
                Long rootNode = tree.newNode(-1L);
                Node root = (Node) tree.getNode(rootNode);
                tree.root = root;
                Long rightNodeNum = tree.newNode(rootNode);
                Node rightNode = (Node) tree.getNode(rightNodeNum);
                parent = rootNode;
                List<Point> subChildren = new ArrayList<>();
                subChildren.addAll(children.subList(0, children.size() / 2));
                List<Point> subRightChildren = new ArrayList<>();
                subRightChildren.addAll(children.subList(children.size() / 2, children.size()));
                for (Point point : subRightChildren) {
                    tree.getNode(point.getValue()).parent = rightNodeNum;
                }
                children = subChildren;
                rightNode.setChildren(subRightChildren);
                root.addPoint(tree.getNum(this), new Point(-1L, tree.getNum(this)), tree);
                root.addPoint(tree.getNum(this), new Point(middlePoint.getKey(), rightNodeNum), tree);
                tree.updateToFile(rightNodeNum);
            } else {
                Long nodeNum = tree.newNode(parent);
                Node newNode = (Node) tree.getNode(nodeNum);
                List<Point> subChildren = new ArrayList<>();
                subChildren.addAll(children.subList(0, children.size() / 2));
                List<Point> subNewChildren = new ArrayList<>();
                subNewChildren.addAll(children.subList(children.size() / 2, children.size()));
                children = subChildren;
                for (Point point : subNewChildren) {
                    tree.getNode(point.getValue()).parent = nodeNum;
                }
                newNode.setChildren(subNewChildren);
                ((Node) tree.getNode(parent)).addPoint(tree.getNum(this), new Point(middlePoint.getKey(), nodeNum), tree);
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
        if (parent.equals(-1L)) {
            if (children.size() == 1) {
                AbstractNode node = tree.getNode(children.get(0).getValue());
                node.parent = -1L;
                tree.root = node;
            }
            return;
        }
        Node parentNode = (Node) tree.getNode(parent);
        if (!parentNode.getExtraNode(tree.getNum(this), tree)) {
            parentNode.mergeNode(tree.getNum(this), tree);
            parentNode.deletePoint(tree.getNum(this), tree);
        }
        tree.updateToFile(tree.getNum(this));
    }

    public void updatePoint(Comparable key, Long value, Long newValue, BplusTree tree) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).getKey().compareTo(key) >= 0) {
                AbstractNode node = tree.getNode(children.get(i - 1).getValue());
                node.updatePoint(key, value, newValue, tree);
                return;
            }
            if (i == children.size() - 1) {
                AbstractNode node = tree.getNode(children.get(i).getValue());
                node.updatePoint(key, value, newValue, tree);
                return;
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
                    if (leftNode.children.size() > (tree.getNodeOrder() + 1) / 2 - 1) {
                        Point removePoint = leftNode.children.remove(children.size() - 1);
                        point.key = removePoint.getKey();
                        Node lakeNode = (Node) tree.getNode(value);
                        lakeNode.children.add(0, removePoint);
                        tree.updateToFile(children.get(i - 1).getValue());
                        return true;
                    }
                }
                if (i < children.size() - 1) {
                    Node rightNode = (Node) tree.getNode(children.get(i + 1).getValue());
                    if (rightNode.children.size() > (tree.getNodeOrder() + 1) / 2 - 1) {
                        Point removePoint = rightNode.children.remove(0);
                        point.key = rightNode.children.get(0).getKey();
                        Node lakeNode = (Node) tree.getNode(value);
                        lakeNode.children.add(removePoint);
                        tree.updateToFile(children.get(i - 1).getValue());
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
                if (i > 0) {
                    Node leftNode = (Node) tree.getNode(children.get(i - 1).getValue());
                    leftNode.children.addAll(targetNode.children);
                    tree.updateToFile(children.get(i - 1).getValue());
                    return;
                }
                if (i < children.size() - 1) {
                    Node rightNode = (Node) tree.getNode(children.get(i + 1).getValue());
                    rightNode.children.addAll(0, targetNode.children);
                    tree.updateToFile(children.get(i + 1).getValue());
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
