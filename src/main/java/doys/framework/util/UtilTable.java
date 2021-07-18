package doys.framework.util;
import doys.framework.a2.structure.EntityTableField;
import doys.framework.database.DBFactory;
public class UtilTable {
    public static void addOrUpdateField(DBFactory dbSys, String databaseName, String tableName, EntityTableField entity) {

    }

    public static boolean isValidColumnName(String columnName) {
        columnName = columnName.trim();
        // ------------------------------------------------
        if (columnName.length() == 0) {
            return false;
        }
        if (columnName.contains(".")) {
            return false;
        }
        // ------------------------------------------------
        return true;
    }
}