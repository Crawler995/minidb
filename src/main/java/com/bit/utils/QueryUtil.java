package com.bit.utils;

import com.bit.api.model.Criteria;
import com.bit.api.model.IndexQuery;

import java.util.Map;

/**
 * @author aerfafish
 * @date 2020/9/20 12:33 下午
 */
public class QueryUtil {

    public static IndexQuery getKey(Criteria criteria) {
        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setCriteria(criteria);
        if (criteria == null) {
            return indexQuery;
        }
        Comparable low = null;
        boolean isEq = false;
        if (criteria.getIsValue() != Criteria.NOT_SET) {
            indexQuery.setEq(true);
            if (criteria.getIsValue() instanceof String) {
                low = (Comparable) criteria.getIsValue();
                indexQuery.setLowKey(low);
            }
            return indexQuery;
        }
//        for (Map.Entry<String, Comparable> entry : criteria.getCriteria().entrySet()) {
//            if (entry.getKey().equals("$gt")) {
//                low = entry.getValue();
//                isEq = false;
//            }
//            if (entry.getKey().equals("$gte")) {
//                low = entry.getValue();
//                isEq = true;
//            }
//            if (entry.getKey().equals("$lt") && low != null) {
//                if (entry.getValue().compareTo(low) <= 0) {
//                    low = null;
//                }
//            }
//            if (entry.getKey().equals("$lte") && low != null) {
//                if (entry.getValue().compareTo(low) < 0) {
//                    low = null;
//                }
//                else if (entry.getValue().compareTo(low) == 0 && !isEq) {
//                    low = null;
//                }
//            }
//        }
        indexQuery.setLowKey(low);
        indexQuery.setEq(isEq);
        return indexQuery;
    }
}
