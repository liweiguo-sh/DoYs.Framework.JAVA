package com.doys.aprint.base;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.core.db.DBFactory;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
public class LabelService extends BaseService {
    /**
     * 初始化标签数据表
     * 根据标签变量定义，动态生成标签数据表
     *
     * @param dbSys
     * @param labelId
     */
    public static void generateLabelXTable(DBFactory dbSys, int labelId) throws Exception {
        int columnLength;

        String sql;
        String dbPrefix, tableName, columnName;

        StringBuilder builder = new StringBuilder();
        SqlRowSet rs;
        // ------------------------------------------------
        dbPrefix = dbSys.getTenantDbName();
        tableName = dbPrefix + ".x_label_" + labelId;
        builder.append("CREATE TABLE " + tableName + " (");
        builder.append("\n\ttask_id int NOT NULL,");
        builder.append("\n\tid int NOT NULL AUTO_INCREMENT,");

        sql = "SELECT * FROM ..base_label_variable WHERE label_id = ?";
        rs = dbSys.getRowSet(sql, labelId);
        while (rs.next()) {
            columnName = rs.getString("name");
            columnLength = rs.getInt("value_len");
            builder.append("\n\t" + columnName + " varchar(" + columnLength + ") NULL DEFAULT '',");
        }
        builder.append("\n\tcdate datetime NULL DEFAULT CURRENT_TIMESTAMP(0),");
        builder.append("\n\tPRIMARY KEY (id),");
        builder.append("\n\tINDEX fx_x_label_" + labelId + " (task_id)");
        builder.append("\n);");

        sql = "DROP TABLE IF EXISTS " + tableName;
        dbSys.exec(sql);

        sql = builder.toString();
        dbSys.exec(sql);
    }

    public static void generatePrintData(DBFactory dbSys, int labelId, int qty, int taskId, ArrayList<LinkedTreeMap<String, Object>> variables) throws Exception {
        int columnCount, colIndex = 0;

        String sql, sqlInsert, sqlUpdate;
        StringBuilder builder = new StringBuilder();
        StringBuilder builderField, builderValue;

        ArrayList<Object[]> listInsert = new ArrayList<>(qty);
        Object[] paraInsert;
        SqlRowSet rsVariable;

        // -- 0. 测试代码 --
        sql = "DELETE FROM ..x_label_" + labelId;
        dbSys.exec(sql);

        // -- 1. 预处理 --
        sql = "SELECT COUNT(1) FROM ..base_label_variable WHERE label_id = ?";
        columnCount = dbSys.getInt(sql, labelId);
        for (int i = 0; i < qty; i++) {
            paraInsert = new Object[columnCount + 1];
            paraInsert[columnCount] = taskId;
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
        builderField.append("task_id");
        builderValue.append("?");

        builder.append("INSERT INTO ..x_label_" + labelId + " ");
        builder.append("(" + builderField.toString() + ") ");
        builder.append("VALUES (" + builderValue.toString() + ")");
        sqlInsert = builder.toString();
        dbSys.batchUpdate(sqlInsert, listInsert);
    }
}