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
