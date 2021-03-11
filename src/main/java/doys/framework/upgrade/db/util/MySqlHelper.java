package doys.framework.upgrade.db.util;
import doys.framework.core.ex.CommonException;
import doys.framework.core.ex.UnImplementException;
import doys.framework.database.DBFactory;
import doys.framework.upgrade.db.enum1.EntityIndexType;
import doys.framework.upgrade.db.obj.EntityField;
import doys.framework.util.UtilYml;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MySqlHelper {
    public static boolean hasTable(DBFactory dbBus, String tableName) throws Exception {
        String sql = "SELECT COUNT(1) FROM information_schema.tables "
            + "WHERE table_schema = (SELECT database()) AND table_name = ?";
        if (dbBus.getInt(sql, 0, tableName) == 1) {
            return true;
        }
        return false;
    }

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


    public static boolean hasColumn(DBFactory dbBus, String tableName, String columnName) throws Exception {
        String sql = "SELECT COUNT(1) FROM information_schema.columns "
            + "WHERE table_schema = (SELECT database()) AND table_name = ? AND column_name = ?";
        if (dbBus.getInt(sql, 0, tableName, columnName) == 1) {
            return true;
        }
        return false;
    }
    public static void addColumn(DBFactory dbBus, String tableName, EntityField field) throws Exception {
        String sql = "ALTER TABLE " + tableName + " ADD " + field.name + " " + field.getCreateColumnSql(true);
        dbBus.exec(sql);
    }
    public static void dropColumn(DBFactory dbBus, String tableName, String columnName) throws Exception {
        String sql = "ALTER TABLE " + tableName + " DROP COLUMN " + columnName;
        dbBus.exec(sql);
    }
    public static void disableColumn(DBFactory dbBus, String tableName, String columnName, String columnType) throws Exception {
        String sql = "";
        String columnNameMock = "", prefixMock = "__$$__";
        String columnComment;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (true) {
            // -- TODO: 暂定只追加修改字段，不删除字段。待数据库实体类双向同步功能完成后再启用。启用时增加恢复删除功能和无数据直接删除 --
            return;
        }
        // -- 1. 模拟删除 --
        if (!columnName.startsWith(prefixMock)) {
            columnNameMock = prefixMock + columnName;
            columnComment = LocalDateTime.now().format(formatter) + prefixMock + "升级程序模拟删除注释，请勿改动";
            sql = "ALTER TABLE " + tableName + " CHANGE COLUMN " + columnName + " " + columnNameMock + " " + columnType + " DEFAULT NULL COMMENT '" + columnComment + "'";
            dbBus.exec(sql);
            return;
        }

        // -- 2. 物理删除 --
        int nIdx = columnComment.indexOf(prefixMock);
        if (nIdx < 8) {
            throw new CommonException("注释不符合框架规范，已被人为改动，请检查。");
        }
        String dtUpgradeStr = columnComment.substring(0, nIdx);
        LocalDate dtUpgrade = LocalDate.parse(dtUpgradeStr, formatter);
        long days = LocalDate.now().toEpochDay() - dtUpgrade.toEpochDay();
        long maxDays = UtilYml.getInt("deleteFieldKeepDays");
        if (days >= maxDays) {
            dropColumn(dbBus, tableName, columnName);
        }
    }

    /**
     * @param tableName 表名称
     * @return 返回表指定索引类型的索引数组
     */
    public static ArrayList<String[]> getIndex(JdbcTemplate jdbcTemplate, String databaseName, String tableName, EntityIndexType indexType) throws Exception {
        ArrayList<String[]> alIndex = new ArrayList<>();
        String indexName = "", indexFields = "", columnName = "";

        String sql = "SELECT index_name, column_name FROM INFORMATION_SCHEMA.STATISTICS "
            + "WHERE table_schema = ? AND table_name = ? AND index_name <> 'PRIMARY' AND non_unique = " + (indexType == EntityIndexType.UNIQUE_INDEX ? 0 : 1) + " "
            + "ORDER BY index_name, seq_in_index";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, databaseName, tableName);
        while (rowSet.next()) {
            columnName = rowSet.getString("column_name");
            if (indexName.equals(rowSet.getString("index_name"))) {
                indexFields += "," + columnName;
            }
            else {
                if (!indexFields.equals("")) {
                    alIndex.add(new String[] { indexName, indexFields });
                }
                indexName = rowSet.getString("index_name");
                indexFields = columnName;
            }
        }
        if (!indexFields.equals("")) {
            alIndex.add(new String[] { indexName, indexFields });
        }

        return alIndex;
    }
    public static void dropIndex(JdbcTemplate jdbcTemplate, String tableName, EntityIndexType indexType, String indexName) throws Exception {
        String sql;
        if (indexType == EntityIndexType.PRIMARY) {
            sql = "ALTER TABLE " + tableName + " DROP PRIMARY KEY";
        }
        else {
            sql = "ALTER TABLE " + tableName + " DROP INDEX " + indexName;
        }
        System.out.println(sql);
        jdbcTemplate.execute(sql);
    }
    public static void addIndex(JdbcTemplate jdbcTemplate, String tableName, EntityIndexType indexType, String indexName, String indexFields) throws Exception {
        String sql = "";
        if (indexType == EntityIndexType.PRIMARY) {
            sql = "ALTER TABLE " + tableName + " ADD PRIMARY KEY(" + indexFields + ")";
        }
        else if (indexType == EntityIndexType.UNIQUE_INDEX) {
            sql = "ALTER TABLE " + tableName + " ADD UNIQUE INDEX " + indexName + "(" + indexFields + ")";
        }
        else if (indexType == EntityIndexType.INDEX) {
            sql = "ALTER TABLE " + tableName + " ADD INDEX " + indexName + "(" + indexFields + ")";
        }
        else {
            throw new CommonException("unknown index type: " + indexType);
        }

        System.out.println(sql);
        jdbcTemplate.execute(sql);
    }
    public static void updateColumnLength(DBFactory dbBus, String tableName, String columnName, String datatype, int columnLength) throws Exception {
        String sql, sqlDatatype;
        // ------------------------------------------------
        if (datatype.equalsIgnoreCase("string")) {
            datatype = "varchar";
        }

        if (datatype.equalsIgnoreCase("varchar")) {
            sqlDatatype = "varchar(" + columnLength + ")";
        }
        else {
            throw new UnImplementException();
        }
        // ------------------------------------------------
        sql = "ALTER TABLE " + tableName + " MODIFY COLUMN " + columnName + " " + sqlDatatype;
        dbBus.exec(sql);
    }

    // -- common --------------------------------------------------------------
}