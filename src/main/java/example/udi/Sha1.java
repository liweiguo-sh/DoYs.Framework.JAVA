package example.udi;
import java.security.MessageDigest;
import java.util.Calendar;

public class Sha1 {
    public static void main(String[] args) {
        long ts = Calendar.getInstance().getTimeInMillis();

        String sk = getSha1(ts + "udi2020");

        System.out.println("ts = " + ts);
        System.out.println("sk = " + sk);
    }

    public static String getSha1(String decript) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // -- Create Hex String --
            StringBuffer hexString = new StringBuffer();
            // -- 字节数组转换为 十六进制 数 --
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}