package com.doys.framework.common;
import java.math.BigDecimal;
public class UtilMath {
    // -- math 四则运算 -----------------------------------------------------------
    public static String BigDecimalFour(String strNum1, String strNum2, String symbol) throws Exception {
        String strResult = "";

        BigDecimal numResult = null;
        // ------------------------------------------------
        if (symbol.equals("+")) {
            numResult = new BigDecimal(strNum1).add(new BigDecimal(strNum2));
        }
        else if (symbol.equals("-")) {
            numResult = new BigDecimal(strNum1).subtract(new BigDecimal(strNum2));
        }
        else if (symbol.equals("*")) {
            numResult = new BigDecimal(strNum1).multiply(new BigDecimal(strNum2));
        }
        else if (symbol.equals("/")) {
            numResult = new BigDecimal(strNum1).divide(new BigDecimal(strNum2));
        }
        else {
            throw new Exception("未实现的运算符 " + symbol);
        }
        // ------------------------------------------------
        strResult = numResult.toString();
        return strResult;
    }
}