package com.doys.framework.common.security;
import java.security.MessageDigest;

public class UtilDigest {
    public static String MD5(String strString) {
        try {
            StringBuilder sb = new StringBuilder();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest((strString).getBytes("UTF-8"));

            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String MD6(String strString) {
        return MD5(strString + "^doys-next.com");
    }
}
