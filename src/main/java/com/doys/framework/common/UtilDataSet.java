package com.doys.framework.common;
import com.doys.framework.config.Const;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
public class UtilDataSet {
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
                String columnType = Const.getFieldType(dataType);

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
    private static String getRsDataString(SqlRowSet rowSet) {
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

}
