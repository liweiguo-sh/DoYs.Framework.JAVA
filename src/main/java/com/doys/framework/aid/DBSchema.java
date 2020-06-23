/*************************************************
 * Copyright (C), 2012-2017, xpas-next.com
 * @author Volant Lee.
 * @version 3.0
 * @create date 2012-07-20
 * @modify date 2017-09-12
 * 解析数据库字段信息到表 ST_TALBE/ST_FIELD/ST_INDEX中, 只追加, 不删除
 */
package com.doys.framework.aid;
import com.doys.framework.database.DBFactory;
import com.doys.framework.database.dtb.DataTable;
import com.doys.framework.util.UtilDataSet;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class DBSchema {
    private DBFactory dbMaster = null;
    // ------------------------------------------------------------------------
    public DBSchema(DBFactory _dbMaster) {
        dbMaster = _dbMaster;
    }
    public boolean refreshDBStruct(String databasePk, String tableName) throws Exception {
        String sql = "";
        String databaseName = "", databaseType = "";

        SqlRowSet rs = null;
        // ------------------------------------------------
        try {
            tableName = tableName.toLowerCase();
            sql = "SELECT * FROM sys_database WHERE pk = ?";
            rs = dbMaster.getRowSet(sql, new Object[] { databasePk });
            if (rs.next()) {
                databaseType = rs.getString("type");
                databaseName = rs.getString("name");
                if (databasePk.equalsIgnoreCase("prefix")) {
                    databaseName += dbMaster.getTenantId();
                }
            }
            else {
                throw new Exception("没有找到逻辑数据库名称为 " + databasePk + " 的记录, 请检查.");
            }

            if (databaseType.equalsIgnoreCase("MySQL")) {
                if (refreshDBStruct_MySQL_Tables(databasePk, databaseName, tableName) == false) {
                    return false;
                }
            }
            else {
                throw new Exception("框架暂不支持 " + databaseType + " 数据库。");
            }
        } catch (Exception e) {
            throw e;
        } finally {
        }
        return true;
    }

    // -- MySQL ---------------------------------------------------------------
    private boolean refreshDBStruct_MySQL_Tables(String databasePk, String databaseName, String tableName) throws Exception {
        int nFind = 0, nResult = 0;

        String sql = "";
        String[] oFind = new String[1];

        DataTable dtb = null;
        DataTable.DataRow drNew = null;
        // ------------------------------------------------
        sql = "SELECT CONCAT('" + databasePk + "', '.', table_name) AS pk, table_name name, CASE table_type WHEN 'BASE TABLE' THEN 'U' ELSE 'V' END AS type "
            + "FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + databaseName + "' AND (table_type = 'BASE TABLE' OR table_type = 'VIEW')";
        if (!tableName.equals("")) {
            sql += "AND table_name IN ('" + tableName.replaceAll(",", "','") + "') ";
        }
        sql += "ORDER BY table_name";

        try {
            SqlRowSet rs = dbMaster.getRowSet(sql);
            sql = "SELECT * FROM sys_table WHERE database_pk = '" + databasePk + "' ";
            if (!tableName.equals("")) {
                sql += "AND name IN ('" + tableName.replaceAll(",", "','") + "') ";
            }
            sql += "ORDER BY pk";
            dtb = dbMaster.getDataTable(sql);
            dtb.Sort("pk");
            // -- 1. 添加新表 ---------------------------------
            while (rs.next()) {
                oFind[0] = rs.getString("pk");
                nFind = dtb.Find(oFind);
                if (nFind < 0) {
                    drNew = dtb.NewRow();
                    drNew.setDataCell("database_pk", databasePk);
                    drNew.setDataCell("pk", rs.getString("pk"));
                    drNew.setDataCell("name", rs.getString("name"));
                    drNew.setDataCell("type", rs.getString("type"));
                    drNew.setDataCell("text", rs.getString("name"));
                    drNew.setRowTag("1");
                    dtb.AddRow(drNew);
                }
                else {
                    dtb.setRowTag(nFind, "1");
                }
            }
            // -- 2. 删除不存在的表(禁用) --------------------------
            for (int i = dtb.getRowCount() - 1; i >= 0; i--) {
                if (dtb.getRowTag(i).equals("")) {
                    // -- dtb.RemoveAt(i); --
                }
            }
            nResult = dtb.Update(dbMaster, "sys_table", "pk");
            if (nResult < 0) {
                throw new Exception("refreshDBStruct_MySQL_Table 遇到错误，请检查。");
            }
            // -- 3. 刷新数据表字段 ------------------------------
            if (refreshDBStruct_MySQL_Fields(databasePk, databaseName, tableName) == false) {
                return false;
            }
            // -- 4. 刷新数据表索引 ------------------------------
            /*
            if (refreshDBStruct_MySQL_Indexes(databasePk, databaseName, tableName) == false) {
                return false;
            }
            */
        } catch (Exception e) {
            throw e;
        } finally {
        }
        return true;
    }
    private boolean refreshDBStruct_MySQL_Fields(String databasePk, String databaseName, String tableName) throws Exception {
        int nFind = 0, nResult = 0;

        String sql = "";
        String dataType = "", fieldType = "", fieldDefault = "", fieldNote = "";
        String[] oFind = new String[1];

        DataTable dtb = null;
        DataTable.DataRow drRow = null;
        // ------------------------------------------------
        sql = "SELECT * FROM sys_field WHERE SUBSTR(table_pk, 1, " + (databasePk.length() + 1) + ") = '" + databasePk + ".' ";
        if (!tableName.equals("")) {
            sql += "AND RIGHT(table_pk, LENGTH(table_pk) - " + databasePk.length() + " - 1) IN ('" + tableName.replaceAll(",", "','") + "') ";
        }
        sql += "ORDER BY pk";
        dtb = dbMaster.getDataTable(sql);
        dtb.Sort("pk");

        // -- 动态视图SQL -------------------------------------
        sql = "SELECT CONCAT('" + databasePk + "', '.', UPPER(table_name)) table_pk, ";
        sql += "CONCAT('" + databasePk + "', '.', UPPER(table_name), '.', column_name) pk, column_name name, data_type, ";
        sql += "character_maximum_length length, CASE is_nullable WHEN 'YES' THEN 1 ELSE 0 END flag_nullable, ";
        sql += "CASE extra WHEN 'auto_increment' THEN 1 ELSE 0 END flag_identity, CASE column_key WHEN 'PRI' THEN 1 ELSE 0 END flag_pkey, ";
        sql += "IFNULL(column_default,'') default_value, column_comment note ";
        sql += "FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = '" + databaseName + "' ";
        if (!tableName.equals("")) {
            sql += "AND table_name IN ('" + tableName.replaceAll(",", "','") + "') ";
        }
        sql += "ORDER BY table_name, column_name, flag_pkey";
        // ------------------------------------------------
        try {
            SqlRowSet rs = dbMaster.getRowSet(sql);
            while (rs.next()) {
                oFind[0] = rs.getString("pk");
                nFind = dtb.Find(oFind);
                if (nFind < 0) {
                    drRow = dtb.NewRow();
                    drRow.setDataCell("table_pk", rs.getString("table_pk"));
                    drRow.setDataCell("pk", rs.getString("pk"));
                    drRow.setDataCell("name", rs.getString("name"));
                    drRow.setRowTag("1");
                    dtb.AddRow(drRow);
                }
                else {
                    drRow = dtb.Row(nFind);
                    dtb.setRowTag(nFind, "1");
                }
                // -- 1、固定属性信息 --------
                dataType = rs.getString("data_type").toLowerCase();
                fieldType = UtilDataSet.getFieldType(dataType);

                drRow.setDataCell("datatype", dataType);
                drRow.setDataCell("type", fieldType);
                drRow.setDataCell("pk", rs.getString("pk"));
                drRow.setDataCell("flag_pkey", rs.getString("flag_pkey"));
                drRow.setDataCell("flag_identity", rs.getString("flag_identity"));
                drRow.setDataCell("text", rs.getString("name"));
                drRow.setDataCell("length", rs.getString("length"));
                drRow.setDataCell("flag_nullable", rs.getString("flag_nullable"));
                // -- 2、field_default --
                fieldDefault = rs.getString("default_value");
                drRow.setDataCell("default_value", fieldDefault);
                // -- 3、field_remark --
                fieldNote = rs.getString("note");
                drRow.setDataCell("note", fieldNote);
                if (fieldNote != null && !fieldNote.equals("")) {
                    String[] arrRemark = fieldNote.split("\\|");
                    drRow.setDataCell("text", arrRemark[0]);
                    drRow.setDataCell("remark", arrRemark[arrRemark.length - 1]);
                }
                // -- 5、others ---------
                if (fieldType.equalsIgnoreCase("datetime") || fieldType.equalsIgnoreCase("number")) {
                    drRow.setDataCell("width", 90);
                }
                else {
                    drRow.setDataCell("width", UtilDataSet.getColumnWidth(fieldType, drRow.DataCell("text"), Integer.parseInt(drRow.DataCell("length"))));
                }

                if (fieldType.equalsIgnoreCase("number")) {
                    drRow.setDataCell("align", "right");
                }
                else if (fieldType.equalsIgnoreCase("datetime")) {
                    drRow.setDataCell("align", "center");
                }
                else {
                    drRow.setDataCell("align", "left");
                }
            }
            // -- 3、删除不存在的字段(禁用) --------------------------
            for (int i = dtb.getRowCount() - 1; i >= 0; i--) {
                if (dtb.getRowTag(i).equals("")) {
                    // -- dtb.RemoveAt(i); --
                }
            }
            nResult = dtb.Update(dbMaster, "sys_field", "pk");
            if (nResult < 0) {
                throw new Exception("刷新数据库字段过程中遇到意外错误.");
            }
        } catch (Exception e) {
            throw e;
        } finally {
        }
        return true;
    }
    private boolean refreshDBStruct_MySQL_Indexes(String databaseKey, String databaseName, String tableName) throws Exception {
        String sql = "";
        // ------------------------------------------------
        sql = "DELETE FROM ST_INDEX_FIELD WHERE ";
        if (tableName.equals("")) {
            sql += "LEFT(table_pk," + databaseKey.length() + ") = '" + databaseKey + "'";
        }
        else {
            sql += "table_pk = '" + databaseKey + "." + tableName + "'";
        }
        dbMaster.exec(sql);
        // ------------------------------------------------
        sql = "INSERT INTO sys..ST_INDEX_FIELD SELECT CONCAT('" + databaseKey
            + ".', UPPER(s.table_name)) AS table_pk, index_name, column_name AS field_name, CASE non_unique WHEN 1 THEN 0 ELSE 1 END AS is_unique, CASE constraint_type WHEN 'PRIMARY KEY' THEN 1 ELSE 2 END AS index_type "
            + "FROM INFORMATION_SCHEMA.STATISTICS s LEFT JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc ON s.table_schema = tc.table_schema  AND s.table_name = tc.table_name AND s.index_name = tc.constraint_name "
            + "WHERE s.table_schema = '" + databaseName + "' " + (tableName.equals("") ? "" : "AND s.table_name = '" + tableName + "' ")
            + "ORDER BY table_pk, index_type, index_name";
        dbMaster.exec(sql);
        // ------------------------------------------------
        return true;
    }
}