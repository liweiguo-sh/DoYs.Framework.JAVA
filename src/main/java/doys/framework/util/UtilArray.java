package doys.framework.util;

import java.util.ArrayList;
public class UtilArray {
    public static String arrayJoin(String[] arrString, String symbol) throws Exception {
        int len = arrString.length;

        StringBuilder builder;
        // ------------------------------------------------
        if (len == 0) {
            return "";
        }
        else if (len == 1) {
            return arrString[0];
        }
        else {
            builder = new StringBuilder(len + 1);
            builder.append(arrString[0]);
            for (int i = 1; i < len; i++) {
                builder.append(symbol + arrString[i]);
            }
            return builder.toString();
        }
    }
    public static String arrayJoin(ArrayList<String> list, String symbol) throws Exception {
        int len = list.size();

        StringBuilder builder;
        // ------------------------------------------------
        if (len == 0) {
            return "";
        }
        else if (len == 1) {
            return list.get(0);
        }
        else {
            builder = new StringBuilder(len + 1);
            builder.append(list.get(0));
            for (int i = 1; i < len; i++) {
                builder.append(symbol + list.get(i));
            }
            return builder.toString();
        }
    }

    public static boolean itemInArray(String[] array, String item) throws Exception {
        String value;
        // ------------------------------------------------
        for (int i = array.length - 1; i >= 0; i--) {
            value = array[i];
            if (value.equalsIgnoreCase(item)) {
                return true;
            }
        }
        // ------------------------------------------------
        return false;
    }

    public static String[][] arrayListToArray(ArrayList<ArrayList<Object>> list) {
        int rowCount = list.size();
        if (rowCount == 0) {
            return new String[0][0];
        }
        int colCount = list.get(0).size();
        String[][] arr = new String[rowCount][colCount];

        ArrayList<Object> listRow;
        for (int iRow = 0; iRow < rowCount; iRow++) {
            listRow = list.get(iRow);
            for (int iCol = 0; iCol < colCount; iCol++) {
                arr[iRow][iCol] = listRow.get(iCol).toString();
            }
        }
        return arr;
    }
}