package com.doys.aprint.base;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.database.DBFactory;
import com.doys.framework.database.dtb.DataTable;
import com.doys.framework.dts.parent.ENTITY_RECORD;
import com.doys.framework.upgrade.db.enum1.EntityFieldType;
import com.doys.framework.upgrade.db.obj.EntityField;
import com.doys.framework.upgrade.db.util.MySqlHelper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.ArrayList;
import java.util.HashMap;
public class LabelTableService extends BaseService {
    private static String getLabelXTableName(int labelId) {
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
                field.length = "50";
                field.not_null = false;

                MySqlHelper.addColumn(dbBus, tableName, field);
            }
        }
    }
    public static void dynamicAddLabelTableColumn(DBFactory dbBus, int labelId, ArrayList<HashMap<String, Object>> variables) throws Exception {
        int nFind;

        String sql;
        String tableName = getLabelXTableName(labelId);
        String columnName;
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
            columnName = (String) map.get("name");
            oFind[0] = columnName;
            nFind = dtbFields.Find(oFind);
            if (nFind < 0) {
                EntityField field = new EntityField();
                field.name = columnName;
                field.type = EntityFieldType.STRING;
                field.length = "50";
                field.not_null = false;

                MySqlHelper.addColumn(dbBus, tableName, field);
            }
            // --------------------------------------------
            nFind = dtbVariables.Find(oFind);
            if (nFind < 0) {
                ENTITY_RECORD entity = new ENTITY_RECORD(dbBus, "base_label_variable");
                entity.setValue("label_id", labelId)
                    .setValue("name", columnName)
                    .setValue("type", "string")
                    .setValue("value", "")
                    .setValue("value_len", 50)
                    .setValue("hidden", 1)
                    .Save();
            }
        }
    }
}