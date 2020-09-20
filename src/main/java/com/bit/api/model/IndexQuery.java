package com.bit.api.model;

import com.bit.api.model.Criteria;
import lombok.Data;

import java.util.regex.Pattern;

/**
 * @author aerfafish
 * @date 2020/9/20 11:01 上午
 */

@Data
public class IndexQuery {
    private Criteria criteria;

    private Comparable lowKey;

    private boolean isEq;
}
