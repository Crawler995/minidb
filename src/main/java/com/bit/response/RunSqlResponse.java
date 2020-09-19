package com.bit.response;

import lombok.Data;

import java.util.List;

/**
 * @author aerfafish
 * @date 2020/9/19 1:22 下午
 */

@Data
public class RunSqlResponse {

    Boolean status;

    String message;

    String totalTime;

    String time;

    String curDatabase;

    List<String> columns;

    List<Object> data;

}
