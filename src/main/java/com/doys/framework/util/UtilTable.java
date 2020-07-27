package com.doys.framework.util;
import com.doys.framework.common.entity.EntityTableField;
import com.doys.framework.database.DBFactory;
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