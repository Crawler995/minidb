package com.bit.handler;

/**
 * @author aerfafish
 * @date 2020/9/7 3:38 下午
 */
public class CommandHandler {
    private CommandHandler() {}

    private static CommandHandler commandHandler = null;

    public static CommandHandler getInstance() {
        if (commandHandler == null) {
            synchronized (CommandHandler.class) {
                if(commandHandler == null) {
                    commandHandler = new CommandHandler();
                }
            }
        }
        return commandHandler;
    }

    public void handle(String command) {
        System.out.println("处理命令：" + command);
        if ("exit".equals(command)) {
            System.exit(0);
        }

        /*
         * 增加其他处理
         */
        
    }
}
