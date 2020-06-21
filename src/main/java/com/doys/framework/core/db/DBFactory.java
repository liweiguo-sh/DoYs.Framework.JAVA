package com.doys.framework.core.db;
import com.doys.framework.core.ex.SessionTimeoutException;
import com.doys.framework.util.UtilDate;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
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
    public int getInt(String sql) throws Exception {
        return _getInt(sql);
    }
    public int getInt(String sql, Object... args) throws Exception {
        return _getInt(sql, args);
    }
    private int _getInt(String sql, Object... args) throws Exception {
        String valueString = _getValue(sql, args);
        return Integer.parseInt(valueString);
    }

    public long getLong(String sql) throws Exception {
        return _getLong(sql);
    }
    private long _getLong(String sql) throws Exception {
        String valueString = _getValue(sql);
        return Long.parseLong(valueString);
    }

    public String getValue(String sql) throws Exception {
        return _getValue(sql);
    }
    public String getValue(String sql, Object... args) throws Exception {
        return _getValue(sql, args);
    }
    private String _getValue(String sql, Object... args) throws Exception {
        Object objValue = _getObject(sql, args);
        if (objValue == null) {
            return "";
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
        LocalDateTime startTime = LocalDateTime.now();
        SqlRowSet rowSet;
        // ------------------------------------------------
        try {
            sql = replaceSQL(sql);
            rowSet = super.queryForRowSet(sql, args);
            writeSqlLog(-9, UtilDate.getDateTimeDiff(startTime), sql, args);
        } catch (Exception e) {
            writeSqlLog(-1, UtilDate.getDateTimeDiff(startTime), sql, args);
            throw e;
        }
        return rowSet;
    }
    public int exec(String sql, Object... args) throws Exception {
        int result = 0;
        LocalDateTime startTime = LocalDateTime.now();
        // ------------------------------------------------
        try {
            sql = replaceSQL(sql);
            result = super.update(sql, args);
            writeSqlLog(result, UtilDate.getDateTimeDiff(startTime), sql, args);
        } catch (DuplicateKeyException e) {
            writeSqlLog(-1, UtilDate.getDateTimeDiff(startTime), sql, args);
            //throw new Exception("录入数据冲突，关键信息重复，请检查。 \r\n" + e.getCause().getLocalizedMessage());
            throw new Exception("录入数据冲突，关键信息重复，请检查。");
        } catch (Exception e) {
            writeSqlLog(-1, UtilDate.getDateTimeDiff(startTime), sql, args);
            throw e;
        }
        return result;
    }
    public int[] batchUpdate(String sql, List<Object[]> batchArgs) throws DataAccessException {
        int[] result = null;
        long nInterval;

        LocalDateTime startTime = LocalDateTime.now();
        // ------------------------------------------------
        try {
            sql = replaceSQL(sql);
            result = super.batchUpdate(sql, batchArgs);

            nInterval = UtilDate.getDateTimeDiff(startTime);
            writeSqlLog(result.length, UtilDate.getDateTimeDiff(startTime), sql, new Object[] {});
        } catch (DataAccessException e) {
            writeSqlLog(-1, UtilDate.getDateTimeDiff(startTime), sql, new Object[] {});
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void writeSqlLog(int result, long interval, String sql, Object... args) {
        for (int i = 0; i < args.length; i++) {
            sql = sql.replaceFirst("\\?", "'" + args[i].toString() + "'");
        }

        if (result >= -1) {
            if (interval == 0) {
                logger.info(result + " => " + sql);
            }
            else {
                logger.info(result + "(" + interval + "ms)" + " => " + sql);
            }
        }
        else {
            if (interval == 0) {
                logger.info(sql);
            }
            else {
                logger.info(interval + "ms => " + sql);
            }
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
        if (sql.indexOf("..") >= 0) {
            return sql.replaceAll("\\.\\.", this.getTenantDbName() + ".");
        }
        else {
            return sql;
        }
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