package doys.framework.common.security;
import java.security.MessageDigest;

public class UtilDigest {
    private static String MD5_SPLIT = "^";
    // ------------------------------------------------------------------------
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

    // ------------------------------------------------------------------------
    public static String passwordMD5(String userPk, String password) {
        return MD5(userPk + MD5_SPLIT + password + MD5_SPLIT + "doys-next.com");
    }
    public static String passwordLoginMD5(String passwordMD5, String loginTime) {
        return MD5(passwordMD5 + MD5_SPLIT + loginTime.substring(2));
    }

    // -- test ----------------------------------------------------------------
    public static void main(String[] args) {
        String userPk = "demo";
        String password = "demo";

        String passwordMD5;
        // ------------------------------------------------
        try {
            String a = "BFEBFBFF000806EA";
            String b = MD5(a);

            passwordMD5 = passwordMD5(userPk, password);
            System.out.println(passwordMD5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}