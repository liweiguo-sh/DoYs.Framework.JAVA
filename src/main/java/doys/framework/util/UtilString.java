package doys.framework.util;
import doys.framework.core.ex.CommonException;
import doys.framework.database.DBFactory;
import doys.framework.database.ds.TenantDataSource;
import doys.framework.database.ds.UtilTDS;
import org.springframework.jdbc.support.rowset.SqlRowSet;

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
        if (string1 == null) {
            return string2 == null;
        }
        else {
            if (string2 == null) return false;

            string1 = trimSpace(string1);
            string2 = trimSpace(string2);

            return string1.equalsIgnoreCase(string2);
        }
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
                    throw new CommonException("累加越界, 超过最大值.");
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

    public static String getSN(DBFactory db, String snKey, String defaultRule) throws Exception {
        return _getSN(db, snKey, "", defaultRule, 1);
    }
    public static String getSN(DBFactory db, String snKey, String scope, String defaultRule) throws Exception {
        return _getSN(db, snKey, scope, defaultRule, 1);
    }
    private static synchronized String _getSN(DBFactory dbBus, String snKey, String scope, String defaultRule, int step) throws Exception {
        int tenantId = ((TenantDataSource) dbBus.getDataSource()).getTenantId();
        int n1, n2, nLen;
        int nNewValue;

        String sql, sqlInsert;
        String strRule, strNewSN, strNewValue;
        String strYYYYMMDD, strYYYY, strYY, strMM, strDD = "";

        SqlRowSet rs;

        DBFactory db = UtilTDS.getDBFactory(tenantId);
        // ------------------------------------------------
        strYYYYMMDD = (new java.text.SimpleDateFormat("yyyyMMdd")).format(new java.util.Date());
        strYYYY = strYYYYMMDD.substring(0, 4);
        strYY = strYYYYMMDD.substring(2, 4);
        strMM = strYYYYMMDD.substring(4, 6);
        strDD = strYYYYMMDD.substring(6, 8);

        // -- 1. 取序列定义 ------------------------------------
        sql = "SELECT t.sn_pk, m.rule, year, month, day, value "
            + "FROM aid_serial_definition m INNER JOIN aid_serial t ON m.pk = t.sn_pk WHERE t.scope = ? AND t.sn_pk = ?";
        rs = db.getRowSet(sql, scope, snKey);
        if (!rs.next()) {
            sqlInsert = "INSERT INTO aid_serial(scope, sn_pk, year, month, day, value) VALUES (?, ?, ?, ?, ?, 0)";
            db.exec(sqlInsert, scope, snKey, strYYYY, strMM, strDD);

            rs = db.getRowSet(sql, scope, snKey);
            if (!rs.next()) {
                if (!defaultRule.equals("")) {
                    sqlInsert = "INSERT INTO aid_serial_definition (pk, name, rule) VALUES (?, ?, ?)";
                    db.exec(sqlInsert, snKey, snKey, defaultRule);

                    rs = db.getRowSet(sql, scope, snKey);
                    if (!rs.next()) {
                        throw new CommonException("创建序列 " + snKey + " 遇到意外错误，请检查。");
                    }
                }
                else {
                    sql = "DELETE FROM aid_serial WHERE sn_pk NOT IN (SELECT pk FROM aid_serial_definition)";
                    db.exec(sql);
                }
            }
        }
        strRule = rs.getString("rule").toUpperCase();
        // -- 2. 更新序列值 ------------------------------------
        if (((strRule.contains("{YYYY}") || strRule.contains("{YY}")) && !rs.getString("year").equals(strYYYY))
            || (strRule.contains("{MM}") && rs.getInt("month") != Integer.parseInt(strMM))
            || (strRule.contains("{DD}") && rs.getInt("day") != Integer.parseInt(strDD))) {
            sql = "UPDATE aid_serial SET year = ?, month = ?, day = ?, value = ? WHERE scope = ? AND sn_pk = ?";
            db.exec(sql, strYYYY, strMM, strDD, step, scope, snKey);
            nNewValue = step;
        }
        else {
            nNewValue = step + rs.getInt("value");
            sql = "UPDATE aid_serial SET value = ? WHERE scope = ? AND sn_pk = ?";
            db.exec(sql, nNewValue, scope, snKey);
        }
        // -- 3. 生成序列值 ------------------------------------
        strNewSN = strRule;
        if (strNewSN.contains("{YYYY}")) {
            strNewSN = strNewSN.replaceAll("\\{YYYY}", strYYYY);
        }
        if (strNewSN.contains("{YY}")) {
            strNewSN = strNewSN.replaceAll("\\{YY}", strYY);
        }
        if (strNewSN.contains("{MM}")) {
            strNewSN = strNewSN.replaceAll("\\{MM}", strMM);
        }
        if (strNewSN.contains("{DD}")) {
            strNewSN = strNewSN.replaceAll("\\{DD}", strDD);
        }

        n1 = strNewSN.indexOf("{");
        n2 = strNewSN.indexOf("}");
        if (n1 >= n2 - 1) {
            return "";
        }
        nLen = Integer.parseInt(strNewSN.substring(n1 + 1, n2));
        strNewValue = String.format("%0" + nLen + "d", nNewValue);
        strNewSN = strNewSN.substring(0, n1) + strNewValue + strNewSN.substring(n2 + 1);

        // -- 4. 检查是否越界 -----------------------------------
        if (String.valueOf(nNewValue).length() > nLen) {
            throw new CommonException("序列号越界, 请检查.");
        }
        return strNewSN;
    }

    public static String padLeft(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }

        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, 0, src.length());
        for (int i = src.length(); i < len; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }
    public static String padRight(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }

        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, diff, src.length());
        for (int i = 0; i < diff; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }
    public static String padLeftNumber(int num, String pad, int len) {
        String numStr = String.valueOf(num);

        for (int i = numStr.length(); i < len; i++) {
            numStr = pad + numStr;
        }
        return numStr;
    }

    public static int toInt(String stringValue) {
        if (stringValue == null) return 0;

        stringValue = stringValue.trim();
        if (stringValue.equals("")) return 0;

        return Integer.parseInt(stringValue);
    }
}