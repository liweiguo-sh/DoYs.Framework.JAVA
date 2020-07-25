package com.doys.framework.system.service;
import com.doys.framework.aid.DBSchema;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.database.DBFactory;
import com.doys.framework.database.dtb.DataTable;
import com.doys.framework.util.UtilDataSet;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

public class ViewService extends BaseService {
    public static void refreshViewField(DBFactory dbSys, DBFactory dbBus, String viewPk) throws Exception {
        int nResult = 0, nFind = 0;

        String sql = "", sqlViewDS = "";
        String databasePk = "", tablePk = "", tableName = "";
        String fieldName = "", fieldType = "";
        String[] oFind = new String[1];

        SqlRowSet rs = null;
        SqlRowSetMetaData rsmd = null;

        DataTable dtbField = null;
        DataTable dtbView_Field = null;
        DataTable.DataRow dr = null;
        // -- 1.刷新基础表 ---------------------------------
        sql = "SELECT database_pk, table_pk, table_name, sql_data_source FROM sys_view WHERE pk = ?";
        rs = dbSys.getRowSet(sql, viewPk);
        if (rs.next()) {
            databasePk = rs.getString("database_pk");
            tablePk = rs.getString("table_pk");
            tableName = rs.getString("table_name");
            sqlViewDS = rs.getString("sql_data_source");
            if (databasePk.equalsIgnoreCase("sys")) {
                dbBus = null;
                dbBus = dbSys;
            }
        }
        else {
            throw new Exception("视图 " + viewPk + " 不存在。");
        }

        DBSchema dbs = new DBSchema(dbSys, dbBus);
        if (!dbs.refreshDBStruct(databasePk, tableName)) {
            throw new Exception("刷新基础表失败，请检查。");
        }

        // -- 2.表ST_FIELD字段信息更新到表ST_VIEW_FIELD --------
        dtbView_Field = dbSys.getDataTable("SELECT * FROM sys_view_field WHERE view_pk = ?", viewPk);
        dtbView_Field.Sort("name");

        dtbField = dbSys.getDataTable("SELECT * FROM sys_field WHERE table_pk = ?", tablePk);
        dtbField.Sort("name");
        for (int i = 0; i < dtbField.getRowCount(); i++) {
            fieldName = dtbField.DataCell(i, "name");
            oFind[0] = fieldName;
            nFind = dtbView_Field.Find(oFind);
            dr = (nFind < 0 ? dtbView_Field.NewRow() : dtbView_Field.Row(nFind));
            dr.setRowTag("1");

            fieldType = dtbField.DataCell(i, "type");
            dr.setDataCell("view_pk", viewPk);
            dr.setDataCell("name", fieldName);
            dr.setDataCell("table_pk", tablePk);
            dr.setDataCell("field_pk", tablePk + "." + fieldName);

            if (dr.DataCell("text", true).equals("")) {
                dr.setDataCell("text", dtbField.DataCell(i, "text"));
            }
            if (dr.DataCell("title", true).equals("")) {
                dr.setDataCell("title", dtbField.DataCell(i, "title"));
            }
            dr.setDataCell("type", fieldType);
            dr.setDataCell("datatype", dtbField.DataCell(i, "datatype"));
            dr.setDataCell("length", dtbField.DataCell(i, "length"));
            dr.setDataCell("field_pk", dtbField.DataCell(i, "pk"));
            dr.setDataCell("flag_identity", dtbField.DataCell(i, "flag_identity"));
            if (dr.DataCell("flag_nullable") == null) {
                dr.setDataCell("flag_nullable", dtbField.DataCell(i, "flag_nullable"));
            }
            if (dr.DataCell("width") == null) {
                dr.setDataCell("width", dtbField.DataCell(i, "width"));
            }
            if (dr.DataCell("align") == null) {
                dr.setDataCell("align", dtbField.DataCell(i, "align"));
            }
            ///if (dr.DataCell("field_query_order") == null) {
            ///dr.setDataCell("field_query_order", i);
            ///}
            if (dr.DataCell("default_value", true).equals("")) {
                dr.setDataCell("default_value", dtbField.DataCell(i, "default_value"));
            }
            if (dr.DataCell("remark", true).equals("")) {
                dr.setDataCell("remark", dtbField.DataCell(i, "remark"));
            }
            if (dr.DataCell("sequence", true).equals("")) {
                dr.setDataCell("sequence", 0);
            }

            if (nFind < 0) {
                dtbView_Field.AddRow(dr);
            }
        }

        // -- 3.根据视图SQL执行结果，将非基础表字段添加到ST_VIEW_FIELD ---
        sql = "SELECT * FROM (" + sqlViewDS + ") t WHERE 1 = 0";
        rs = dbBus.getRowSet(sql);
        rsmd = rs.getMetaData();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            fieldName = rsmd.getColumnLabel(i).toLowerCase();
            oFind[0] = fieldName;
            nFind = dtbView_Field.Find(oFind);
            if (nFind < 0) {
                dr = dtbView_Field.NewRow();
            }
            else {
                if (!dtbView_Field.getRowTag(nFind).equals("")) {
                    continue; // -- 是基础表字段, 第2部分代码已处理 --
                }
                dr = dtbView_Field.Row(nFind);
            }
            dr.setRowTag("1");

            dr.setDataCell("view_pk", viewPk);
            dr.setDataCell("name", fieldName);
            dr.setDataCell("table_pk", "");
            if (dr.DataCell("field_pk", true).equals("")) {
                // -- _g字段, 特殊处理 --
                dr.setDataCell("field_pk", fieldName);
            }

            if (dr.DataCell("text", true).equals("")) {
                dr.setDataCell("text", fieldName);
            }
            if (dr.DataCell("title", true).equals("")) {
                dr.setDataCell("title", fieldName);
            }
            dr.setDataCell("datatype", rsmd.getColumnTypeName(i));
            dr.setDataCell("type", UtilDataSet.getFieldType(rsmd.getColumnTypeName(i)));
            dr.setDataCell("length", rsmd.getColumnDisplaySize(i));
            dr.setDataCell("flag_pkey", "0");
            dr.setDataCell("flag_identity", "0");
            ///dr.setDataCell("field_nullable", rsmd.isNullable(i));
            if (dr.DataCell("width", true).equals("")) {
                dr.setDataCell("width", UtilDataSet.getColumnWidth(dr.DataCell("type"), dr.DataCell("text"), Integer.parseInt(dr.DataCell("length"))));
            }

            if (dr.DataCell("align") == null || dr.DataCell("align").equals("")) {
                if (dr.DataCell("type").equals("datetime")) {
                    dr.setDataCell("align", "center");
                }
                else if (dr.DataCell("type").equals("int") || dr.DataCell("type").equals("number")) {
                    dr.setDataCell("align", "right");
                }
                else {
                    if (dr.DataCell("align", true).equals("")) {
                        dr.setDataCell("align", "left");
                    }
                }
            }
            if (dr.DataCell("sequence", true).equals("")) {
                dr.setDataCell("sequence", 1);
            }

            if (nFind < 0) {
                dtbView_Field.AddRow(dr);
            }
        }
        // -- 4.删除不存在的字段 ------------------------------
        for (int i = dtbView_Field.getRowCount() - 1; i >= 0; i--) {
            if (dtbView_Field.getRowTag(i).equals("")) {
                dtbView_Field.RemoveAt(i);
            }
        }
        // -- 5.提交保存 ----------------------------------
        nResult = dtbView_Field.Update(dbSys, "sys_view_field", "view_pk,name");
        if (nResult < 0) {
            throw new Exception("意外错误。");
        }
    }
}