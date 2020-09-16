package com.bit.tree;

import com.bit.bplustree.BplusTree;
import com.bit.bplustree.Point;

import java.util.Scanner;

/**
 * @author aerfafish
 * @date 2020/9/12 2:13 下午
 */
public class TreeTest {

    public static void main(String[] args) {
        BplusTree tree = null;
        Scanner scanner = new Scanner(System.in);
<<<<<<< HEAD
//        tree = new BplusTree("/tmp/bplus1", 4, 4);
//        tree.insert(new Point(1L, 11L));
//        tree.insert(new Point(2L, 22L));
//        tree.insert(new Point(3L, 33L));
//        tree.insert(new Point(4L, 44L));
//        tree.insert(new Point(5L, 55L));
//        tree.insert(new Point(6L, 66L));
//        tree.insert(new Point(7L, 77L));
//        tree.insert(new Point(8L, 88L));
//        tree.insert(new Point(9L, 99L));
//        tree.insert(new Point(10L, 1010L));
=======
        tree = new BplusTree("/tmp/bplus1", 4, 4);
        tree.insert(new Point(1L, 11L));
        tree.insert(new Point(2L, 22L));
        tree.insert(new Point(3L, 33L));
        tree.insert(new Point(4L, 44L));
        tree.insert(new Point(5L, 55L));
        tree.insert(new Point(6L, 66L));
        tree.insert(new Point(7L, 77L));
        tree.insert(new Point(8L, 88L));
        tree.insert(new Point(9L, 99L));
        tree.insert(new Point(10L, 10L));
        tree.insert(new Point(11L, 11L));
        tree.insert(new Point(12L, 12L));
        tree.insert(new Point(13L, 13L));
>>>>>>> master
//        tree.insert(new Point(5L, 5L));
//        tree.insert(new Point(3L, 3L));
//        tree.insert(new Point(4L, 2L));
//        tree.insert(new Point(2L, 3L));
//        tree.insert(new Point(5L, 5L));
//        tree.insert(new Point(3L, 3L));
//        tree.insert(new Point(4L, 2L));
//        tree.insert(new Point(2L, 3L));
//        tree.insert(new Point(5L, 5L));
//        tree.insert(new Point(3L, 3L));
//        tree.insert(new Point(4L, 2L));
//        tree.insert(new Point(2L, 3L));
//        tree.insert(new Point(5L, 5L));
//        tree.insert(new Point(3L, 3L));
//        tree.insert(new Point(4L, 2L));
//        tree.insert(new Point(2L, 3L));
//        tree.insert(new Point(5L, 5L));
//        tree.insert(new Point(3L, 3L));
//        tree.insert(new Point(4L, 2L));
//        tree.insert(new Point(2L, 3L));
//        tree.insert(new Point(5L, 5L));
//        tree.insert(new Point(3L, 3L));
//        tree.insert(new Point(4L, 2L));
//        tree.insert(new Point(2L, 3L));
//        tree.insert(new Point(5L, 5L));

        while (true) {
            System.out.print("db > ");
            //输入命令
            String command = scanner.nextLine();
            /**
             * Deal command
             * 处理输入命令，调用指定接口
             */
            if (command.equals("new")) {
                System.out.print("db储存位置 > ");
                String filePath = scanner.nextLine();
                tree = new BplusTree(filePath, 4, 4);
            }
            if (command.equals("insert")) {
                System.out.print("key > ");
                Long key = Long.valueOf(scanner.nextLine());
                System.out.print("value > ");
                Long value = Long.valueOf(scanner.nextLine());
                tree.insert(new Point(key, value));
            }
            if (command.equals("get")) {
                System.out.print("key > ");
                Long key = Long.valueOf(scanner.nextLine());
                System.out.println(tree.get(key));
            }
            if (command.equals("delete")) {
                System.out.print("key > ");
                Long key = Long.valueOf(scanner.nextLine());
                System.out.print("value > ");
                Long value = Long.valueOf(scanner.nextLine());
                tree.remove(new Point(key, value));
            }
            if (command.equals("update")) {
                System.out.print("key > ");
                Long key = Long.valueOf(scanner.nextLine());
                System.out.print("originValue > ");
                Long originValue = Long.valueOf(scanner.nextLine());
                System.out.print("newValue > ");
                Long newValue = Long.valueOf(scanner.nextLine());
                tree.update(key, originValue, newValue);
            }
        }
    }
}
