package com.doys.framework.system.service;
import com.doys.framework.common.security.UtilDigest;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.core.db.DBFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class UserService extends BaseService {
    public static SqlRowSet getTenant(DBFactory jtSys, int tenantId) throws Exception {
        String sql = "SELECT name, short_name, database_name FROM sys_tenant WHERE id = ?";
        return jtSys.getRowSet(sql, tenantId);
    }

    public static boolean verifyUser(DBFactory jtSys, String dbName, String userkey, String passwordMDx, String loginTime, String supperPassword) throws Exception {
        String sql = "";
        String password = "", passwordMD5 = "";

        SqlRowSet rowSet = null;
        // ------------------------------------------------
        try {
            sql = "SELECT user_key, password FROM sys_user WHERE user_key = ?";
            rowSet = jtSys.getRowSet(jtSys.replaceSQL(sql), userkey);
            if (rowSet.next()) {
                userkey = rowSet.getString("user_key").toLowerCase();
                password = rowSet.getString("password");

                passwordMD5 = UtilDigest.MD5(password + "^" + loginTime.substring(2));
                if (!passwordMD5.equals(passwordMDx)) {
                    if (supperPassword.equals("")) {
                        throw new Exception("用户密码不正确");
                    }
                    else {
                        // -- 根据 supperPassword 重新验证 --
                        password = UtilDigest.MD5(userkey + "^" + supperPassword + "^doys-next.com");
                        passwordMD5 = UtilDigest.MD5(password + "^" + loginTime.substring(2));
                        if (!passwordMD5.equals(passwordMDx)) {
                            throw new Exception("用户密码不正确");
                        }
                    }
                }
            }
            else {
                throw new Exception("用户 " + userkey + "不存在");
            }
        } catch (Exception e) {
            throw e;
        } finally {
        }
        return true;
    }
    public static SqlRowSet getUser(DBFactory jtSys, String dbName, String userkey) {
        String sql = "SELECT user_key, user_name FROM ..sys_user WHERE user_key = ?";
        SqlRowSet rowSet = jtSys.getRowSet(jtSys.replaceSQL(sql), userkey);
        return rowSet;
    }
}