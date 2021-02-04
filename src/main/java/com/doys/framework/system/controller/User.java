/******************************************************************************
 * Copyright 2020, doys-next.com
 * Author: David.Li
 * Create Date: 2020-04-10
 * Modify Date: 2020-04-18
 * Description: 用户登录
 *****************************************************************************/
package com.doys.framework.system.controller;
import com.doys.framework.common.image.ImageVerifyCode;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import com.doys.framework.system.service.UserService;
import com.doys.framework.util.UtilEnv;
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
public class User extends BaseController {
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

        String dbName = "";
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
            session().setAttribute("tenantId", tenantId);

            rsTenant = UserService.getTenant(dbSys, tenantId);
            if (rsTenant.next()) {
                dbName = rsTenant.getString("database_name");
                session().setAttribute("dbName", dbName);
                session().setAttribute("tenantName", rsTenant.getString("name"));
                session().setAttribute("tenantShortName", rsTenant.getString("short_name"));
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
                    if (!verifyCode.equals(verifyCodeSession)) {
                        return ResultErr("验证码错误，请检查。");
                    }
                }
            }
            // -- 1.2. 验证登录密码 --
            if (blVerifyPassword) {
                UserService.verifyUser(dbBus, userPk, password, loginTime, superPassword);
            }

            // -- 2. 返回用户信息 --
            rsUser = UserService.getUser(dbBus, userPk);
            if (rsUser.next()) {
                setSession(rsUser);
                if (!ssBoolean("isDeveloper") && rsUser.getInt("flag_menu_overdue") == 1) {
                    // -- 如果不是开发人员并且访问菜单的配置已过期，重新计算用户可访问菜单 --
                    UserService.recalUserMenu(dbBus, userPk, ssValue("sqlUserGroupPks"));
                }
            }
            else {
                return ResultErr("用户不存在，请检查。");
            }

            // -- 3. 登录日志 --
            logger.info("用户 " + tenantId + "\\" + userPk + " 成功登录系统");
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    @GetMapping("/getVerifyCode")
    private RestResult getVerifyCode(HttpSession ss) {
        int w = 150, h = 40;

        String fileName = "", verifyCode = "";
        String physicalPath = "", virtualPath = "/work/login/checkcode/";
        // ------------------------------------------------
        try {
            physicalPath = UtilEnv.getWebRootPath() + virtualPath;
            fileName = (new Random()).nextInt(1000000) + ".jpg";
            verifyCode = ImageVerifyCode.generateVerifyCode(4, "1234567890");

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
        rsGroupUser = dbBus.getRowSet(sql, userPk);
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