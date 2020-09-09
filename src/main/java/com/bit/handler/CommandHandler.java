package com.bit.handler;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

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

        /**
         * 增加其他处理
         */
        CharStream stream = CharStreams.fromString(command.toUpperCase());
        MySqlLexer lexer = new MySqlLexer(stream);
        CommonTokenStream token = new CommonTokenStream(lexer);
        MySqlParser parser = new MySqlParser(token);
        ParseTree tree = parser.root();

        //MySqlParserVisitor mySqlParserVisitor = new MySqlParserBaseVisitor();
        //mySqlParserVisitor.visit(tree);

    }
}
