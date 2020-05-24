package com.doys.framework.core.db;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.regex.Pattern;
public class DBFactory extends JdbcTemplate {
    private static String regInject = "(\\b(delete|update|insert|drop|truncate|alter|exec|execute)\\b)";
    private static Pattern sqlPattern = Pattern.compile(regInject, Pattern.CASE_INSENSITIVE);

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
    public long getLong(String sql) throws Exception {
        return _getLong(sql);
    }
    private long _getLong(String sql) throws Exception {
        String valueString = _getValue(sql, null, "");
        return Long.parseLong(valueString);
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

    // -- Check SQL injection -------------------------------------------------
    public static boolean checkSqlInjection(String sqlStatement) {
        // ------------------------------------------------
        try {
            if (sqlPattern.matcher(sqlStatement).find()) {
                System.out.println("Suspicious SQL injection statement was found.");
                // -- TODO：有待测试验证 --
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
        }
        // ------------------------------------------------
        return true;
    }
}