package com.bit.api.model;

import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/19 5:38 下午
 */
public class Query {
    private final Map<String, Criteria> criteria = new LinkedHashMap();

    public Query addCriteria(Criteria criteria) throws Exception {
        Assert.notNull(criteria, "Criteria不能为空!");
        Criteria existing = (Criteria)this.criteria.get(criteria.getKey());
        String key = criteria.getKey();
        if (existing == null) {
            this.criteria.put(key, criteria);
            return this;
        } else {
            throw new Exception(String.format("不能添加同一个key '%s' 的Criteria两次", key));
        }
    }

    public Map<String, Criteria> getCriteria() {
        return criteria;
    }

}
