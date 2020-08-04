package com.doys.framework.system.service;
import com.doys.framework.common.security.UtilDigest;
import com.doys.framework.config.Const;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.database.DBFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class UserService extends BaseService {
    public static SqlRowSet getTenant(DBFactory jtSys, int tenantId) throws Exception {
        String sql = "SELECT name, short_name, database_name FROM sys_tenant WHERE id = ?";
        return jtSys.getRowSet(sql, tenantId);
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
                    throw new Exception("用户密码不正确");
                }
                else {
                    // -- 根据 supperPassword 重新验证 --
                    passwordMD5 = UtilDigest.passwordMD5(userPk, supperPassword);
                    passwordLoginMD5 = UtilDigest.passwordLoginMD5(passwordMD5, loginTime);
                    if (!passwordLoginMD5.equals(passwordClient)) {
                        throw new Exception("用户密码不正确");
                    }
                }
            }
        }
        else {
            throw new Exception("用户 " + userPk + "不存在");
        }
        return true;
    }
    public static SqlRowSet getUser(DBFactory dbBus, String userPk) throws Exception {
        String sql = "SELECT pk, name, flag_menu_overdue FROM sys_user WHERE pk = ?";
        SqlRowSet rowSet = dbBus.getRowSet(sql, userPk);
        return rowSet;
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