package com.bit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * @author aerfafish
 * @date 2020/9/7 3:27 下午
 */
public class Client {
    public static void main(String[] args) {
        while (true) {
            System.out.print("db > ");
            Scanner scanner = new Scanner(System.in);
            //输入一组单词
            String command = scanner.nextLine();
            /**
             * Deal command
             * 处理输入命令，调用指定接口
             */
            System.out.println(command);
        }
    }
}
