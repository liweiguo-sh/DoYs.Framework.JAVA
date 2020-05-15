package com.doys.framework.config;
public class Const {
    public static String ok = "ok";

    public static String CHAR1 = "\1";        // -- 分隔符1 --
    public static String CHAR2 = "\2";        // -- 分隔符2 --
    public static String CHAR3 = "\3";        // -- 分隔符3 --
    public static String CHAR4 = "\4";        // -- 分隔符4 --
    public static String CHAR5 = "\5";        // -- 分隔符5 --
    public static String CHAR6 = "\6";        // -- 分隔符6, 框架保留分隔符, 程序员禁用. --
    public static String CHAR7 = "\7";        // -- 分隔符7, 框架保留分隔符, 程序员禁用. --

    public static String dateFormat = "yyyy-MM-dd";                  // -- 默认日期格式 --
    public static String timeFormat = "HH:mm:ss";                    // -- 默认时间格式 --
    public static String datetimeFormat = "yyyy-MM-dd HH:mm:ss";     // -- 默认日期时间格式 --
    public static String longDtFormat = "yyyy-MM-dd HH:mm:ss.SSS";   // -- 毫秒日期时间格式 --

    public static int MAX_PAGE_ROWS = 100;   // -- 视图每页数据行数 --
    // ------------------------------------------------------------------------
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
        else if (dataType.equals("numeric") || dataType.equals("decimal") || dataType.equals("float") || dataType.equals("real") || dataType.equals("money")) {
            fieldType = "number";
        }
        else if (dataType.indexOf("date") >= 0 || dataType.indexOf("time") >= 0) {
            fieldType = "datetime";
        }
        else if (dataType.equals("bit")) {
            fieldType = "boolean";
        }
        else {
            throw new Exception("com.xznext.Const.getFileType, Unknown dataType " + dataType);
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
}
