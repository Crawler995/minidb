package com.bit.handler;

import com.bit.api.ApiManager;
import com.bit.api.model.Criteria;
import com.bit.api.model.Query;
import com.bit.model.TableData;
import com.sun.javafx.collections.SortableList;

import java.util.*;

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
            if(operate.equals("LIKE")){
                query.addCriteria(Criteria.where(columnName).regex(value));
            }
        }
        return query;
    }

    public HandlerResult getResult(List<TableData> tableDatas, CommandContent content) throws Exception {
        if(tableDatas == null){
        }

        HandlerResult handlerResult = new HandlerResult();
        List<String> columnNames = new ArrayList<>();
        List<Object> data = new ArrayList<>();

        for(ColumnName name : content.getColumnNames()){
            if(name.getColumnName().equals("*")){
                columnNames.clear();
                columnNames.addAll(tableDatas.get(0).getData().keySet());
                break;
            }
            columnNames.add(name.getColumnName());
        }
        for (TableData tableData : tableDatas) {
            Map<String,Comparable> tempData = new HashMap<>();
            for(String name : columnNames){
                tempData.put(name,tableData.getData().get(name));
            }
            data.add(tempData);
        }
        handlerResult.setData(data);
        handlerResult.setColumns(columnNames);

        return handlerResult;
    }

    public List<TableData> single(String tableName,List<SubCommandOfWhere> conditions) throws Exception {
        List<String> columnName = apiManager.getTableColumns(tableName);
        for(SubCommandOfWhere command : conditions){
            if(command.getColumnNameLeft().getTableName() != null && !command.getColumnNameLeft().getTableName().equals(tableName)){
                throw new Exception("Unknown table " + tableName +  " in field list\n");

            }

            if(!columnName.contains(command.getColumnNameLeft().getColumnName())){
                throw new Exception("Unknown column " + command.getColumnNameLeft().getColumnName() +  " in field list\n");
            }
        }
        Query  query = getQuery(conditions);
        return apiManager.selectData(query,tableName);

    }

    public List<TableData> join(String left, String right, List<SubCommandOfWhere> conditions, TableName.JoinType joinType) throws Exception {
        List<String> leftTableColumn = apiManager.getTableColumns(left);
        List<String> rightTableColumn = apiManager.getTableColumns(right);
        List<SubCommandGroup> subCommandGroups = new ArrayList<>();

        /* can't deal with quote priority*/
        /* no xor*/
         for(int i = 0; i < conditions.size(); i++){
            SubCommandGroup subCommandGroup = new SubCommandGroup();
            if(conditions.get(i).getLogicalOperation() != null) {
                while (conditions.get(i).getLogicalOperation() != null && conditions.get(i).getLogicalOperation().equals("AND") && i < conditions.size()) {
                    subCommandGroup.add(conditions.get(i++));
                    if (i < conditions.size() && !"AND".equals(conditions.get(i).getLogicalOperation())) {
                        subCommandGroup.add(conditions.get(i));
                        break;
                    }
                }
            }
            else{
                subCommandGroup.add(conditions.get(i));
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
            Iterator<SubCommandOfWhere> iterator = commands.iterator();
            while (iterator.hasNext()) {
                SubCommandOfWhere command = iterator.next();
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
                    iterator.remove();
                }
            }
            //直接查询
            List<TableData> leftTable = apiManager.selectData(getQuery(leftGroup.getGroup()), left);
            List<TableData> rightTable = apiManager.selectData(getQuery(rightGroup.getGroup()), right);
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
                                    boolean flag = false;
                                    for (TableData leftData : leftTable) {
                                        for (TableData rightData : rightTable) {
                                            if (leftData.getData().get(leftColumnName).compareTo(rightData.getData().get(rightColumnName)) == 0) {
                                                flag = true;
                                                Map<String,Comparable> temp = new HashMap<>();
                                                for(String leftName : leftTableColumn){
                                                    temp.put(leftName,leftData.getData().get(leftName));
                                                }
                                                for(String rightName : rightTableColumn){
                                                    temp.put(rightName,rightData.getData().get(rightName));
                                                }
                                                TableData data = new TableData();
                                                data.setData(temp);
                                                target.add(data);
                                            }
                                        }
                                    }
                                    if(!flag){
                                        Map<String,Comparable> temp = new HashMap<>();
                                        for(String leftName : leftTableColumn){
                                            temp.put(leftName,null);
                                        }
                                        for(String rightName : rightTableColumn){
                                            temp.put(rightName,null);
                                        }
                                        TableData data = new TableData();
                                        data.setData(temp);
                                        target.add(data);
                                    }
                                    break;
                                case leftJoin:
                                    target.addAll(specificJoin(leftTable,leftColumnName,rightTable,rightColumnName,leftTableColumn,rightTableColumn));
                                    break;
                                case rightJoin:
                                    target.addAll(specificJoin(rightTable,rightColumnName,leftTable,leftColumnName,leftTableColumn,rightTableColumn));
                                    break;
                                default:
                                    throw new Exception("Not supported this join type yet");
                            }
                            break;
                        default:
                            throw new Exception("Not Supported yet");
                    }
                }
            }
            lastReturn.addAll(target);
        }
        return lastReturn;
    }

    public List<TableData> specificJoin(List<TableData> leftTable, String leftColumnName, List<TableData> rightTable,String rightColumnName,List<String> leftTableColumn,List<String> rightTableColumn){
        List<TableData> target = new ArrayList<>();
        for (TableData leftData : leftTable) {
            boolean flag = false;
            Map<String,Comparable> temp = new HashMap<>();
            for(String leftName : leftTableColumn){
                temp.put(leftName,leftData.getData().get(leftName));
            }
            for (TableData rightData : rightTable) {
                if (leftData.getData().get(leftColumnName).compareTo(rightData.getData().get(rightColumnName)) == 0) {
                    flag = true;
                    for(String rightName : rightTableColumn){
                        temp.put(rightName,rightData.getData().get(rightName));
                    }
                }
            }
            if(!flag){
                for(String rightName : rightTableColumn){
                    temp.put(rightName,null);
                }
            }
            TableData data = new TableData();
            data.setData(temp);
            target.add(data);
        }
        return target;
    }
}
