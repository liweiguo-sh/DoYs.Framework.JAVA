/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-08-08
 * 标签数据表服务类
 *****************************************************************************/
package com.doys.aprint.labels;
import com.doys.aprint.cfg.AprintConst;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.database.DBFactory;
import com.doys.framework.database.dtb.DataTable;
import com.doys.framework.upgrade.db.enum1.EntityFieldType;
import com.doys.framework.upgrade.db.obj.EntityField;
import com.doys.framework.upgrade.db.util.MySqlHelper;
public class LabelTableService extends BaseService {
    public static String getLabelXTableName(long labelId) {
        return "x_label_" + labelId;
    }
    public static void createLabelTable(DBFactory dbBus, String tableName) throws Exception {
        String sql;
        StringBuilder builder = new StringBuilder();

        builder.append("CREATE TABLE " + tableName + " (");

        builder.append("\n\ttask_id int NOT NULL,");
        builder.append("\n\tid bigint NOT NULL AUTO_INCREMENT,");
        builder.append("\n\t$row_no int NOT NULL,");
        builder.append("\n\t$cdate datetime NULL DEFAULT CURRENT_TIMESTAMP(0),");

        builder.append("\n\tPRIMARY KEY (id),");
        builder.append("\n\tINDEX fx_" + tableName + "_label_id (task_id),");
        builder.append("\n\tINDEX ix_" + tableName + "_row_no ($row_no)");
        builder.append("\n);");

        sql = builder.toString();
        dbBus.exec(sql);
    }

    /**
     * 根据标签变量表，动态创建标签数据表 x_label_xxx 表
     *
     * @param dbBus
     * @param labelId
     * @throws Exception
     */
    public static void labelVariableToLabelColumn(DBFactory dbBus, long labelId) throws Exception {
        int nFind, valueLen, columnLen;

        String sql;
        String tableName, varName, columnName;
        String[] arrFind = new String[1];

        DataTable dtbVariable, dtbField;
        // ------------------------------------------------
        tableName = getLabelXTableName(labelId);
        sql = "SELECT column_name, character_maximum_length column_len FROM information_schema.COLUMNS " +
            "WHERE table_schema = (SELECT database()) AND table_name = ? AND column_name NOT IN ('id', 'task_id', '$cdate', '$row_no')";
        dtbField = dbBus.getDataTable(sql, tableName);
        dtbField.Sort("column_name");

        sql = "SELECT name, value_len FROM base_label_variable WHERE label_id = ?";
        dtbVariable = dbBus.getDataTable(sql, labelId);
        dtbVariable.Sort("name");

        // -- 增加、更新字段 -------------------------------------
        for (int i = 0; i < dtbVariable.getRowCount(); i++) {
            varName = dtbVariable.DataCell(i, "name");
            valueLen = Math.max(Integer.parseInt(dtbVariable.DataCell(i, "value_len")), AprintConst.MIN_XLABEL_COLUMN_LENGTH);

            arrFind[0] = varName;
            nFind = dtbField.Find(arrFind);
            if (nFind < 0) {
                EntityField field = new EntityField();
                field.name = varName;
                field.type = EntityFieldType.STRING;
                field.length = String.valueOf(valueLen);
                MySqlHelper.addColumn(dbBus, tableName, field);
            }
            else {
                columnLen = Integer.parseInt(dtbField.DataCell(nFind, "column_len"));
                if (columnLen != valueLen) {
                    MySqlHelper.updateColumnLength(dbBus, tableName, varName, "string", valueLen);
                }
            }
        }

        // -- 删除字段 ----------------------------------------
        for (int i = 0; i < dtbField.getRowCount(); i++) {
            columnName = dtbField.DataCell(i, "column_name");
            arrFind[0] = columnName;
            nFind = dtbVariable.Find(arrFind);
            if (nFind < 0) {
                MySqlHelper.dropColumn(dbBus, tableName, columnName);
            }
        }
    }
}