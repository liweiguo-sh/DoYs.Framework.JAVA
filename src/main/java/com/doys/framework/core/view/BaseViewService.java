/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-05-15
 * 通用视图服务基类, 用于通用视图
 *****************************************************************************/
package com.doys.framework.core.view;
import com.doys.framework.common.UtilDataSet;
import com.doys.framework.common.UtilString;
import com.doys.framework.config.Const;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.core.db.DBFactory;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import java.util.HashMap;
public class BaseViewService extends BaseService {
    public static SqlRowSet getView(DBFactory jtSys, String viewPk) throws Exception {
        String sql = "SELECT * FROM sys_view WHERE pk = ?";
        return jtSys.getRowSet(sql, viewPk);
    }
    public static SqlRowSet getViewField(DBFactory dbSys, String viewPk) {
        String sql = "SELECT  name, text, fixed, align, width, data_source_type, data_source, sequence "
                + "FROM sys_view_field "
                + "WHERE view_pk = ? AND sequence <> 0 "
                + "ORDER BY sequence";
        return dbSys.getRowSet(sql, viewPk);
    }

    public static SqlRowSet getFlowNode(DBFactory jtSys, String flowPks) throws Exception {
        String sql = "SELECT f.pk flow_pk, f.name flow_name, n.node_pk, n.name node_name, n.filter, n.group_pks, n.user_pks, allow_addnew " +
                "FROM sys_flow f INNER JOIN sys_flow_node n ON f.pk = n.flow_pk " +
                "WHERE f.pk IN (" + flowPks + ") " +
                "ORDER BY f.level, f.sequence, n.sequence";
        return jtSys.getRowSet(sql);
    }
    public static SqlRowSet getFlowButton(DBFactory jtSys, String flowPks) throws Exception {
        String sql = "SELECT flow_pk, button_pk, b.name, b.icon, assert_js, action_type, action_do, action_remove, group_pks, user_pks " +
                "FROM sys_flow f INNER JOIN sys_flow_button b ON f.pk = b.flow_pk " +
                "WHERE f.pk IN (" + flowPks + ") AND flag_disabled = 0 " +
                "ORDER BY f.level, f.sequence, b.sequence";
        return jtSys.getRowSet(sql);
    }
    public static SqlRowSet getFlowButton(DBFactory jtSys, String flowPk, String buttonPk) throws Exception {
        String sql = "SELECT * FROM sys_flow_button WHERE flow_pk = ? AND button_pk = ?";
        return jtSys.getRowSet(sql, flowPk, buttonPk);
    }

    public static SqlRowSet getViewData(DBFactory dbSys, SqlRowSet rsView, int pageNum, String sqlFilter, HashMap map) throws Exception {
        String sql = "";
        String sqlData, sqlOrderBy;

        rsView.first();
        sqlData = rsView.getString("sql_data_source");
        sqlOrderBy = rsView.getString("sql_orderby");
        // -- 1. 取总记录行数 -----------------------------------
        if (pageNum == 0) {
            sql = "SELECT COUNT(1) FROM (" + sqlData + ") t ";
            if (!sqlFilter.equals("")) {
                sql += "WHERE " + sqlFilter;
            }
            long totalRows = dbSys.getLong(sql);
            map.put("totalRows", totalRows);
            pageNum = 1;
        }

        // -- 2. 取分页数据 ------------------------------------
        sql = "SELECT * FROM (" + sqlData + ") t ";
        if (!sqlFilter.equals("")) {
            sql += "WHERE " + sqlFilter + " ";
        }
        if (!sqlOrderBy.equals("")) {
            sql += "ORDER BY " + sqlOrderBy + " ";
        }
        sql += "LIMIT " + Const.MAX_PAGE_ROWS * (pageNum - 1) + ", " + Const.MAX_PAGE_ROWS;

        return dbSys.getRowSet(dbSys.replaceSQL(sql));
    }
    public static SqlRowSet getViewDataOne(DBFactory dbBus, SqlRowSet rsView, long id) throws Exception {
        // -- 获取一条视图数据 --
        String sql = "";

        try {
            rsView.first();
            sql = "SELECT * FROM (" + rsView.getString("sql_data_source") + ") t WHERE id = " + id;
            return dbBus.getRowSet(sql);
        } catch (Exception e) {
            logger.error(sql);
            throw e;
        }
    }

    // -- ViewForm ------------------------------------------------------------
    public static HashMap<String, SqlRowSet> getViewDS(DBFactory dbBus, SqlRowSet rsViewField) throws Exception {
        HashMap<String, SqlRowSet> mapDS = new HashMap<>();

        String columnName, dataSourceType, dataSource;
        // ------------------------------------------------
        rsViewField.first();
        while (rsViewField.next()) {
            columnName = rsViewField.getString("name");
            dataSourceType = UtilString.KillNull(rsViewField.getString("data_source_type"));
            if (UtilString.equals(dataSourceType, "sql")) {
                dataSource = UtilString.KillNull(rsViewField.getString("data_source"));
                mapDS.put(columnName, dbBus.getRowSet(dataSource));
            }
        }
        return mapDS;
    }
    public static SqlRowSet getFormData(DBFactory dbSys, SqlRowSet rsView, long id) throws Exception {
        String sql = "";
        String tableName;

        rsView.first();
        tableName = rsView.getString("table_name");
        // ------------------------------------------------
        sql = "SELECT * FROM .." + tableName + " WHERE id = ?";
        return dbSys.getRowSet(sql, id);
    }

