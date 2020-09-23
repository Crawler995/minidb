package com.bit.handler;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aerfafish
 * @date 2020/9/19 1:30 下午
 */
@Data
public class HandlerResult {
    String curDatabase = "";

    List<String> columns = new ArrayList<>();

    List<Object> data =  new ArrayList<>();

    long totalTime;
}
