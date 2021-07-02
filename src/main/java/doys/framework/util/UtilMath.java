package doys.framework.util;
import doys.framework.core.ex.CommonException;
import doys.framework.core.ex.UnexpectedException;

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
            throw new CommonException("未实现的运算符 " + symbol);
        }
        // ------------------------------------------------
        strResult = numResult.toString();
        return strResult;
    }

    public static int tenPower(int pow) throws UnexpectedException {
        int result = 1;
        if (pow == 0) {
            return 1;
        }
        else if (pow > 0) {
            for (int i = 0; i < pow; i++) {
                result *= 10;
            }
        }
        else {
            throw new UnexpectedException();
        }
        return result;
    }
}