package com.bit.api.model;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/19 5:38 下午
 */

@Data
public class Update {

    Map<String, Comparable> modifyData = new HashMap<>();

    public void set(@Nullable String key, @Nullable Comparable value) {
        modifyData.put(key, value);
    }
}
