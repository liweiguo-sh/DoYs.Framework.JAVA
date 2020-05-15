/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-05-15
 * 通用视图服务基类, 用于通用视图
 *****************************************************************************/
package com.doys.framework.core.view;
import com.doys.framework.config.Const;
import com.doys.framework.core.db.DBFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.HashMap;
public class BaseViewService {
    public static SqlRowSet getView(DBFactory jtSys, String viewPk) throws Exception {
        String sql = "SELECT * FROM sys_view WHERE pk = ?";
        return jtSys.queryForRowSet(sql, viewPk);
    }
    public static SqlRowSet getViewField(DBFactory dbSys, String viewPk) {
        String sql = "SELECT  name, text, fixed, align, width "
                + "FROM sys_view_field "
                + "WHERE view_pk = ? AND sequence > 0 "
                + "ORDER BY sequence";
        return dbSys.queryForRowSet(sql, viewPk);
    }

    public static SqlRowSet getFlowNode(DBFactory jtSys, String flowPk) throws Exception {
        String sql = "SELECT  f.name flow_name, n.node_pk, n.name node_name, n.filter, n.groups, n.users " +
                "FROM sys_flow f LEFT JOIN sys_flow_node n ON f.pk = n.flow_pk " +
                "WHERE f.pk = ? ORDER BY n.sequence";
        return jtSys.queryForRowSet(sql, flowPk);
    }

    public static SqlRowSet getViewData(DBFactory dbSys, SqlRowSet rsView, int pageNum, HashMap map) throws Exception {
        String sql = "";
        String sqlData, sqlOrderBy;

        rsView.first();
        sqlData = rsView.getString("sql_data_source");
        sqlOrderBy = rsView.getString("sql_orderby");

        if (pageNum == 0) {
            sql = "SELECT COUNT(1) FROM (" + sqlData + ") t ";
            int totalRows = dbSys.getInt(sql);
            map.put("totalRows", totalRows);
            pageNum = 1;
        }

        sql = "SELECT * FROM (" + sqlData + ") t ";
        if (!sqlOrderBy.equals("")) {
            sql += "ORDER BY " + sqlOrderBy + " ";
        }
        sql += "LIMIT " + Const.MAX_PAGE_ROWS * (pageNum - 1) + ", " + Const.MAX_PAGE_ROWS;

        return dbSys.queryForRowSet(sql);
    }
}