/**
 * 标签模板文件和标签数据文件服务类
 */
package com.doys.aprint.labels.bartender;

import com.doys.framework.database.DBFactory;
import com.doys.framework.database.dtb.DataTable;
import com.doys.framework.dts.base.ENTITY_RECORD;
import com.doys.framework.util.UtilFile;

import java.util.ArrayList;
public class BarTenderFileService {
    public static void dataFileToLabelVariable(DBFactory dbBus, int labelId, String dataFile) throws Exception {
        int nFind;

        String sql;
        String columnName;

        String[] arrFind = new String[1];
        String[] headers, values;
        ArrayList<String> list;

        DataTable dtbVariable;
        // -- 1. 读取csv数据 ----------------------------------
        list = UtilFile.readTextFile(dataFile, "", 2);
        if (list.size() < 2) {
            throw new Exception("标签数据模板文件格式错误，请检查。");
        }
        headers = list.get(0).split(",");
        values = list.get(1).split(",");
        if (headers.length != values.length) {
            throw new Exception("标签数据模板文件格式错误，请检查。");
        }

        // -- 2. 添加标签变量 -----------------------------------
        sql = "SELECT label_id, name FROM base_label_variable WHERE label_id = ?";
        dtbVariable = dbBus.getDataTable(sql, labelId);
        dtbVariable.Sort("name");
        for (int i = 0; i < headers.length; i++) {
            columnName = headers[i];
            if (columnName.equals("")) {
                throw new Exception("标签数据模板文件格式错误，请检查。");
            }

            arrFind[0] = columnName;
            nFind = dtbVariable.Find(arrFind);
            if (nFind < 0) {
                ENTITY_RECORD entity = new ENTITY_RECORD(dbBus, "base_label_variable");
                entity
                    .setValue("label_id", labelId)
                    .setValue("name", columnName)
                    .setValue("type", "string")
                    .setValue("value", values[i])
                    .setValue("value_len", Math.max(values[i].length(), 20))
                    .Save();
            }
            else {
                dtbVariable.setRowTag(nFind, "1");
            }
        }

        // -- 3. 删除标签变量 -----------------------------------
        for (int i = dtbVariable.getRowCount() - 1; i >= 0; i--) {
            if (!dtbVariable.getRowTag(i).equals("1")) {
                sql = "DELETE FROM base_label_variable WHERE label_id = ? AND name = ?";
                dbBus.exec(sql, labelId, dtbVariable.DataCell(i, "name"));
            }
        }
    }
}