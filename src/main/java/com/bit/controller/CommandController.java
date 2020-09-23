package com.bit.controller;

import com.bit.api.ApiManager;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/19 1:11 下午
 */

@RestController
@RequestMapping(value = "/api", method = {RequestMethod.GET, RequestMethod.POST})
public class CommandController {

    @Autowired
    ApiManager apiManager;

    @Autowired
    CommandHandler commandHandler;

    @RequestMapping("/runsql")
    public List<RunSqlResponse> execCommand(@RequestBody Map params) {
        String command = (String) params.get("command");
        List<RunSqlResponse> runSqlResponseList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        List<HandlerResult> handlerResultList;
        long startTime = System.currentTimeMillis();
        try {
            handlerResultList = commandHandler.handle(command);
        } catch (SqlErrorException sqlErrorException) {
            Error error = new Error();
            error.setRow(sqlErrorException.getRow());
            error.setColumn(sqlErrorException.getColumn());
            error.setText(sqlErrorException.getText());
            RunSqlResponse runSqlResponse = new RunSqlResponse();
            runSqlResponse.setTotalTime(System.currentTimeMillis()-startTime+"");
            runSqlResponse.setError(error);
            runSqlResponse.setTime(sdf.format(date));
            runSqlResponse.setStatus(false);
            runSqlResponse.setMessage("row " + error.getRow() + ", column " + error.getColumn() + ", " + error.getText());
            runSqlResponseList.add(runSqlResponse);
            return runSqlResponseList;
        } catch (Exception e) {
            RunSqlResponse runSqlResponse = new RunSqlResponse();
            runSqlResponse.setTime(sdf.format(date));
            runSqlResponse.setTotalTime(System.currentTimeMillis()-startTime+"");
            runSqlResponse.setMessage(e.getMessage() == null ? "" : e.getMessage());
            runSqlResponse.setStatus(false);
            e.printStackTrace();
            runSqlResponseList.add(runSqlResponse);
            return runSqlResponseList;
        }
        for (HandlerResult handlerResult : handlerResultList) {
            RunSqlResponse runSqlResponse = new RunSqlResponse();
            runSqlResponse.setColumns(handlerResult.getColumns());
            runSqlResponse.setData(handlerResult.getData());
            runSqlResponse.setStatus(true);
            runSqlResponse.setTime(sdf.format(date));
            runSqlResponse.setTotalTime(handlerResult.getTotalTime()+"");
            try {
                runSqlResponse.setCurDatabase(apiManager.getCurrentDatabase());
            } catch (Exception e) {
                runSqlResponse.setCurDatabase("");
            }
            runSqlResponseList.add(runSqlResponse);
        }

        return runSqlResponseList;
    }

    @RequestMapping("/curdb")
    public CurrentDatabaseResponse getCurrentDatabase() {
        CurrentDatabaseResponse response = new CurrentDatabaseResponse();
        try {
            response.setRes(apiManager.getCurrentDatabase());
        } catch (Exception e) {
            response.setRes("");
            e.printStackTrace();
        }
        response.setStatus(true);
        return response;
    }

}
