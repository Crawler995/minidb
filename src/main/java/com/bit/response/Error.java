package com.bit.response;

import lombok.Data;

/**
 * @author aerfafish
 * @date 2020/9/23 3:43 下午
 */
@Data
public class Error {

    int row;

    int column;

    String text = "";

    String type = "error";
}
