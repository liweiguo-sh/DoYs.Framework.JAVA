package com.doys.framework.core.db;
import com.doys.framework.core.ex.SessionTimeoutException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.sql.DataSource;
import java.util.Map;
import java.util.regex.Pattern;
public class DBFactory extends JdbcTemplate {
    private static String prefix;
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
        String valueString = _getValue(sql, "");
        return Long.parseLong(valueString);
    }

    public String getValue(String sql) throws Exception {
        return _getValue(sql, null, "");
    }
    public String getValue(String sql, Object... args) throws Exception {
        return _getValue(sql, "", args);
    }
    public String getValue(String sql, String defaultValue, Object... args) throws Exception {
        return _getValue(sql, defaultValue, args);
    }
    private String _getValue(String sql, String defaultValue, Object... args) throws Exception {
        Object objValue = _getObject(sql, args);
        if (objValue == null) {
            return defaultValue;
        }
        else {
            return objValue.toString();
        }
    }

    private Object _getObject(String sql, Object... args) throws Exception {
        Map<String, Object> map;

        sql = replaceSQL(sql);
        if (args.length > 0) {
            map = this.queryForMap(sql, args);
        }
        else {
            map = this.queryForMap(sql);
        }

        if (map.size() == 1) {
            for (Object value : map.values()) {
                return value;
            }
        }
        return null;
    }

    // -- Override(rename) base method ----------------------------------------
    public SqlRowSet getRowSet(String sql, Object... args) throws Exception {
        sql = replaceSQL(sql);
        writeSqlLog(-9, sql, args);

        return super.queryForRowSet(sql, args);
    }
    public int exec(String sql, Object... args) throws Exception {
        try {
            sql = replaceSQL(sql);
            int result = super.update(sql, args);
            writeSqlLog(result, sql, args);

        } catch (Exception e) {
            writeSqlLog(-1, sql, args);
            throw e;
        }

        return super.update(sql, args);
    }
    private void writeSqlLog(int result, String sql, Object... args) {
        for (int i = 0; i < args.length; i++) {
            sql = sql.replaceFirst("\\?", "'" + args[i].toString() + "'");
        }
        if (result >= -1) {
            // -- 不输出记录数 --
            logger.info(result + " => " + sql);
        }
        else {
            logger.info(sql);
        }
    }

    @Deprecated
    @Override
    public void execute(final String sql) throws DataAccessException {
        super.execute(sql);
    }

    // -- public static method ------------------------------------------------
    public static int getTenantId() throws Exception {
        try {
            return (int) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession().getAttribute("tenantId");
        } catch (NullPointerException e) {
            throw new SessionTimeoutException();
        } catch (Exception e) {
            throw e;
        }
    }
    public String getTenantDbName() throws Exception {
        if (prefix == null) {
            Map<String, Object> map = this.queryForMap("SELECT name FROM sys_database WHERE pk = 'prefix'");
            prefix = (String) map.get("name");
        }
        return prefix + getTenantId();
    }
    public String replaceSQL(String sql) throws Exception {
        return sql.replaceAll("\\.\\.", this.getTenantDbName() + ".");
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