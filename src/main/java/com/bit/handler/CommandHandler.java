package com.bit.handler;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

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


        List<CommandContent> commandContents = new ArrayList<>();

        CharStream stream = CharStreams.fromString(command.toUpperCase()); // 将命令读入字节符号流
        MySqlLexer lexer = new MySqlLexer(stream);//新建一个词法分析器，处理输入的字节流
        CommonTokenStream token = new CommonTokenStream(lexer);//新建一个词法符号的缓冲区，用于存储词法分析器将生成的词法符号
        MySqlParser parser = new MySqlParser(token);//新建一个语法分析器，处理词法符号缓冲区的内容
        ParseTree tree = parser.root();//针对root规则，开始语法分析，生成一颗树

        SqlCommandVisitor sqlCommandVisitor = new SqlCommandVisitor(commandContents);
        sqlCommandVisitor.visit(tree);

        CommandContent content =  commandContents.get(0);
        System.out.println("operation:" + content.getOperation().name() +"\nColumnName:" + content.getColumnNames().get(0).getColumnName() + "\nTableName:" + content.getTableNames().get(0).getTableName());

        /**
         * analyse commandContents here
         */
    }
}