    public static long insert(DBFactory dbBus, String tableName, LinkedTreeMap form) throws Exception {
        int nIdx = 0;

        String sql = "SELECT * FROM .." + tableName + " LIMIT 0";
        String columnType, columnName, columnValue, quotes;
        StringBuilder buildField = new StringBuilder();
        StringBuilder buildValue = new StringBuilder();
        SqlRowSetMetaData rsmd;
        // ------------------------------------------------
        try {
            buildField.append("INSERT INTO .." + tableName + " (");
            buildValue.append("VALUES (");

            sql = "SELECT * FROM .." + tableName + " LIMIT 0";
            rsmd = dbBus.getRowSet(sql).getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                columnType = UtilDataSet.getFieldType(rsmd.getColumnTypeName(i));
                columnName = rsmd.getColumnName(i);

                if (!form.containsKey(columnName)) {
                    continue;
                }
                if (!columnName.equalsIgnoreCase("id")) {
                    quotes = "'";
                    buildField.append(nIdx == 0 ? "" : ", ");
                    buildValue.append(nIdx++ == 0 ? "" : ", ");
                    if (columnType.equalsIgnoreCase("number")) {
                        quotes = "";
                        columnValue = form.get(columnName).toString();
                        if (columnValue.equals("")) {
                            columnValue = null;
                        }
                    }
                    else if (columnType.equalsIgnoreCase("datetime")) {
                        columnValue = (String) form.get(columnName);
                        if (columnValue.equals("")) {
                            columnValue = null;
                        }
                    }
                    else {
                        columnValue = (String) form.get(columnName);
                    }

                    buildField.append(columnName);
                    if (columnValue == null) {
                        buildValue.append("NULL");
                    }
                    else {
                        buildValue.append(quotes + columnValue + quotes);
                    }
                }
            }

            buildField.append(") ");
            buildValue.append(")");
            sql = buildField.toString() + buildValue.toString();

            dbBus.exec(sql);
            return dbBus.getLong("SELECT @@identity");
        } catch (Exception e) {
            logger.info(sql);
            throw e;
        }
    }
    public static boolean update(DBFactory dbBus, String tableName, LinkedTreeMap form) throws Exception {
        int result = 0, nIdx = 0;

        String sql = "SELECT * FROM .." + tableName + " LIMIT 0";
        String columnType, columnName, columnValue, quotes;
        StringBuilder builder = new StringBuilder();
        SqlRowSetMetaData rsmd;
        // ------------------------------------------------
        try {
            builder.append("UPDATE .." + tableName + " SET ");

            sql = "SELECT * FROM .." + tableName + " LIMIT 0";
            rsmd = dbBus.getRowSet(sql).getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                columnType = UtilDataSet.getFieldType(rsmd.getColumnTypeName(i));
                columnName = rsmd.getColumnName(i);

                if (!form.containsKey(columnName)) {
                    continue;
                }
                if (!columnName.equalsIgnoreCase("id")) {
                    quotes = "'";
                    builder.append(nIdx++ == 0 ? "" : ", ");
                    if (columnType.equalsIgnoreCase("number")) {
                        quotes = "";
                        columnValue = form.get(columnName).toString();
                        if (columnValue.equals("")) {
                            columnValue = null;
                        }
                    }
                    else if (columnType.equalsIgnoreCase("datetime")) {
                        columnValue = (String) form.get(columnName);
                        if (columnValue.equals("")) {
                            columnValue = null;
                        }
                    }
                    else {
                        columnValue = (String) form.get(columnName);
                    }

                    if (columnValue == null) {
                        builder.append(columnName + " = NULL");
                    }
                    else {
                        builder.append(columnName + " = " + quotes + columnValue + quotes);
                    }
                }
            }

            builder.append(" WHERE id = " + form.get("id"));
            sql = builder.toString();

            result = dbBus.exec(sql);
            if (result != 1) {
                throw new Exception("当前记录已不存在，请刷新视图页面。");
            }
            return true;
        } catch (Exception e) {
            logger.info(sql);
            throw e;
        }
    }
    public static boolean delete(DBFactory dbBus, String tableName, long id) throws Exception {
        String sql = "DELETE FROM .." + tableName + " WHERE id = " + id;
        int result = dbBus.exec(sql);
        if (result == 0) {
            throw new Exception("当前记录已不存在，请刷新视图页面。");
        }
        return true;
    }

    public static boolean doFlow(DBFactory dbBus, String tableName, long id, SqlRowSet rsFlowButton) throws Exception {
        String sql = "";
        String sqlAssert, sqlAction;
        //-------------------------------------------------
        rsFlowButton.first();
        sqlAssert = UtilString.KillNull(rsFlowButton.getString("assert_sql"));
        sqlAction = rsFlowButton.getString("action_do");

        sql = "UPDATE .." + tableName + " SET " + sqlAction + " WHERE id = " + id;
        if (!sqlAssert.equals("")) {
            sql += " AND " + sqlAssert;
        }

        int result = dbBus.exec(sql);
        if (result == 0) {
            throw new Exception("数据状态已变更，不符合操作条件，请刷新视图页面。");
        }
        return true;
    }
}