package com.doys.aprint.base;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.database.DBFactory;
import com.doys.framework.database.dtb.DataTable;
import com.doys.framework.dts.base.ENTITY_RECORD;
import com.doys.framework.upgrade.db.enum1.EntityFieldType;
import com.doys.framework.upgrade.db.obj.EntityField;
import com.doys.framework.upgrade.db.util.MySqlHelper;
import com.doys.framework.util.UtilTable;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.ArrayList;
import java.util.HashMap;
public class LabelTableService extends BaseService {
    public static String getLabelXTableName(long labelId) {
        return "x_label_" + labelId;
    }
    private static void createLabelTable(DBFactory dbBus, String tableName) throws Exception {
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

    public static void createLabelTable(DBFactory dbBus, int labelId) throws Exception {
        int nFind;

        String sql;
        String tableName, columnName;
        String[] oFind = new String[1];

        SqlRowSet rsVaribles;
        DataTable dtbFields;
        // -- 动态创建表 ---------------------------------------
        tableName = getLabelXTableName(labelId);
        if (!MySqlHelper.hasTable(dbBus, tableName)) {
            createLabelTable(dbBus, tableName);
        }

        // -- 追加创建字段(标签变量) --------------------------------
        sql = "SELECT column_name FROM information_schema.columns WHERE table_schema = (SELECT database()) AND table_name = ?";
        dtbFields = dbBus.getDataTable(sql, tableName);
        dtbFields.Sort("column_name");

        sql = "SELECT * FROM base_label_variable WHERE label_id = ?";
        rsVaribles = dbBus.getRowSet(sql, labelId);
        while (rsVaribles.next()) {
            columnName = rsVaribles.getString("name");
            oFind[0] = columnName;
            nFind = dtbFields.Find(oFind);
            if (nFind < 0) {
                EntityField field = new EntityField();
                field.name = columnName;
                field.type = EntityFieldType.STRING;
                field.length = "" + rsVaribles.getInt("value_len");
                field.not_null = false;

                MySqlHelper.addColumn(dbBus, tableName, field);
            }
        }
    }
    public static void dynamicDelLabelTableColumn(DBFactory dbBus, int labelId, String varName) throws Exception {
        String sql;
        String tableName = getLabelXTableName(labelId);

        if (MySqlHelper.hasColumn(dbBus, tableName, varName)) {
            MySqlHelper.dropColumn(dbBus, tableName, varName);
        }

        sql = "DELETE FROM base_label_variable WHERE label_id = ? AND name = ?";
        dbBus.exec(sql, labelId, varName);
    }
    public static void dynamicAddLabelTableColumn(DBFactory dbBus, int labelId, ArrayList<HashMap<String, Object>> variables) throws Exception {
        int nFind, fieldLenth;

        String sql;
        String tableName = getLabelXTableName(labelId);
        String name, value, type;
        String[] oFind = new String[1];

        DataTable dtbFields, dtbVariables;
        // -- 动态创建表 ---------------------------------------
        if (!MySqlHelper.hasTable(dbBus, tableName)) {
            createLabelTable(dbBus, tableName);
        }

        // -- 追加创建字段(标签变量) --------------------------------
        sql = "SELECT column_name FROM information_schema.columns WHERE table_schema = (SELECT database()) AND table_name = ?";
        dtbFields = dbBus.getDataTable(sql, tableName);
        dtbFields.Sort("column_name");

        sql = "SELECT name FROM base_label_variable WHERE label_id = ?";
        dtbVariables = dbBus.getDataTable(sql, labelId);
        dtbVariables.Sort("name");

        for (HashMap<String, Object> map : variables) {
            name = (String) map.get("name");
            value = map.get("value").toString();
            if (map.containsKey("value_len")) {
                fieldLenth = (int) map.get("value_len");
            }
            else {
                fieldLenth = 20;
            }
            if (map.containsKey("type")) {
                type = (String) map.get("type");
            }
            else {
                type = "string";
            }

            if (type.equalsIgnoreCase("seq")) {
                if (map.containsKey("value_len")) {
                    fieldLenth = Integer.parseInt(map.get("value_len").toString());
                }
                else {
                    fieldLenth = value.length();
                }
            }
            else {
                fieldLenth = Math.max(Math.max(value.length(), fieldLenth), 20);
            }

            if (name.contains("标签元素") || name.contains("共享变量")) {
                continue;
            }
            if (!UtilTable.isValidColumnName(name)) {
                continue;
            }

            // -- 创建字段 ------------------------------------
            oFind[0] = name;
            nFind = dtbFields.Find(oFind);
            if (nFind < 0) {
                EntityField field = new EntityField();
                field.name = name;
                field.type = EntityFieldType.STRING;
                field.length = fieldLenth + "";
                field.not_null = false;

                try {
                    MySqlHelper.addColumn(dbBus, tableName, field);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
            // -- 创建变量定义 ----------------------------------
            nFind = dtbVariables.Find(oFind);
            if (nFind < 0) {
                ENTITY_RECORD entity = new ENTITY_RECORD(dbBus, "base_label_variable");
                entity.setValue("label_id", labelId)
                    .setValue("name", name)
                    .setValue("type", "string")
                    .setValue("value", value)
                    .setValue("value_len", fieldLenth)
                    .setValue("hidden", 0)
                    .Save();
            }
        }
    }
}