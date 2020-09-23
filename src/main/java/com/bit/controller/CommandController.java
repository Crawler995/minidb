package com.bit.controller;

import com.bit.api.manager.DatabaseManager;
import com.bit.exception.SqlErrorException;
import com.bit.handler.CommandHandler;
import com.bit.handler.HandlerResult;
import com.bit.response.CurrentDatabaseResponse;
import com.bit.response.RunSqlResponse;
import com.bit.response.Error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/19 1:11 下午
 */

@RestController
@RequestMapping(value = "/api", method = {RequestMethod.GET, RequestMethod.POST})
public class CommandController {

    @Autowired
    DatabaseManager databaseManager;

    @Autowired
    CommandHandler commandHandler;

    @RequestMapping("/runsql")
    public RunSqlResponse execCommand(@RequestBody Map params) {
        String command = (String) params.get("command");
        RunSqlResponse runSqlResponse = new RunSqlResponse();
//        java.net.URLDecoder.decode(command, StandardCharsets.UTF_8.name());
        long startTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        try {
            HandlerResult handlerResult = commandHandler.handle(command);
            runSqlResponse.setColumns(handlerResult.getColumns());
            runSqlResponse.setCurDatabase(handlerResult.getCurDatabase());
            runSqlResponse.setData(handlerResult.getData());
            runSqlResponse.setStatus(true);
        } catch (SqlErrorException sqlErrorException) {
            Error error = new Error();
            error.setRow(sqlErrorException.getRow());
            error.setColumn(sqlErrorException.getColumn());
            error.setText(sqlErrorException.getText());
            runSqlResponse.setError(error);
            runSqlResponse.setStatus(false);
            runSqlResponse.setMessage("row " + error.getRow() + ", column " + error.getColumn() + ", " + error.getText());
        } catch (Exception e) {
            runSqlResponse.setMessage(e.getMessage() == null ? "" : e.getMessage());
            runSqlResponse.setStatus(false);
            e.printStackTrace();
        }
        runSqlResponse.setTime(sdf.format(date));
        long endTime = System.currentTimeMillis();
        runSqlResponse.setTotalTime((endTime - startTime) + "");
        return runSqlResponse;
    }

    @RequestMapping("/curdb")
    public CurrentDatabaseResponse getCurrentDatabase() {
        CurrentDatabaseResponse response = new CurrentDatabaseResponse();
        response.setRes("bit");
        response.setStatus(true);
        return response;
    }

}
