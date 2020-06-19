package com.doys.framework.util;

public class UtilString {
    /**
     * 驼峰转下划线
     *
     * @param strField 实体类字段，示例：officeCode
     * @return 返回数据库字段格式，示例：office_code
     */
    public static String humpToUnderline(String strField) throws Exception {
        StringBuilder sb = new StringBuilder();
        char[] chars = strField.toCharArray();
        for (char c : chars) {
            if (c >= 65 && c <= 90) {
                c += 32;
                sb.append("_");
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * TODO: xpas版jdk有这个问题，dosy新版有待验证。当前方法需要验证改进或废除。
     * 去除字符串两端空格(包括不可见字符)不论左右两端空格有多少，字符串中间空格不去除，顺带将null字符串返回空字符串, java自带的trim有问题.
     */
    public static String trimSpace(String str) {
        if (str == null || str.equals("")) {
            return "";
        }
        int n1 = 0, n2 = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) > 32) {
                n1 = i;
                break;
            }
        }
        for (int i = str.length() - 1; i >= 0; i--) {
            if (str.charAt(i) > 32) {
                n2 = i + 1;
                break;
            }
        }
        return str.substring(n1, n2);
    }

    public static String KillNull(String valueString) {
        if (valueString == null) {
            return "";
        }
        return valueString;
    }
    public static boolean equals(String string1, String string2) {
        string1 = trimSpace(string1);
        string2 = trimSpace(string2);

        return string1.equalsIgnoreCase(string2);
    }

    public static String getNewNodeKey(String currentMaxNodeKey, int nNodeLen) throws Exception {
        int nLenParent = currentMaxNodeKey.length() - nNodeLen;
        String pNodeKey = currentMaxNodeKey.substring(0, nLenParent);
        String subKey = currentMaxNodeKey.substring(nLenParent);

        char[] chArr = subKey.toCharArray();
        for (int i = chArr.length - 1; i >= 0; i--) {
            if (chArr[i] == 'Z') {
                chArr[i] = '0';
                if (i == 0) {
                    // -- 累加越界, 超过最大范围 --
                    throw new Exception("累加越界, 超过最大值.");
                }
            }
            else {
                if (chArr[i] == '9') {
                    chArr[i] = 'A';
                }
                else {
                    chArr[i]++;
                }
                break;
            }
        }
        return pNodeKey + new String(chArr);
    }
}