package com.bit.api.model;

import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/19 5:38 下午
 */
public class Update {

    Map<String, Object> modifyData = new HashMap<>();

    public void set(@Nullable String key, @Nullable Object value) {
        modifyData.put(key, value);
    }
}
