package com.bit;

import com.bit.handler.CommandHandler;

import java.util.Scanner;

/**
 * @author aerfafish
 * @date 2020/9/7 3:27 下午
 */
public class Client {
    public static void main(String[] args) {
        CommandHandler commandHandler = new CommandHandler();
        while (true) {
            System.out.print("db > ");
            Scanner scanner = new Scanner(System.in);
            //输入命令
            String command = scanner.nextLine();
            /**
             * Deal command
             * 处理输入命令，调用指定接口
             */
            commandHandler.handle(command);
        }
    }
}
