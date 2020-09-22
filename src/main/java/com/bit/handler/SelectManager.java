package com.bit.handler;

import com.bit.model.TableData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectManager {
    public TableData InnerJoin(String left, String right, List<SubCommandOfWhere> conditions){
        TableData leftTable;
        TableData rightTable;
        for(SubCommandOfWhere condition : conditions){
            if(!condition.getRightIsColumn()){
                
            }
        }
    }
}
