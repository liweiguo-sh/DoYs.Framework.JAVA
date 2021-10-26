package doys.framework.util;
import doys.framework.core.ex.UnexpectedException;

public class UtilCheckBit {
    public static void main(String[] args) {
        String code = "1690123456789";
        try {
            System.out.println(getEanCheckBit(code));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -- GTIN13, GTIN14 ------------------------------------------------------
    public static String getEAN13(String code) throws Exception {
        if (code == null || code.length() != 12) {
            throw new UnexpectedException("EAN code length must be 12");
        }
        return getEanCheckBit(code);
    }
    public static String getGTIN14(String code) throws Exception {
        if (code == null || code.length() != 13) {
            throw new UnexpectedException("GTIN code length must be 13");
        }
        return getEanCheckBit(code);
    }

    private static String getEanCheckBit(String code) throws Exception {
        int len = code.length();
        int sum = 0, num;

        for (int i = 1; i <= len; i++) {
            num = Integer.parseInt(code.substring(len - i, len - i + 1));
            if (i % 2 == 0) {
                sum += num;
            }
            else {
                sum += 3 * num;
            }
        }
        sum = 10 - sum % 10;

        if (sum == 10) {
            return "0";
        }
        return String.valueOf(sum);
    }
}