package com.doys.framework.system.service;
import com.doys.framework.core.base.BaseService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class MenuService extends BaseService {
    public static SqlRowSet getSystem(JdbcTemplate jtSys, String userkey) throws Exception {
        String sql = "";

        SqlRowSet rowSet = null;
        // ------------------------------------------------
        try {
            sql = "SELECT pk, name, text FROM sys_system ORDER BY sequence";
            rowSet = jtSys.queryForRowSet(sql);
            return rowSet;
        } catch (Exception e) {
            throw e;
        } finally {
        }
    }
    public static SqlRowSet getMenuByUser(JdbcTemplate jtSys, String systemKey, String userkey) throws Exception {
        String sql = "";

        SqlRowSet rowSet = null;
        // ------------------------------------------------
        try {
            sql = "SELECT * FROM sys_menu WHERE LEFT(pk, 3) = ? ORDER BY sequence";
            rowSet = jtSys.queryForRowSet(sql, systemKey);
            return rowSet;
        } catch (Exception e) {
            throw e;
        } finally {
        }
    }
}