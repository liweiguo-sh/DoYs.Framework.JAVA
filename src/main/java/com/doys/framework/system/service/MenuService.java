package com.doys.framework.system.service;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.database.DBFactory;
import com.doys.framework.database.ds.UtilDDS;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class MenuService extends BaseService {
    public static SqlRowSet getSystem(DBFactory dbSys, DBFactory dbBus, String sqlUserGroupPks) throws Exception {
        String sql, sqlMenu;
        String sysDB;

        SqlRowSet rowSet;
        // ------------------------------------------------
        sysDB = UtilDDS.getSysDbName(dbSys);

        if (sqlUserGroupPks.contains("'developer'") || sqlUserGroupPks.contains("'developers'")) {
            // -- 用户developer或组developers用户不受限制 --
            sqlMenu = "SELECT DISTINCT LEFT(pk, 3) FROM " + sysDB + ".sys_menu";
        }
        else {
            sqlMenu = "SELECT DISTINCT LEFT(menu_pk, 3) FROM sys_menu_acl WHERE user_group_pk IN (" + sqlUserGroupPks + ")";
        }
        sql = "SELECT pk, name, text FROM " + sysDB + ".sys_system WHERE pk IN (" + sqlMenu + ") AND flag_disabled = 0 ORDER BY sequence";

        rowSet = dbBus.getRowSet(sql);
        return rowSet;
    }
    public static SqlRowSet getMenuByUser(DBFactory dbSys, String systemKey, String sqlUserGroupPks) throws Exception {
        String sql, sqlMenu6, sqlMenu9, sqlMenu12;  // -- 暂定支持到3级菜单 --
        String busDB;

        SqlRowSet rowSet;
        // ------------------------------------------------
        busDB = UtilDDS.getTenantDbName(dbSys);

        if (sqlUserGroupPks.contains("'developer'") || sqlUserGroupPks.contains("'developers'")) {
            // -- 用户developer或组developers用户不受限制 --
            sqlMenu6 = "SELECT DISTINCT LEFT(pk, 6) FROM sys_menu";
            sqlMenu9 = "SELECT DISTINCT LEFT(pk, 9) FROM sys_menu";
            sqlMenu12 = "SELECT DISTINCT LEFT(pk, 12) FROM sys_menu";
        }
        else {
            sqlMenu6 = "SELECT DISTINCT LEFT(menu_pk, 6) FROM " + busDB + ".sys_menu_acl WHERE user_group_pk IN (" + sqlUserGroupPks + ")";
            sqlMenu9 = "SELECT DISTINCT LEFT(menu_pk, 9) FROM " + busDB + ".sys_menu_acl WHERE user_group_pk IN (" + sqlUserGroupPks + ")";
            sqlMenu12 = "SELECT DISTINCT LEFT(menu_pk, 12) FROM " + busDB + ".sys_menu_acl WHERE user_group_pk IN (" + sqlUserGroupPks + ")";
        }

        sql = "SELECT v.controller, m.* FROM sys_menu m LEFT JOIN sys_view v ON m.type_pk = v.pk "
            + "WHERE LEFT(m.pk, 3) = ? AND (m.pk IN (" + sqlMenu6 + ") OR m.pk IN (" + sqlMenu9 + ")  OR m.pk IN (" + sqlMenu12 + ") ) AND flag_disabled = 0 "
            + "ORDER BY m.sequence";
        rowSet = dbSys.getRowSet(sql, systemKey);
        return rowSet;
    }
}