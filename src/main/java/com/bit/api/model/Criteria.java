package com.bit.api.model;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author aerfafish
 * @date 2020/9/19 7:14 下午
 */
public class Criteria {
    public static final Object NOT_SET = new Object();

    private static final int[] FLAG_LOOKUP = new int['\uffff'];

    @Nullable
    private String key;

    private List<Criteria> criteriaChain;

    private LinkedHashMap<String, Comparable> criteria = new LinkedHashMap();

    public LinkedHashMap<String, Comparable> getCriteria() {
        return criteria;
    }

    @Nullable
    private Object isValue;

    public Criteria() {
        this.isValue = NOT_SET;
        this.criteriaChain = new ArrayList();
    }

    public Criteria(String key) {
        this.criteriaChain = new ArrayList();
        this.isValue = NOT_SET;
        this.key = key;
    }

    protected Criteria(List<Criteria> criteriaChain, String key) {
        this.criteriaChain = criteriaChain;
        this.criteriaChain.add(this);
        this.isValue = NOT_SET;
        this.key = key;
    }

    public static Criteria where(String key) {
        return new Criteria(key);
    }

    public Criteria is(@Nullable Object value) throws Exception {
        if (!this.isValue.equals(NOT_SET)) {
            throw new Exception("Multiple 'is' values declared. You need to use 'and' with multiple criteria");
        } else if (this.lastOperatorWasNot()) {
            throw new Exception("Invalid query: 'not' can't be used with 'is' - use 'ne' instead.");
        } else {
            this.isValue = value;
            return this;
        }
    }

    public Criteria ne(@Nullable Comparable value) {
        this.criteria.put("$ne", value);
        return this;
    }

    public Criteria lt(Comparable value) {
        this.criteria.put("$lt", value);
        return this;
    }

    public Criteria lte(Comparable value) {
        this.criteria.put("$lte", value);
        return this;
    }

    public Criteria gt(Comparable value) {
        this.criteria.put("$gt", value);
        return this;
    }

    public Criteria gte(Comparable value) {
        this.criteria.put("$gte", value);
        return this;
    }

    private boolean lastOperatorWasNot() {
        return !this.criteria.isEmpty() && "$not".equals(this.criteria.keySet().toArray()[this.criteria.size() - 1]);
    }


    public Criteria exists(boolean value) {
        this.criteria.put("$exists", value);
        return this;
    }


    public Criteria not() {
        return this.not(null);
    }

    private Criteria not(@Nullable Comparable value) {
        this.criteria.put("$not", value);
        return this;
    }

    public Criteria regex(String regex) throws Exception {
//        return this.regex(regex, null);
        Pattern pattern = Pattern.compile(regex);
        if (!this.isValue.equals(NOT_SET)) {
            throw new Exception("Multiple 'is' values declared. You need to use 'and' with multiple criteria");
        } else if (this.lastOperatorWasNot()) {
            throw new Exception("Invalid query: 'not' can't be used with 'is' - use 'ne' instead.");
        } else {
            this.isValue = pattern;
            return this;
        }
    }

    public Criteria regex(String regex, @Nullable String options) {
        return this.regex(this.toPattern(regex, options));
    }

    public Criteria regex(Pattern pattern) {
        Assert.notNull(pattern, "Pattern must not be null!");
        if (this.lastOperatorWasNot()) {
            return this.not(pattern.pattern());
        } else {
            this.isValue = pattern;
            return this;
        }
    }

    public Criteria orOperator(Criteria... criterias) throws Exception {
        List<Criteria> criteriaList = new LinkedList<>(Arrays.asList(criterias));
        return this.registerCriteriaChainElement((new Criteria("$or")).is(criteriaList));
    }

    public Criteria norOperator(Criteria... criterias) throws Exception {
        List<Criteria> criteriaList = new LinkedList<>(Arrays.asList(criterias));
        return this.registerCriteriaChainElement((new Criteria("$nor")).is(criteriaList));
    }

    public Criteria andOperator(Criteria... criterias) throws Exception {
        List<Criteria> criteriaList = new LinkedList<>(Arrays.asList(criterias));
        return this.registerCriteriaChainElement((new Criteria("$and")).is(criteriaList));
    }

    private Criteria registerCriteriaChainElement(Criteria criteria) {
        if (this.lastOperatorWasNot()) {
            throw new IllegalArgumentException("operator $not is not allowed around criteria chain element: " + criteria);
        } else {
            this.criteriaChain.add(criteria);
            return this;
        }
    }

    private Pattern toPattern(String regex, @Nullable String options) {
        Assert.notNull(regex, "Regex Object must not be null!");
        return Pattern.compile(regex, regexFlags(options));
    }

    private static int regexFlags(@Nullable String s) {
        int flags = 0;
        if (s == null) {
            return flags;
        } else {
            char[] var2 = s.toLowerCase().toCharArray();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                char f = var2[var4];
                flags |= regexFlag(f);
            }

            return flags;
        }
    }

    private static int regexFlag(char c) {
        int flag = FLAG_LOOKUP[c];
        if (flag == 0) {
            throw new IllegalArgumentException(String.format("Unrecognized flag [%c]", c));
        } else {
            return flag;
        }
    }

    public String getKey() {
        return key;
    }

    @Nullable
    public Object getIsValue() {
        return isValue;
    }

    public void setIsValue(@Nullable Object isValue) {
        this.isValue = isValue;
    }

    public static void main(String[] args) throws Exception {
        Criteria criteria = Criteria.where("id").lt(3).orOperator(Criteria.where("name").is("yhz")).andOperator(Criteria.where("age").not(20));
        System.out.println(criteria);
    }
}
