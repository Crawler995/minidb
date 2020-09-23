package com.bit.handler;

import com.bit.api.ApiManager;
import com.bit.api.model.Criteria;
import com.bit.api.model.Query;
import com.bit.model.TableData;
import com.sun.javafx.collections.SortableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectManager {
    ApiManager apiManager;

    SelectManager(ApiManager apiManager){
        this.apiManager = apiManager;
    }

    public Query getQuery(List<SubCommandOfWhere> commands) throws Exception {
        Query query = new Query();
        for(SubCommandOfWhere command : commands){
            String columnName = command.getColumnNameLeft().getColumnName();
            String value = command.getValueFirst();
            String operate = command.getOperation();
            if (operate.equals(">")) {
                query.addCriteria(Criteria.where(columnName).gte(value));
            }
            if (operate.equals(">=")) {
                query.addCriteria(Criteria.where(columnName).lte(value));
            }
            if (operate.equals("=")) {
                query.addCriteria(Criteria.where(columnName).is(value));
            }
            if (operate.equals("<")) {
                query.addCriteria(Criteria.where(columnName).lt(value));
            }
            if (operate.equals("<=")) {
                query.addCriteria(Criteria.where(columnName).lte(value));
            }
            if (operate.equals("!=")) {
                query.addCriteria(Criteria.where(columnName).ne(value));
            }
        }
        return query;
    }

    public List<TableData> Single(String tableName,List<SubCommandOfWhere> conditions){
        return null;
    }

    public List<TableData> Join(String left, String right, List<SubCommandOfWhere> conditions, TableName.JoinType joinType) throws Exception {
        List<String> leftTableColumn = apiManager.getTableColumns(left);
        List<String> rightTableColumn = apiManager.getTableColumns(right);
        List<SubCommandGroup> subCommandGroups = new ArrayList<>();

        /* can't deal with quote priority*/
        /* no xor*/
        for(int i = 0; i < conditions.size(); i++){
            SubCommandGroup subCommandGroup = new SubCommandGroup();
            while(conditions.get(i).getLogicalOperation().equals("AND") && i < conditions.size()){
                subCommandGroup.add(conditions.get(i++));
                if(!conditions.get(i).getOperation().equals("AND")){
                    subCommandGroup.add(conditions.get(i));
                    break;
                }
            }
            subCommandGroups.add(subCommandGroup);
        }

        /* now subCommandGroup is ALL AND */
        List<TableData> lastReturn = new ArrayList<>();
        for(SubCommandGroup subCommandGroup : subCommandGroups) {
            SubCommandGroup leftGroup = new SubCommandGroup();
            SubCommandGroup rightGroup = new SubCommandGroup();
            List<SubCommandOfWhere> commands = subCommandGroup.getGroup();

            // 将直接查询和连接查询分开
            for (SubCommandOfWhere command : commands) {
                if (!command.getRightIsColumn()) {
                    String tableName = command.getColumnNameLeft().getTableName();
                    String columnName = command.getColumnNameLeft().getColumnName();
                    if ((tableName == null || tableName.equals(left)) && leftTableColumn.contains(columnName)) {
                        leftGroup.add(command);
                    } else if ((tableName == null || tableName.equals(right)) && rightTableColumn.contains(columnName)) {
                        rightGroup.add(command);
                    } else {
                        throw new Exception("Wrong Join Condition.");
                    }
                    commands.remove(command);
                }
            }
            //直接查询
            List<TableData> leftTable = apiManager.selectData(getQuery(leftGroup.getGroup()), left);
            List<TableData> rightTable = apiManager.selectData(getQuery(rightGroup.getGroup()), left);
            List<TableData> target = new ArrayList<>();
            //连接操作
            if(commands.size() == 0){
                target.addAll(leftTable);
                target.addAll(rightTable);
            }
            else {
                for (SubCommandOfWhere command : commands) {
                    String operate = command.getOperation();
                    switch (operate) {
                        case "=":
                            String tableName1 = command.getColumnNameLeft().getTableName();
                            String columnName1 = command.getColumnNameLeft().getColumnName();
                            String tableName2 = command.getColumnNameRight().getTableName();
                            String columnName2 = command.getColumnNameRight().getColumnName();
                            String leftColumnName = null;
                            String rightColumnName = null;
                            if (columnName1.equals(columnName2)) {
                                leftColumnName = rightColumnName = columnName1;
                            } else {
                                if ((tableName1 == null || tableName1.equals(left)) && leftTableColumn.contains(columnName1)) {
                                    leftColumnName = columnName1;
                                } else if ((tableName1 == null || tableName1.equals(right)) && rightTableColumn.contains(columnName1)) {
                                    rightColumnName = columnName1;
                                } else {
                                    throw new Exception("Wrong Join Condition.");
                                }
                                if (leftColumnName != null) {
                                    rightColumnName = columnName2;
                                } else {
                                    leftColumnName = columnName2;
                                }
                            }

                            switch (joinType){
                                case innerJoin:
                                    for (TableData data : leftTable) {
                                        for (TableData tableData : rightTable) {
                                            boolean flag = false;
                                            if (data.getData().get(leftColumnName) == tableData.getData().get(rightColumnName)) {
                                                flag = true;
                                                target.add(tableData);
                                            }
                                            if (flag) {
                                                target.add(data);
                                            }
                                        }
                                    }
                                    break;
                                case leftJoin:
                                    target.addAll(SpecificJoin(leftTable,leftColumnName,rightTable,rightColumnName));
                                    break;
                                case rightJoin:
                                    target.addAll(SpecificJoin(rightTable,rightColumnName,leftTable,leftColumnName));
                                    break;
                                default:
                                    throw new Exception("Not supported this join type yet");
                            }
                        default:
                            throw new Exception("Not Supported yet");
                    }
                }
            }
            lastReturn.addAll(target);
        }
        return lastReturn;
    }

    public List<TableData> SpecificJoin(List<TableData> leftTable, String leftColumnName, List<TableData> rightTable,String rightColumnName){
        List<TableData> target = new ArrayList<>();
        for (TableData data : leftTable) {
            for (TableData tableData : rightTable) {
                if (data.getData().get(leftColumnName) == tableData.getData().get(rightColumnName)) {
                    target.add(tableData);
                }
            }
        }
        target.addAll(leftTable);
        return target;
    }
}
