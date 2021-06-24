package doys.framework.util;

import doys.framework.a0.Const;
import doys.framework.core.ex.CommonException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
public class UtilResultSet {
    public static String getRowSetString(ResultSet rs) throws Exception {
        return getRsFieldString(rs) + Const.CHAR7 + getRsDataString(rs);
    }
    private static String getRsFieldString(ResultSet rs) throws Exception {
        int columnCount = 0;

        ResultSetMetaData metaData = null;
        StringBuilder builder = new StringBuilder();
        try {
            metaData = rs.getMetaData();
            columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String dataType = metaData.getColumnTypeName(i);
                String columnType = UtilResultSet.getFieldType(dataType);

                if (i > 1) {
                    builder.append(Const.CHAR1);
                }
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
    private static String getRsDataString(ResultSet rowSet) {
        int rowCount = 0;
        int columnCount = 0;
        StringBuilder builder = new StringBuilder();
        try {
            columnCount = rowSet.getMetaData().getColumnCount();
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
            e.printStackTrace();
            return "";
        }
    }

    public static String getFieldType(String dataType) throws Exception {
        String fieldType = "unknown";
        // ------------------------------------------------
        dataType = dataType.toLowerCase();
        if (dataType.indexOf("char") >= 0 || dataType.indexOf("text") >= 0) {
            fieldType = "string";
        }
        else if (dataType.equals("int") || dataType.equals("integer") || dataType.equals("smallint") || dataType.equals("bigint") || dataType.equals("number")) {
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
}