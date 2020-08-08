/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-08-08
 * 码尚标签模板文件服务类
 *****************************************************************************/
package com.doys.aprint.labels.maso;

import com.doys.framework.database.DBFactory;
import com.doys.framework.database.dtb.DataTable;
import com.doys.framework.dts.base.ENTITY_RECORD;

import java.util.ArrayList;
import java.util.HashMap;
public class MasoFileService {
    public static void masoVariableToLabelVariable(DBFactory dbBus, int labelId, ArrayList<HashMap<String, Object>> listVars) throws Exception {
        int nFind;

        String sql;
        String varName, varValue;
        String[] arrFind = new String[1];

        DataTable dtbVariable;
        // -- 1. 添加标签变量 -----------------------------------
        sql = "SELECT label_id, name FROM base_label_variable WHERE label_id = ?";
        dtbVariable = dbBus.getDataTable(sql, labelId);
        dtbVariable.Sort("name");
        for (HashMap<String, Object> map : listVars) {
            varName = (String) map.get("name");
            varValue = map.get("value").toString();

            arrFind[0] = varName;
            nFind = dtbVariable.Find(arrFind);
            if (nFind < 0) {
                ENTITY_RECORD entity = new ENTITY_RECORD(dbBus, "base_label_variable");
                entity
                    .setValue("label_id", labelId)
                    .setValue("name", varName)
                    .setValue("type", "string")
                    .setValue("value", varValue)
                    .setValue("value_len", Math.max(varValue.length(), 20))
                    .Save();
            }
            else {
                dtbVariable.setRowTag(nFind, "1");
            }
        }

        // -- 2. 删除标签变量 -----------------------------------
        for (int i = dtbVariable.getRowCount() - 1; i >= 0; i--) {
            if (!dtbVariable.getRowTag(i).equals("1")) {
                sql = "DELETE FROM base_label_variable WHERE label_id = ? AND name = ?";
                dbBus.exec(sql, labelId, dtbVariable.DataCell(i, "name"));
            }
        }
    }
}