package com.doys.framework.upgrade.db.util;

import com.doys.framework.upgrade.db.enum1.EntityIndexType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * MySql 系统库助手
 */
public class MySqlSysHelper {
    private static Logger log = LoggerFactory.getLogger("MySqlSysHelper");
    private static Period Periodbe;

    /**
     * @param tableName 表名称
     * @return 返回表主键字段
     */
    public static String getPrimaryKey(JdbcTemplate jdbcTemplate, String tableName) throws Exception {
        String primaryKey = "";

        String sql = "SELECT column_name FROM INFORMATION_SCHEMA.STATISTICS WHERE table_schema=(select database()) and table_name = '" + tableName + "' AND index_name = 'PRIMARY' ORDER BY seq_in_index";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while (rowSet.next()) {
            primaryKey += "," + rowSet.getString("column_name");
        }

        if (!primaryKey.equals("")) {
            primaryKey = primaryKey.substring(1);
        }
        return primaryKey;
    }

    /**
     * @param tableName 表名称
     * @return 返回表指定索引类型的索引数组
     */
    public static ArrayList<String[]> getIndex(JdbcTemplate jdbcTemplate, String tableName, EntityIndexType indexType) throws Exception {
        ArrayList<String[]> alIndex = new ArrayList<>();
        String indexName = "", indexFields = "", columnName = "";

        String sql = "SELECT index_name, column_name FROM INFORMATION_SCHEMA.STATISTICS "
                + "WHERE table_name = '" + tableName + "' AND index_name <> 'PRIMARY' AND non_unique = " + (indexType == EntityIndexType.UNIQUE_INDEX ? 0 : 1) + " "
                + "ORDER BY index_name, seq_in_index";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while (rowSet.next()) {
            columnName = rowSet.getString("column_name");
            if (indexName.equals(rowSet.getString("index_name"))) {
                indexFields += "," + columnName;
            } else {
                if (!indexFields.equals("")) {
                    alIndex.add(new String[]{indexName, indexFields});
                }
                indexName = rowSet.getString("index_name");
                indexFields = columnName;
            }
        }
        if (!indexFields.equals("")) {
            alIndex.add(new String[]{indexName, indexFields});
        }

        return alIndex;
    }

    public static void dropIndex(JdbcTemplate jdbcTemplate, String tableName, EntityIndexType indexType, String indexName) throws Exception {
        String sql = "";
        if (indexType == EntityIndexType.PRIMARY) {
            sql = "ALTER TABLE " + tableName + " DROP PRIMARY KEY";
        } else {
            sql = "ALTER TABLE " + tableName + " DROP INDEX " + indexName;
        }
        System.out.println(sql);
        jdbcTemplate.execute(sql);
    }

    public static void addIndex(JdbcTemplate jdbcTemplate, String tableName, EntityIndexType indexType, String indexName, String indexFields) throws Exception {
        String sql = "";
        if (indexType == EntityIndexType.PRIMARY) {
            sql = "ALTER TABLE " + tableName + " ADD PRIMARY KEY(" + indexFields + ")";
        } else if (indexType == EntityIndexType.UNIQUE_INDEX) {
            sql = "ALTER TABLE " + tableName + " ADD UNIQUE INDEX " + indexName + "(" + indexFields + ")";
        } else if (indexType == EntityIndexType.INDEX) {
            sql = "ALTER TABLE " + tableName + " ADD INDEX " + indexName + "(" + indexFields + ")";
        } else {
            throw new Exception("unknown index type: " + indexType);
        }

        jdbcTemplate.execute(sql);
    }

    public static void dropColumn(JdbcTemplate jdbcTemplate, String tableName, String columnName) throws Exception {
        String sql = "";

        sql = "ALTER TABLE " + tableName + " DROP COLUMN " + columnName;
        System.out.println(sql);
        jdbcTemplate.execute(sql);
    }

    public static void disableColumn(JdbcTemplate jdbcTemplate, String tableName, String columnName, String columnType, String columnComment) throws Exception {
        long maxDays = 30;

        String sql = "";
        String columnNameMock = "", prefixMock = "__$$__";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // -- 1. 模拟删除 --
        if (!columnName.startsWith(prefixMock)) {
            columnNameMock = prefixMock + columnName;
            columnComment = LocalDateTime.now().format(formatter) + prefixMock + "升级程序模拟删除注释，请勿改动";
            sql = "ALTER TABLE " + tableName + " CHANGE COLUMN " + columnName + " " + columnNameMock + " " + columnType + " DEFAULT NULL COMMENT '" + columnComment + "'";
            System.out.println(sql);
            jdbcTemplate.execute(sql);
            return;
        }

        // -- 2. 物理删除 --
        int nIdx = columnComment.indexOf(prefixMock);
        if (nIdx < 8) {
            throw new Exception("注释不符合框架规范，已被人为改动，请检查。");
        }
        String dtUpgradeStr = columnComment.substring(0, nIdx);
        LocalDate dtUpgrade = LocalDate.parse(dtUpgradeStr, formatter);
        long days = LocalDate.now().toEpochDay() - dtUpgrade.toEpochDay();
        if (days > maxDays) {
            dropColumn(jdbcTemplate, tableName, columnName);
        }
    }
}