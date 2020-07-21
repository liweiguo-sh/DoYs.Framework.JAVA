package com.doys.framework.database;
import com.doys.framework.database.dtb.DataTable;
import com.doys.framework.util.UtilDate;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

public class DBFactory extends JdbcTemplate {
    private static String regInject = "(\\b(delete|update|insert|drop|truncate|alter|exec|execute)\\b)";
    private static Pattern sqlPattern = Pattern.compile(regInject, Pattern.CASE_INSENSITIVE);
    public static int NULL_NUMBER = -31415926;

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
        return _getInt(sql, NULL_NUMBER);
    }
    public int getInt(String sql, int defaultValue) throws Exception {
        return _getInt(sql, defaultValue);
    }
    public int getInt(String sql, int defaultValue, Object... args) throws Exception {
        return _getInt(sql, defaultValue, args);
    }
    private int _getInt(String sql, int defaultValue, Object... args) throws Exception {
        String valueString = _getValue(sql, null, args);
        if (valueString == null) {
            if (defaultValue == NULL_NUMBER) {
                throw new Exception("记录不存在，请检查。");
            }
            else {
                return defaultValue;
            }
        }
        return Integer.parseInt(valueString);
    }

    public long getLong(String sql) throws Exception {
        return _getLong(sql, 0);
    }
    public long getLong(String sql, long defaultValue) throws Exception {
        return _getLong(sql, defaultValue);
    }
    public long getLong(String sql, long defaultValue, Object... args) throws Exception {
        return _getLong(sql, defaultValue, args);
    }
    private long _getLong(String sql, long defaultValue, Object... args) throws Exception {
        String valueString = _getValue(sql, null, args);
        if (valueString == null) {
            if (defaultValue == NULL_NUMBER) {
                throw new Exception("记录不存在，请检查。");
            }
            else {
                return defaultValue;
            }
        }
        return Long.parseLong(valueString);
    }

    public String getValue(String sql) throws Exception {
        return _getValue(sql, null);
    }
    public String getValue(String sql, Object defaultValue) throws Exception {
        return _getValue(sql, defaultValue);
    }
    public String getValue(String sql, Object defaultValue, Object... args) throws Exception {
        return _getValue(sql, defaultValue, args);
    }
    private String _getValue(String sql, Object defaultValue, Object... args) throws Exception {
        Object objValue = _getObject(sql, defaultValue, args);
        if (objValue == null) {
            return null;
        }
        else {
            return objValue.toString();
        }
    }

    private Object _getObject(String sql, Object defaultValue, Object... args) throws Exception {
        Object objectReturn = null;

        SqlRowSet rs;
        LocalDateTime startTime = LocalDateTime.now();
        // ------------------------------------------------
        try {
            sql = replaceSQL(sql);
            if (args.length > 0) {
                rs = this.getRowSet(sql, args);
            }
            else {
                rs = this.getRowSet(sql);
            }

            if (rs.next()) {
                objectReturn = rs.getObject(1);
                if (objectReturn == null) {
                    objectReturn = defaultValue;
                }
            }
            else {
                objectReturn = defaultValue;
            }
        } catch (Exception e) {
            throw e;
        }
        return objectReturn;
    }

    // -- Override(rename) base method ----------------------------------------
    public SqlRowSet getRowSet(String sql, Object... args) throws Exception {
        LocalDateTime startTime = LocalDateTime.now();
        SqlRowSet rowSet;
        // ------------------------------------------------
        try {
            sql = replaceSQL(sql);
            rowSet = super.queryForRowSet(sql, args);
            writeSqlInfo(-9, startTime, sql, args);
        } catch (Exception e) {
            writeSqlErr(-1, startTime, sql, args);
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
            writeSqlInfo(result, startTime, sql, args);
        } catch (DuplicateKeyException e) {
            writeSqlErr(-1, startTime, sql, args);
            throw new Exception("录入数据冲突，关键信息重复，请检查。");
        } catch (Exception e) {
            writeSqlErr(-1, startTime, sql, args);
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
            writeSqlInfo(result.length, startTime, sql, new Object[] {});
        } catch (DataAccessException e) {
            writeSqlErr(-1, startTime, sql, new Object[] {});
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void writeSqlInfo(int result, LocalDateTime startTime, String sql, Object... args) {
        String logString = getSqlLog(result, startTime, sql, args);
        logger.info(logString);
    }
    private void writeSqlErr(int result, LocalDateTime startTime, String sql, Object... args) {
        String logString = getSqlLog(result, startTime, sql, args);
        logger.error(logString);
    }
    private String getSqlLog(int result, LocalDateTime startTime, String sql, Object... args) {
        long interval = UtilDate.getDateTimeDiff(startTime);

        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                sql = sql.replaceFirst("\\?", "null");
            }
            else {
                sql = sql.replaceFirst("\\?", "'" + args[i].toString() + "'");
            }
        }

        if (result >= -1) {
            return result + "(" + interval + "ms)" + " => " + sql;
        }
        else {
            return interval + "ms => " + sql;
        }
    }

    @Deprecated
    @Override
    public void execute(final String sql) throws DataAccessException {
        super.execute(sql);
    }

    // -- public static method ------------------------------------------------
    public String replaceSQL(String sql) throws Exception {
        return sql;
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