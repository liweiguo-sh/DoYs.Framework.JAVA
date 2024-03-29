/******************************************************************************
 * Copyright (C), 2020-2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-05-15
 * @modify_date 2021-11-10
 * 通用视图服务基类, 用于通用视图
 *****************************************************************************/
package doys.framework.core.view;
import doys.framework.core.Token;
import doys.framework.core.base.BaseService;
import doys.framework.core.ex.CommonException;
import doys.framework.database.DBFactory;
import doys.framework.util.UtilRowSet;
import doys.framework.util.UtilString;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import java.util.HashMap;

public class BaseViewService extends BaseService {
    public static SqlRowSet getView(DBFactory dbSys, String viewPk) throws Exception {
        String sql = "SELECT * FROM sys_view WHERE pk = ?";
        SqlRowSet rsView = dbSys.getRowSet(sql, viewPk);

        rsView.first();

        return rsView;
    }
    public static SqlRowSet getViewField(DBFactory dbSys, String viewPk) throws Exception {
        String sql = "SELECT table_pk, name, text, fixed, flag_nullable, align, width, datatype, data_source_type, data_source, sequence "
            + "FROM sys_view_field "
            + "WHERE view_pk = ? AND sequence <> 0 "
            + "ORDER BY sequence";
        return dbSys.getRowSet(sql, viewPk);
    }

    public static SqlRowSet getFlowNode(DBFactory dbSys, String flowPks) throws Exception {
        String sql = "SELECT f.pk flow_pk, f.name flow_name, n.node_pk, n.name node_name, n.filter, n.group_pks, n.user_pks, allow_addnew " +
            "FROM sys_flow f INNER JOIN sys_flow_node n ON f.pk = n.flow_pk " +
            "WHERE f.pk IN (" + flowPks + ") " +
            "ORDER BY f.level, f.sequence, n.sequence";
        return dbSys.getRowSet(sql);
    }
    public static SqlRowSet getFlowButton(DBFactory dbSys, String flowPks) throws Exception {
        String sql = "SELECT flow_pk, button_pk, b.name, b.icon, assert_js, action_type, action_do, action_remove, group_pks, user_pks " +
            "FROM sys_flow f INNER JOIN sys_flow_button b ON f.pk = b.flow_pk " +
            "WHERE f.pk IN (" + flowPks + ") AND flag_disabled = 0 " +
            "ORDER BY f.level, f.sequence, b.sequence";
        return dbSys.getRowSet(sql);
    }
    public static SqlRowSet getFlowButton(DBFactory dbSys, String flowPk, String buttonPk) throws Exception {
        String sql = "SELECT * FROM sys_flow_button WHERE flow_pk = ? AND button_pk = ?";
        return dbSys.getRowSet(sql, flowPk, buttonPk);
    }

    public static SqlRowSet getTree(DBFactory dbSys, String treePk) throws Exception {
        String sql = "SELECT * FROM sys_tree WHERE pk = ?";
        return dbSys.getRowSet(sql, treePk);
    }
    public static SqlRowSet getTreeLevel(DBFactory dbSys, String treePk) throws Exception {
        String sql = "SELECT * FROM sys_tree_level WHERE tree_pk = ?";
        return dbSys.getRowSet(sql, treePk);
    }
    public static SqlRowSet getTreeNode(DBFactory dbSys, DBFactory dbBus, String treePk, int nodeLevel, String nodeValue) throws Exception {
        String sql, sqlData;

        sql = "SELECT sql_data FROM sys_tree_level WHERE tree_pk = ? AND level = ?";
        sqlData = dbSys.getValue(sql, "", treePk, nodeLevel);

        sql = sqlData.replaceAll("\\{node_value}", nodeValue);
        return dbBus.getRowSet(sql);
    }

