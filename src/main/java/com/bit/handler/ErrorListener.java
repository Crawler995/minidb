package com.bit.handler;

import com.bit.exception.SqlErrorException;
import lombok.SneakyThrows;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.Collections;
import java.util.List;

/**
 * @author aerfafish
 * @date 2020/9/23 1:56 下午
 */
public class ErrorListener extends BaseErrorListener {

    @SneakyThrows
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        List<String> invocationStack = ((Parser) recognizer).getRuleInvocationStack();
        Collections.reverse(invocationStack);
        System.err.println("[语法错误] 规则栈: " + invocationStack);
        System.err.println("行" + line + "列" + charPositionInLine + "非法符号: " + msg + ". 原始原因:" + e);
        throw new SqlErrorException(line, charPositionInLine, msg);
    }

}
