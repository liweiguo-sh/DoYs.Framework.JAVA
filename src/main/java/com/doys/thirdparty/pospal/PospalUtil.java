package com.doys.thirdparty.pospal;
import java.security.MessageDigest;
import java.util.Calendar;

public class PospalUtil {
    public static String appKey = "527210429188633158";
    public static String appId = "BAD3BB37E0D7A96485CC870850F9A894";
    public static String urlQuery = "https://area13-win.pospal.cn:443/pospal-api2/openapi/v1/productOpenApi/";

    public static String urlQueryProductCategoryPages = urlQuery + "queryProductCategoryPages";
    public static String urlQueryProductPages = urlQuery + "queryProductPages";
    public static String urlQueryProductImagesByProductUid = urlQuery + "queryProductImagesByProductUid";

    // ------------------------------------------------------------------------
    public static String encryptToMd5String(String content, String appKey) throws Exception {
        //return encryptToMd5String(StringUtils.trim(appKey) + StringUtils.trim(content));  // -- 找不到正确的类库 --
        //return encryptToMd5String(StringUtils.trimAllWhitespace(appKey) + StringUtils.trimAllWhitespace(content));
        return encryptToMd5String(appKey.trim() + content.trim());
    }
    private static String encryptToMd5String(String content) throws Exception {
        String md5String = null;
        MessageDigest md = MessageDigest.getInstance("md5");
        md.update(content.getBytes("UTF-8"));
        md5String = parseByte2HexString(md.digest());
        return md5String;
    }
    private static String parseByte2HexString(byte buf[]) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            stringBuffer.append(hex.toUpperCase());
        }
        return stringBuffer.toString();
    }

    public static String getTimeStamp() {
        return String.valueOf(Calendar.getInstance().getTimeInMillis());
    }
}