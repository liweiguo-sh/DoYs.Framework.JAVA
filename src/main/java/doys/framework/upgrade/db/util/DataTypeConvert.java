package doys.framework.upgrade.db.util;
import doys.framework.core.ex.CommonException;
import doys.framework.upgrade.db.enumeration.EntityFieldType;
public class DataTypeConvert {
    /**
     * @param filedType 实体数据类型, 例如：String、int、java.lang.Integer、long等
     * @return 返回物理数据库对应的字段类型，varchar、int、bigint等
     * @throws Exception
     */
    public static String getColumnType(EntityFieldType filedType) throws Exception {
        String columnType;

        if (filedType == EntityFieldType.STRING) {
            columnType = "varchar";
        }
        else if (filedType == EntityFieldType.INT) {
            columnType = "int";
        }
        else if (filedType == EntityFieldType.TINYINT) {
            columnType = "tinyint";
        }
        else if (filedType == EntityFieldType.LONG) {
            columnType = "bigint";
        }
        else if (filedType == EntityFieldType.FLOAT) {
            columnType = "float";
        }
        else if (filedType == EntityFieldType.DOUBLE) {
            columnType = "double";
        }
        else if (filedType == EntityFieldType.DECIMAL) {
            columnType = "decimal";
        }
        else if (filedType == EntityFieldType.DATETIME) {
            columnType = "datetime";
        }
        else if (filedType == EntityFieldType.DATE) {
            columnType = "date";
        }
        else if (filedType == EntityFieldType.TIME) {
            columnType = "time";
        }
        else if (filedType == EntityFieldType.TEXT) {
            columnType = "text";
        }
        else {
            throw new CommonException("unknown entity field type " + filedType + ", please complete the code.");
        }
        return columnType;
    }
}