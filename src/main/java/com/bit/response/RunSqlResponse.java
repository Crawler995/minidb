package com.bit.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author aerfafish
 * @date 2020/9/19 1:22 下午
 */

@Data
public class RunSqlResponse {

    Boolean status = false;

    String message = "";

    String totalTime = "";

    String time = "";

    String curDatabase = "";

    Error error = null;

    List<String> columns = new ArrayList<>();

    List<Object> data = new ArrayList<>();
<<<<<<< HEAD
=======

>>>>>>> e6f70ebd947b55eee657a861be9860c12eead655
}
