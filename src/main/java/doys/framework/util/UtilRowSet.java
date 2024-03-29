package doys.framework.util;
import doys.framework.a0.Const;
import doys.framework.core.ex.CommonException;
import doys.framework.core.ex.UnexpectedException;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import java.util.ArrayList;
import java.util.HashMap;
public class UtilRowSet {
    public static String getRowSetString(SqlRowSet rowSet) throws Exception {
        return getRsFieldString(rowSet) + Const.CHAR7 + getRsDataString(rowSet);
    }
    private static String getRsFieldString(SqlRowSet rowSet) throws Exception {
        int columnCount = 0;

        SqlRowSetMetaData metaData = null;
        StringBuilder builder = new StringBuilder();
        try {
            metaData = rowSet.getMetaData();
            columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String dataType = metaData.getColumnTypeName(i);
                String columnType = UtilRowSet.getFieldType(dataType);

                if (i > 1) {
                    builder.append(Const.CHAR1);
                }
                String name = metaData.getColumnLabel(i).toLowerCase();
                int nameLen = name.length();

                builder.append("name" + Const.CHAR3 + metaData.getColumnLabel(i).toLowerCase());
                // -- TODO：使用AS转换别名后, getColumnName方法在MySQL数据库中得到的是原始字段名称 --
                // -- sbField.append("name" + Const.CHAR3 + rsmd.getColumnName(i).toLowerCase()); --
                builder.append(Const.CHAR2 + "dataType" + Const.CHAR3 + dataType);
                builder.append(Const.CHAR2 + "columnType" + Const.CHAR3 + columnType);
            }
        } catch (Exception e) {
            throw e;
        }
        return builder.toString();
    }
    private static String getRsDataString(SqlRowSet rowSet) {
        int rowCount = 0;
        int columnCount = 0;
        StringBuilder builder = new StringBuilder();
        // ------------------------------------------------
        try {
            columnCount = rowSet.getMetaData().getColumnCount();
            rowSet.beforeFirst();
            while (rowSet.next()) {
                if (rowCount++ > 0)
                    builder.append(Const.CHAR1);
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1)
                        builder.append(Const.CHAR2);
                    if (rowSet.getString(i) != null) {
                        builder.append(rowSet.getString(i));
                    }
                }
            }
            return builder.toString();
        } catch (Exception e) {
            throw e;
        }
    }

    public static String getFieldType(String dataType) throws Exception {
        String fieldType = "unknown";
        // ------------------------------------------------
        dataType = dataType.toLowerCase();
        if (dataType.indexOf("char") >= 0 || dataType.indexOf("text") >= 0) {
            fieldType = "string";
        }
        else if (dataType.equals("int") || dataType.equals("integer") || dataType.equals("tinyint") || dataType.equals("smallint") || dataType.equals("bigint") || dataType.equals("number")) {
            fieldType = "number";
        }
        else if (dataType.equals("numeric") || dataType.equals("decimal") || dataType.equals("double") || dataType.equals("float") || dataType.equals("real") || dataType.equals("money")) {
            fieldType = "number";
        }
        else if (dataType.indexOf("date") >= 0 || dataType.indexOf("time") >= 0) {
            fieldType = "datetime";
        }
        else if (dataType.equals("bit")) {
            fieldType = "boolean";
        }
        else {
            throw new CommonException("com.xznext.Const.getFileType, Unknown dataType " + dataType);
        }
        return fieldType;
    }
    public static int getColumnWidth(String fieldType, String fieldText, int fieldLength) {
        int nWidth = 0, nWidthType = 0, nWidthText = 0, nWidthLength = 0;
        // ------------------------------------------------
        if (fieldType.equalsIgnoreCase("int")) {
            nWidthType = 80;
        }
        else if (fieldType.equalsIgnoreCase("number")) {
            nWidthType = 100;
        }
        else if (fieldType.equalsIgnoreCase("datetime")) {
            nWidthType = 80;
        }
        else if (fieldType.equalsIgnoreCase("string")) {
            nWidthLength = fieldLength;
        }
        else {
            System.out.println("com.xznext.Const.getColumnWidth: unknown fieldType " + fieldType + ".");
            nWidthType = 100;
        }
        // ------------------------------------------------
        for (int i = 0; i < fieldText.length(); i++) {
            int ascii = (int) fieldText.charAt(i);
            if (ascii <= 126) {
                nWidthText += 10;
            }
            else {
                nWidthText += 25;
            }
        }
        // ------------------------------------------------
        nWidth = Math.max(50, nWidthType);
        nWidth = Math.max(nWidth, nWidthText);
        nWidth = Math.max(nWidth, nWidthLength);
        nWidth = Math.min(nWidth, 300);

        return nWidth;
    }

    // -- sql -----------------------------------------------------------------
    public static String getSelectSql(SqlRowSet rowSet, String excludeFields) throws Exception {
        int columnCount, fieldCount;

        String fieldName;
        String[] fields = excludeFields.replaceAll(" ", "").split(",");
        StringBuilder builder = new StringBuilder();

        SqlRowSetMetaData rsmd = rowSet.getMetaData();
        // ------------------------------------------------
        fieldCount = fields.length;
        columnCount = rsmd.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            fieldName = rsmd.getColumnLabel(i);
            for (int j = 0; j < fieldCount; j++) {
                if (fieldName.equalsIgnoreCase(fields[j])) {
                    fieldName = "";
                    break;
                }
            }
            if (fieldName.equals("")) continue;

            builder.append(",").append(fieldName);
        }
        return builder.toString().substring(1);
    }

    // -- convertTo -----------------------------------------------------------
    public static ArrayList<HashMap<String, Object>> toArrayList(SqlRowSet rsData) throws Exception {
        int columnCount;

        String columnName, dataType, columnType;

        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        SqlRowSetMetaData rsmd = rsData.getMetaData();
        // ------------------------------------------------
        columnCount = rsmd.getColumnCount();
        while (rsData.next()) {
            HashMap<String, Object> map = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                columnName = rsmd.getColumnLabel(i);
                dataType = rsmd.getColumnTypeName(i);
                columnType = UtilRowSet.getFieldType(dataType);
                if (columnType.equals("number")) {
                    if (dataType.equals("INT") || dataType.equals("TINYINT") || dataType.equals("BIGINT")) {
                        map.put(columnName, rsData.getInt(i));
                    }
                    else if (dataType.equals("FLOAT")) {
                        map.put(columnName, rsData.getFloat(i));
                    }
                    else if (dataType.equals("DOUBLE") || dataType.equals("DECIMAL")) {
                        map.put(columnName, rsData.getDouble(i));
                    }
                    else {
                        map.put(columnName, rsData.getFloat(i));
                        throw new UnexpectedException("UtilRowSet.toArrayList::debug here");
                    }
                }
                else {
                    map.put(columnName, rsData.getString(i));
                }
            }
            list.add(map);
        }
        // ------------------------------------------------
        return list;
    }
    public static HashMap<String, Object> toHashMap(SqlRowSet rsData) throws Exception {
        int columnCount;

        String columnName, dataType, columnType;

        SqlRowSetMetaData rsmd = rsData.getMetaData();
        HashMap<String, Object> map = new HashMap<>();
        // ------------------------------------------------
        if (!rsData.next()) return map;

        columnCount = rsmd.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            columnName = rsmd.getColumnLabel(i);
            dataType = rsmd.getColumnTypeName(i);
            columnType = UtilRowSet.getFieldType(dataType);
            if (columnType.equals("number")) {
                if (dataType.equals("INT")) {
                    map.put(columnName, rsData.getInt(i));
                }
                else if (dataType.equals("TINYINT")) {
                    map.put(columnName, rsData.getInt(i) == 1);
                }
                else if (dataType.equals("BIGINT")) {
                    map.put(columnName, rsData.getLong(i) == 1);
                }
                else if (dataType.equals("FLOAT")) {
                    map.put(columnName, rsData.getFloat(i));
                }
                else if (dataType.equals("DOUBLE") || dataType.equals("DECIMAL")) {
                    map.put(columnName, rsData.getDouble(i));
                }
                else {
                    throw new UnexpectedException();
                }
            }
            else {
                map.put(columnName, rsData.getString(i));
            }
        }

        if (rsData.next()) {
            throw new CommonException("UtilRowSet.toHashMap仅适用于单条记录的情况，请检查。");
        }
        // ------------------------------------------------
        return map;
    }
    public static HashMap<String, Object> toHashMap(SqlRowSet rsData, String keyField, String valueField) throws Exception {
        int columnCount;

        String columnName, dataType = "", columnType = "";
        String keyValue;

        SqlRowSetMetaData rsmd = rsData.getMetaData();
        HashMap<String, Object> map = new HashMap<>();
        // ------------------------------------------------
        columnCount = rsmd.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            columnName = rsmd.getColumnLabel(i);
            if (columnName.equalsIgnoreCase(valueField)) {
                dataType = rsmd.getColumnTypeName(i);
                columnType = UtilRowSet.getFieldType(dataType);
                break;
            }
        }

        // ------------------------------------------------
        rsData.beforeFirst();
        while (rsData.next()) {
            keyValue = rsData.getString(keyField);
            if (columnType.equals("number")) {
                if (dataType.equals("INT")) {
                    map.put(keyValue, rsData.getInt(valueField));
                }
                else if (dataType.equals("TINYINT")) {
                    map.put(keyValue, rsData.getInt(valueField) == 1);
                }
                else if (dataType.equals("BIGINT")) {
                    map.put(keyValue, rsData.getLong(valueField));
                }
                else if (dataType.equals("FLOAT")) {
                    map.put(keyValue, rsData.getFloat(valueField));
                }
                else if (dataType.equals("DOUBLE") || dataType.equals("DECIMAL")) {
                    map.put(keyValue, rsData.getDouble(valueField));
                }
                else {
                    throw new UnexpectedException();
                }
            }
            else {
                map.put(keyValue, rsData.getString(valueField));
            }
        }
        // ------------------------------------------------
        return map;
    }
}