    // -- ViewForm ------------------------------------------------------------
    public static SqlRowSet getViewBaseField(DBFactory dbSys, String viewPk, String tablePk) throws Exception {
        String sql = "SELECT table_pk, name, text, datatype, data_source_type, data_source, flag_nullable "
            + "FROM sys_view_field "
            + "WHERE view_pk = ? AND table_pk = ? ORDER BY name";
        return dbSys.getRowSet(sql, viewPk, tablePk);
    }
    public static HashMap<String, SqlRowSet> getViewDS(DBFactory dbBus, SqlRowSet rsViewField) throws Exception {
        HashMap<String, SqlRowSet> mapDS = new HashMap<>();

        String columnName, dataSourceType, dataSource;
        // ------------------------------------------------
        rsViewField.beforeFirst();
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
    public static SqlRowSet getViewButton(DBFactory dbMaster, String viewPk) throws Exception {
        String sql = "SELECT button_pk, name, icon, assert_js, action_type, action_do, action_remove, group_pks, user_pks "
            + "FROM sys_view_button "
            + "WHERE view_pk = ? AND flag_disabled = 0 "
            + "ORDER BY sequence";
        return dbMaster.getRowSet(sql, viewPk);
    }
    public static SqlRowSet getFormData(DBFactory dbBus, String tableName, long id) throws Exception {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        return dbBus.getRowSet(sql, id);
    }

    public static long insert(DBFactory dbBus, String tableName, HashMap form, Token token) throws Exception {
        int nIdx = 0;

        String sql;
        String columnType, columnName, columnValue, quotes;
        StringBuilder buildField = new StringBuilder();
        StringBuilder buildValue = new StringBuilder();
        SqlRowSetMetaData rsmd;
        // ------------------------------------------------
        try {
            buildField.append("INSERT INTO " + tableName + " (");
            buildValue.append("VALUES (");

            sql = "SELECT * FROM " + tableName + " LIMIT 0";
            rsmd = dbBus.getRowSet(sql).getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                columnType = UtilRowSet.getFieldType(rsmd.getColumnTypeName(i));
                columnName = rsmd.getColumnName(i);

                // -- 预处理 ---------------------------------
                if (columnName.equalsIgnoreCase("creator")) {
                    if (!form.containsKey(columnName)) {
                        form.put(columnName, token.getString("userPk"));
                    }
                    else {
                        form.replace(columnName, token.getString("userPk"));
                    }
                }
                else if (columnName.equalsIgnoreCase("cdate")) {
                    continue;
                }
                else if (columnName.equalsIgnoreCase("astatus")) {
                    if (!form.containsKey(columnName)) {
                        continue;
                    }
                    else {
                        if (form.get(columnName).toString().equals("")) {
                            form.replace(columnName, "0");
                        }
                    }
                }
                else if (columnName.equalsIgnoreCase("id_ver")) {
                    // -- 新增记录时，数据记录版本号为0 --
                    if (!form.containsKey(columnName)) {
                        form.put(columnName, 0);
                    }
                    else {
                        form.replace(columnName, 0);
                    }
                }
                else {
                    if (!form.containsKey(columnName)) {
                        continue;
                    }
                }
                // ----------------------------------------
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
                        columnValue = columnValue.replaceAll("'", "''");
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
            throw e;
        }
    }
    public static boolean update(DBFactory dbSys, DBFactory dbBus, String tableName, HashMap form, Token token) throws Exception {
        int result, nIdx = 0;
        long id = Long.parseLong(form.get("id").toString());

        String sql;
        String columnType, columnName, columnValue, quotes;
        StringBuilder builder = new StringBuilder();
        SqlRowSetMetaData rsmd;
        // ------------------------------------------------
        builder.append("UPDATE " + tableName + " SET ");

        sql = "SELECT * FROM " + tableName + " LIMIT 0";
        rsmd = dbBus.getRowSet(sql).getMetaData();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            columnType = UtilRowSet.getFieldType(rsmd.getColumnTypeName(i));
            columnName = rsmd.getColumnName(i);

            // -- 预处理 ---------------------------------
            if (!form.containsKey(columnName)) {
                continue;
            }
            // --------------------------------------------
            if (columnName.equalsIgnoreCase("id")) {
            }
            else if (columnName.equalsIgnoreCase("modifier")) {
                builder.append((nIdx++ == 0 ? "" : ", ") + columnName + " = '" + token.getString("userPk") + "'");
            }
            else if (columnName.equalsIgnoreCase("mdate")) {
                builder.append((nIdx++ == 0 ? "" : ", ") + columnName + " = now()");
            }
            else if (columnName.equalsIgnoreCase("id_ver")) {
                int valueDB = dbBus.getInt("SELECT id_ver FROM " + tableName + " WHERE id = ?", 0, id);
                int valueForm = UtilString.toInt(form.get(columnName).toString());
                if (valueDB != valueForm) {
                    throw new CommonException("当前记录已被修改，请关闭界面重新打开。");
                }
                builder.append((nIdx++ == 0 ? "" : ", ") + columnName + " = " + (valueDB + 1));
            }
            else {
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
                    if (columnValue == null || columnValue.equals("")) {
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
                    columnValue = columnValue.replaceAll("'", "''");
                    builder.append(columnName + " = " + quotes + columnValue + quotes);
                }
            }
        }

        builder.append(" WHERE id = " + id);
        sql = builder.toString();

        result = dbBus.exec(sql);
        if (result != 1) {
            throw new CommonException("当前记录已不存在，请刷新视图页面。");
        }
        return true;
    }
    public static boolean delete(DBFactory dbBus, String tableName, long id) throws Exception {
        String sql = "DELETE FROM " + tableName + " WHERE id = " + id;
        int result = dbBus.exec(sql);
        if (result == 0) {
            throw new CommonException("当前记录已不存在，请刷新视图页面。");
        }
        return true;
    }

    public static boolean doFlow(DBFactory dbBus, String tableName, long id, SqlRowSet rsFlowButton, String userPk) throws Exception {
        String sql = "";
        String sqlAssert, sqlAction;
        //-------------------------------------------------
        rsFlowButton.first();
        sqlAssert = UtilString.KillNull(rsFlowButton.getString("assert_sql"));
        sqlAction = rsFlowButton.getString("action_do");

        sql = "UPDATE " + tableName + " SET " + sqlAction + ", auditor = ?, adate = now() WHERE id = " + id;
        if (!sqlAssert.equals("")) {
            sql += " AND " + sqlAssert;
        }

        int result = dbBus.exec(sql, userPk);
        if (result == 0) {
            throw new CommonException("数据状态已变更，不符合操作条件，请刷新视图页面。");
        }
        return true;
    }
}