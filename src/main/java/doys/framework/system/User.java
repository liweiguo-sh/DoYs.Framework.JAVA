/******************************************************************************
 * Copyright 2020-2021, doys-next.com
 * Author: David.Li
 * Create Date: 2020-04-10
 * Modify Date: 2021-02-23
 * Description: 用户登录
 *****************************************************************************/
package doys.framework.system;
import doys.framework.common.image.ImageVerifyCode;
import doys.framework.core.base.BaseControllerStd;
import doys.framework.core.entity.RestResult;
import doys.framework.util.UtilDate;
import doys.framework.util.UtilYml;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/user")
public class User extends BaseControllerStd {
    @Value("${global.login.verifyPassword}")
    private boolean blVerifyPassword;
    @Value("${global.login.verifyCode}")
    private boolean blVerifyCode;
    @Value("${global.login.superPassword}")
    private String superPassword;

    // ------------------------------------------------------------------------
    @PostMapping("/login")
    private RestResult login() {
        int tenantId;

        String userPk = in("userPk");
        String password = in("password");
        String loginTime = in("loginTime");
        String verifyCode = in("verifyCode");
        String verifyCodeSession = "";

        SqlRowSet rsUser, rsTenant;
        // ------------------------------------------------
        try {
            // -- 0. read tenant infomation --
            tenantId = parseTenantId(in("tenantId"));
            rsTenant = UserService.getTenant(dbSys, tenantId);
            if (rsTenant.next()) {
                session().setAttribute("tenantId", tenantId);
                session().setAttribute("tenantName", rsTenant.getString("name"));
                session().setAttribute("tenantShortName", rsTenant.getString("short_name"));
                session().setAttribute("tenantExpDate", rsTenant.getString("exp_date"));
            }
            else {
                return ResultErr("企业代码不正确，请检查。");
            }

            // -- 1.1 验证验证码 --
            if (blVerifyCode) {
                verifyCodeSession = ssValue("verifyCode");
                if (verifyCodeSession.equals("")) {
                    return ResultErr("会话已超时，请刷新登录页面。");
                }
                else {
                    if (!verifyCode.equalsIgnoreCase(verifyCodeSession)) {
                        return ResultErr("验证码错误，请检查。");
                    }
                }
            }
            // -- 1.2. 验证登录密码 --
            if (blVerifyPassword) {
                UserService.verifyUser(dbTenant, userPk, password, loginTime, superPassword);
            }

            // -- 2. 返回用户信息 --
            rsUser = UserService.getUser(dbTenant, userPk);
            if (rsUser.next()) {
                setSession(rsUser);
                if (!ssBoolean("isDeveloper") && rsUser.getInt("flag_menu_overdue") == 1) {
                    // -- 如果不是开发人员并且访问菜单的配置已过期，重新计算用户可访问菜单 --
                    UserService.recalUserMenu(dbTenant, userPk, ssValue("sqlUserGroupPks"));
                }
            }
            else {
                return ResultErr("用户不存在，请检查。");
            }

            // -- 3. 登录日志 --
            logger.info("用户 " + tenantId + "\\" + userPk + " 成功登录系统 (" + UtilDate.getDateTimeString() + ")");
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    @GetMapping("/getVerifyCode")
    private RestResult getVerifyCode(HttpSession ss) {
        int w = 150, h = 40;

        String fileName, verifyCode;
        String physicalPath, virtualPath, path;
        // ------------------------------------------------
        try {
            path = "0/login/checkcode/";
            virtualPath = UtilYml.getTempRootVPath() + path;
            physicalPath = UtilYml.getTempRootPath() + path;

            fileName = (new Random()).nextInt(1000000) + ".jpg";
            verifyCode = ImageVerifyCode.generateVerifyCode(4, "2345689ABCDEFGHKMNPRSUVWY");

            ImageVerifyCode.outputImage(w, h, new File(physicalPath + fileName), verifyCode);
            ss.setAttribute("verifyCode", verifyCode);

            if (blVerifyCode) {
                ok("verifyCode", "");
            }
            else {
                ok("verifyCode", verifyCode);
            }
            ok("url", virtualPath + fileName);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    // ------------------------------------------------------------------------
    private int parseTenantId(String tenantIdString) {
        int tenantId;
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(tenantIdString);

        while (matcher.find()) {
            tenantIdString = matcher.group();
        }
        tenantId = Integer.parseInt(tenantIdString);
        return tenantId;
    }
    private void setSession(SqlRowSet rsUser) throws Exception {
        String sql;
        String userPk, userName;
        String strGroupPks = "";
        String sqlGroupPks = "", sqlUserGroupPks = "";

        HttpSession ss = session();
        SqlRowSet rsGroupUser;
        // ------------------------------------------------
        rsUser.first();
        userPk = rsUser.getString("pk");
        userName = rsUser.getString("name");

        ok("userPk", userPk);
        ok("userName", userName);

        ss.setAttribute("userPk", userPk);
        ss.setAttribute("userName", userName);

        // ------------------------------------------------
        sql = "SELECT group_pk FROM sys_group_user WHERE user_pk = ?";
        rsGroupUser = dbTenant.getRowSet(sql, userPk);
        while (rsGroupUser.next()) {
            strGroupPks += "," + rsGroupUser.getString("group_pk");
            sqlGroupPks += ",'" + rsGroupUser.getString("group_pk") + "'";
        }
        if (sqlGroupPks.length() > 0) {
            strGroupPks = strGroupPks.substring(1);
            sqlGroupPks = sqlGroupPks.substring(1);
            sqlUserGroupPks = "'" + userPk + "'," + sqlGroupPks;
        }
        else {
            sqlUserGroupPks = "'" + userPk + "'";
        }
        ok("groupPks", strGroupPks);
        ss.setAttribute("sqlGroupPks", sqlGroupPks);
        ss.setAttribute("sqlUserGroupPks", sqlUserGroupPks);
        ss.setAttribute("isDeveloper", (sqlUserGroupPks.contains("'developer'") || sqlUserGroupPks.contains("'developers'")));
    }
}