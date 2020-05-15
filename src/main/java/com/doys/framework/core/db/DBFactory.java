package com.doys.framework.core.db;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
public class DBFactory extends JdbcTemplate {
    public DBFactory(DataSource dataSource) {
        setDataSource(dataSource);
        afterPropertiesSet();
    }

    // -- DataTable -----------------------------------------------------------
    public DataTable getDataTable(String sql) throws Exception {
        return getDataTable(sql, new Object[] {});
    }
    public DataTable getDataTable(String sql, Object[] parameters) throws Exception {
        DataTable dtb = new DataTable(this, sql, parameters);
        return dtb;
    }

    // -- getValue ------------------------------------------------------------
    public int getInt(String sql) throws Exception {
        return _getInt(sql);
    }
    private int _getInt(String sql) throws Exception {
        String valueString = _getValue(sql, null, "");
        return Integer.parseInt(valueString);
    }

    public String getValue(String sql) throws Exception {
        return _getValue(sql, null, "");
    }
    public String getValue(String sql, Object[] parameters) throws Exception {
        return _getValue(sql, parameters, "");
    }
    public String getValue(String sql, Object[] parameters, String defaultValue) throws Exception {
        return _getValue(sql, parameters, defaultValue);
    }
    private String _getValue(String sql, Object[] parameters, String defaultValue) throws Exception {
        String returnString = null;
        SqlRowSet rs = this.queryForRowSet(sql, parameters);
        if (rs.next()) {
            returnString = rs.getString(1);
            if (returnString == null) {
                returnString = defaultValue;
            }
        }
        return returnString;
    }
}