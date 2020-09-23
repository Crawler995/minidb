package com.bit.handler;

import java.util.ArrayList;
import java.util.List;

public class SubCommandGroup {
    List<SubCommandOfWhere> group = new ArrayList<>();

    public List<SubCommandOfWhere> getGroup() {
        return group;
    }

    public void setGroup(List<SubCommandOfWhere> group) {
        this.group = group;
    }

    public void add(SubCommandOfWhere subCommandOfWhere){
        group.add(subCommandOfWhere);
    }
}
