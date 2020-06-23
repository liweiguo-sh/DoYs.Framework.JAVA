package com.doys.framework.system.service;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.database.DBFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class MenuService extends BaseService {
    public static SqlRowSet getSystem(DBFactory dbSys, String userkey) throws Exception {
        String sql = "";

        SqlRowSet rowSet = null;
        // ------------------------------------------------
        try {
            sql = "SELECT pk, name, text FROM sys_system ORDER BY sequence";
            rowSet = dbSys.getRowSet(sql);
            return rowSet;
        } catch (Exception e) {
            throw e;
        } finally {
        }
    }
    public static SqlRowSet getMenuByUser(DBFactory dbSys, String systemKey, String userkey) throws Exception {
        String sql = "";

        SqlRowSet rowSet = null;
        // ------------------------------------------------
        sql = "SELECT v.controller, m.* "
            + "FROM sys_menu m LEFT JOIN sys_view v ON m.type_pk = v.pk "
            + "WHERE LEFT(m.pk, 3) = ? ORDER BY m.sequence";
        rowSet = dbSys.getRowSet(sql, systemKey);
        return rowSet;
    }
}