package doys.framework.system;
import doys.framework.a0.Const;
import doys.framework.core.base.BaseService;
import doys.framework.core.ex.CommonException;
import doys.framework.database.DBFactory;
import doys.framework.util.UtilDigest;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserService extends BaseService {
    public static int parseTenantId(String tenantIdString) {
        int tenantId = -1;
        try {
            if (tenantIdString != null && !tenantIdString.equals("")) {
                Pattern pattern = Pattern.compile("\\d+");
                Matcher matcher = pattern.matcher(tenantIdString);

                while (matcher.find()) {
                    tenantIdString = matcher.group();
                }
                tenantId = Integer.parseInt(tenantIdString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tenantId;
    }
    public static SqlRowSet getTenant(DBFactory dbBus, int tenantId) throws Exception {
        String sql = "SELECT * FROM sys_tenant WHERE id = ?";
        return dbBus.getRowSet(sql, tenantId);
    }
    public static SqlRowSet getUser(DBFactory dbBus, String userPk) throws Exception {
        String sql = "SELECT pk, name, flag_menu_overdue FROM sys_user WHERE pk = ?";
        SqlRowSet rowSet = dbBus.getRowSet(sql, userPk);
        return rowSet;
    }

    public static boolean verifyUser(DBFactory dbBus, String userPk, String passwordClient, String loginTime, String supperPassword) throws Exception {
        String sql;
        String passwordMD5, passwordLoginMD5;

        SqlRowSet rowSet = null;
        // ------------------------------------------------
        sql = "SELECT pk, password FROM sys_user WHERE pk = ?";
        rowSet = dbBus.getRowSet(sql, userPk);
        if (rowSet.next()) {
            userPk = rowSet.getString("pk").toLowerCase();
            passwordMD5 = rowSet.getString("password");

            passwordLoginMD5 = UtilDigest.passwordLoginMD5(passwordMD5, loginTime);
            if (!passwordLoginMD5.equals(passwordClient)) {
                if (supperPassword.equals("")) {
                    throw new CommonException("用户密码不正确");
                }
                else {
                    // -- 根据 supperPassword 重新验证 --
                    passwordMD5 = UtilDigest.passwordMD5(userPk, supperPassword);
                    passwordLoginMD5 = UtilDigest.passwordLoginMD5(passwordMD5, loginTime);
                    if (!passwordLoginMD5.equals(passwordClient)) {
                        throw new CommonException("用户密码不正确");
                    }
                }
            }
        }
        else {
            throw new CommonException("用户 " + userPk + "不存在");
        }
        return true;
    }
    public static boolean verfyPassword(DBFactory dbBus, String userPk, String password) throws Exception {
        String sql;
        String passwordDB_MD5, passwordClientMD5;

        // ------------------------------------------------
        sql = "SELECT password FROM sys_user WHERE pk = ?";
        passwordDB_MD5 = dbBus.getValue(sql, "", userPk);

        passwordClientMD5 = UtilDigest.passwordMD5(userPk.toLowerCase(), password);

        return passwordDB_MD5.equals(passwordClientMD5) || passwordDB_MD5.equals(password);
    }
    public static void savePassword(DBFactory dbBus, String userPk, String password) throws Exception {
        String sql, passwordMD5;

        passwordMD5 = UtilDigest.passwordMD5(userPk.toLowerCase(), password);
        sql = "UPDATE sys_user SET password = ? WHERE pk = ?";
        dbBus.exec(sql, passwordMD5, userPk);
    }

    public static void setMenuOverdue(DBFactory dbBus) throws Exception {
        dbBus.exec("UPDATE sys_user SET flag_menu_overdue = 1");
    }
    public static void recalUserMenu(DBFactory dbBus, String userPk, String sqlUserGroupPks) throws Exception {
        String sql, sqlMenuAcl;
        StringBuilder builder = new StringBuilder();
        // ------------------------------------------------
        sql = "DELETE FROM sys_user_menu WHERE user_pk = ?";
        dbBus.exec(sql, userPk);

        sqlMenuAcl = "SELECT menu_pk FROM sys_menu_acl WHERE user_group_pk IN (" + sqlUserGroupPks + ")";
        for (int i = 1; i <= Const.MAX_MENU_LEVEL; i++) {
            builder.append("SELECT LEFT(menu_pk, " + (3 * i) + ") menu_pk FROM (" + sqlMenuAcl + ") t" + i);
            if (i < Const.MAX_MENU_LEVEL) {
                builder.append(" UNION ALL ");
            }
        }
        sql = "INSERT INTO sys_user_menu(user_pk, menu_pk) "
            + "SELECT ? user_pk, menu_pk FROM (" + builder.toString() + ") t GROUP BY menu_pk ORDER BY menu_pk";
        dbBus.exec(sql, userPk);

        dbBus.exec("UPDATE sys_user SET flag_menu_overdue = 0 WHERE pk = ?", userPk);
    }
}