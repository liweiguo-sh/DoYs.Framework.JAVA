/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-08-08
 * 打印任务服务类
 *****************************************************************************/
package com.doys.aprint.task;
import com.doys.framework.config.Const;
import com.doys.framework.database.DBFactory;
import com.doys.framework.dts.base.ENTITY_RECORD;
import com.doys.framework.util.UtilDate;
import com.doys.framework.util.UtilString;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskService {
    public static int createTask(DBFactory dbBus, int labelId, String userPk) throws Exception {
        String taskPk;

        ENTITY_RECORD entity;

        // ------------------------------------------------
        taskPk = UtilString.getSN(dbBus, "task_pk", "", "T-{yy}{mm}{dd}-{5}");

        entity = new ENTITY_RECORD(dbBus, "core_task");
        entity
            .setValue("pk", taskPk)
            .setValue("label_id", labelId)
            .setValue("bus_date", UtilDate.getDateStr())
            .setValue("creator", userPk);
        entity.Save();

        return (int) entity.getId();
    }
    public static void deleteTask(DBFactory dbBus, int taskId) throws Exception {
        int labelId;

        String sql;
        SqlRowSet rsTask;
        // ------------------------------------------------
        sql = "SELECT * FROM core_task WHERE id = ?";
        rsTask = dbBus.getRowSet(sql, taskId);
        if (rsTask.next()) {
            labelId = rsTask.getInt("label_id");
        }
        else {
            throw new Exception("任务单 (taks_id = " + taskId + ") 已不存在，请检查。");
        }

        // ------------------------------------------------
        sql = "DELETE FROM x_label_" + labelId + " WHERE task_id = ?";
        dbBus.exec(sql, taskId);

        sql = "DELETE FROM core_task WHERE id = ?";
        dbBus.exec(sql, taskId);
    }
    public static SqlRowSet getTaskData(DBFactory dbBus, int labelId, int taskId, int rowNoFrom, int rowNoTo) throws Exception {
        String sql = "SELECT * FROM x_label_" + labelId + " WHERE task_id = ? AND $row_no >= ? AND $row_no <= ?";
        return dbBus.getRowSet(sql, taskId, rowNoFrom, rowNoTo);
    }

    // -- generate data -------------------------------------------------------
    public static void generatePrintData(DBFactory dbBus, int labelId, int qty, int taskId, ArrayList<HashMap<String, Object>> variables) throws Exception {
        int columnCount, colIndex = 0;

        String sql, sqlInsert, sqlUpdate;
        StringBuilder builder = new StringBuilder();
        StringBuilder builderField, builderValue;

        ArrayList<Object[]> listInsert = new ArrayList<>(qty);
        Object[] paraInsert;
        SqlRowSet rsVariable;

        // -- 1. 预处理 --
        sql = "SELECT COUNT(1) FROM base_label_variable WHERE label_id = ?";
        columnCount = dbBus.getInt(sql, 0, labelId);
        for (int i = 0; i < qty; i++) {
            paraInsert = new Object[columnCount + 2];
            paraInsert[columnCount] = taskId;
            paraInsert[columnCount + 1] = (i + 1);
            listInsert.add(paraInsert);
        }
        builderField = new StringBuilder(columnCount + 1);
        builderValue = new StringBuilder(columnCount + 1);

        // -- 2. 生成数据 --
        sql = "SELECT * FROM base_label_variable WHERE label_id = ? ORDER BY sequence, name";
        rsVariable = dbBus.getRowSet(sql, labelId);
        while (rsVariable.next()) {
            String name = rsVariable.getString("name");
            String type = rsVariable.getString("type");
            String value = rsVariable.getString("value");
            int valueLen = rsVariable.getInt("value_len");

            builderField.append(name).append(", ");
            builderValue.append("?, ");
            // -- 1. 合并客户端变量值 --
            for (HashMap<String, Object> variable : variables) {
                String paraKey = (String) variable.get("name");
                if (paraKey.equalsIgnoreCase(name)) {
                    value = variable.get("value").toString();
                }
            }
            // -- 2. 执行规则 --
            if (type.equalsIgnoreCase("seq")) {
                long valueLong = Long.parseLong(value);
                String valueString;
                for (int i = 0; i < qty; i++) {
                    valueString = String.format("%0" + valueLen + "d", valueLong++);
                    listInsert.get(i)[colIndex] = valueString;
                }

                // -- 3. 更新末次打印值 --
                valueString = String.format("%0" + valueLen + "d", valueLong);
                sqlUpdate = "UPDATE base_label_variable SET value = ? WHERE label_id = ? AND name = ?";
                dbBus.exec(sqlUpdate, valueString, labelId, name);
            }
            else if (type.equalsIgnoreCase("date")) {
                if (value.length() == 10) {
                    value += " 00:00:00";
                }
                String dateFormat = rsVariable.getString("rule_date_format");
                LocalDateTime dateVar = LocalDateTime.parse(value, DateTimeFormatter.ofPattern(Const.datetimeFormat));
                String valueString = dateVar.format(DateTimeFormatter.ofPattern(dateFormat));

                for (int i = 0; i < qty; i++) {
                    listInsert.get(i)[colIndex] = valueString;
                }
            }
            else {
                for (int i = 0; i < qty; i++) {
                    listInsert.get(i)[colIndex] = value;
                }
            }
            colIndex++;
        }

        // -- 9 .批量插入数据 --
        builderField.append("task_id, $row_no");
        builderValue.append("?, ?");

        builder.append("INSERT INTO x_label_" + labelId + " ");
        builder.append("(" + builderField.toString() + ") ");
        builder.append("VALUES (" + builderValue.toString() + ")");
        sqlInsert = builder.toString();
        dbBus.batchUpdate(sqlInsert, listInsert);
    }
}