package com.doys.aprint.print;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.core.db.DBFactory;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
public class PrintService extends BaseService {
    public static void generatePrintData(DBFactory dbSys, int labelId, int qty, int taskId, ArrayList<LinkedTreeMap<String, Object>> variables) throws Exception {
        int columnCount, colIndex = 0;

        String sql, sqlInsert, sqlUpdate;
        StringBuilder builder = new StringBuilder();
        StringBuilder builderField, builderValue;

        ArrayList<Object[]> listInsert = new ArrayList<>(qty);
        Object[] paraInsert;
        SqlRowSet rsVariable;

        // -- 1. 预处理 --
        sql = "SELECT COUNT(1) FROM ..base_label_variable WHERE label_id = ?";
        columnCount = dbSys.getInt(sql, labelId);
        for (int i = 0; i < qty; i++) {
            paraInsert = new Object[columnCount + 2];
            paraInsert[columnCount] = taskId;
            paraInsert[columnCount + 1] = (i + 1);
            listInsert.add(paraInsert);
        }
        builderField = new StringBuilder(columnCount + 1);
        builderValue = new StringBuilder(columnCount + 1);

        // -- 2. 生成数据 --
        sql = "SELECT * FROM ..base_label_variable WHERE label_id = ? ORDER BY sequence, name";
        rsVariable = dbSys.getRowSet(sql, labelId);
        while (rsVariable.next()) {
            String name = rsVariable.getString("name");
            String type = rsVariable.getString("type");
            String format = rsVariable.getString("format");
            String value = rsVariable.getString("value");
            int valueLen = rsVariable.getInt("value_len");

            builderField.append(name).append(", ");
            builderValue.append("?, ");
            // -- 1. 合并客户端变量值 --
            for (LinkedTreeMap<String, Object> variable : variables) {
                String paraKey = (String) variable.get("name");
                if (paraKey.equalsIgnoreCase(name)) {
                    value = (String) variable.get("value");
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
                sqlUpdate = "UPDATE ..base_label_variable SET value = ? WHERE label_id = ? AND name = ?";
                dbSys.exec(sqlUpdate, valueString, labelId, name);
            }
            else if (type.equalsIgnoreCase("date")) {
                String valueString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
                for (int i = 0; i < qty; i++) {
                    listInsert.get(i)[colIndex] = valueString;
                }

                // -- 3. 更新末次打印值 --
                sqlUpdate = "UPDATE ..base_label_variable SET value = ? WHERE label_id = ? AND name = ?";
                dbSys.exec(sqlUpdate, valueString, labelId, name);
            }
            else {
                for (int i = 0; i < qty; i++) {
                    listInsert.get(i)[colIndex] = value;
                }
            }
            colIndex++;
        }

        // -- 9 .批量插入数据 --
        builderField.append("task_id, row_no");
        builderValue.append("?, ?");

        builder.append("INSERT INTO ..x_label_" + labelId + " ");
        builder.append("(" + builderField.toString() + ") ");
        builder.append("VALUES (" + builderValue.toString() + ")");
        sqlInsert = builder.toString();
        dbSys.batchUpdate(sqlInsert, listInsert);
    }
    public static void deleteTask(DBFactory dbSys, int taskId) throws Exception {
        int labelId;

        String sql;
        SqlRowSet rsTask;
        // ------------------------------------------------
        sql = "SELECT * FROM ..core_task WHERE id = ?";
        rsTask = dbSys.getRowSet(sql, taskId);
        if (rsTask.next()) {
            labelId = rsTask.getInt("label_id");
        }
        else {
            throw new Exception("任务单 (taks_id = " + taskId + ") 已不存在，请检查。");
        }

        // ------------------------------------------------
        sql = "DELETE FROM ..x_label_" + labelId + " WHERE task_id = ?";
        dbSys.exec(sql, taskId);

        sql = "DELETE FROM ..core_task WHERE id = ?";
        dbSys.exec(sql, taskId);
    }
    public static SqlRowSet getTaskData(DBFactory dbSys, int labelId, int taskId, int rowNoFrom, int rowNoTo) throws Exception {
        String sql = "SELECT * FROM ..x_label_" + labelId + " WHERE task_id = ? AND row_no >= ? AND row_no <= ?";
        return dbSys.getRowSet(sql, taskId, rowNoFrom, rowNoTo);
    }
}