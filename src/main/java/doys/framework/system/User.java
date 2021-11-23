/******************************************************************************
 * Copyright 2020-2021, doys-next.com
 * Author: David.Li
 * Create Date: 2020-04-10
 * Modify Date: 2021-11-23
 * Description: 用户登录
 *
 * Modify Date: 2021-11-22
 * 改为token模式
 *****************************************************************************/
package doys.framework.system;
import doys.framework.a0.Const;
import doys.framework.common.image.ImageVerifyCode;
import doys.framework.core.Token;
import doys.framework.core.TokenService;
import doys.framework.core.base.BaseControllerStd;
import doys.framework.core.entity.RestResult;
import doys.framework.database.DBFactory;
import doys.framework.database.ds.UtilTDS;
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
    @PostMapping("/getToken")
    private RestResult loginByApi() {
        int tenantId = UserService.parseTenantId(in("tenantId", "0"));

        String sql;
        String userPk = in("username");
        String password = in("appSecret");

        SqlRowSet rsTenant, rsUser;
        DBFactory dbBus;
        Token token;
        // ------------------------------------------------
        try {
            // -- 1. 验证商户 --
            sql = "SELECT id FROM sys_tenant WHERE id = ?";
            rsTenant = dbSys.getRowSet(sql, tenantId);
            if (!rsTenant.next()) {
                return ResultErr(Const.ERR_TENANT_ID, Const.ERROR_TENANT_ID);
            }

            // -- 2. 验证用户 --
            dbBus = UtilTDS.getDBFactory(tenantId);
            sql = "SELECT password FROM sys_user WHERE pk = ?";
            rsUser = dbBus.getRowSet(sql, userPk);
            if (rsUser.next()) {
                if (!rsUser.getString("password").equalsIgnoreCase(password)) {
                    return ResultErr(Const.ERR_APP_SECRET, Const.ERROR_APP_SECRET);
                }
            }
            else {
                return ResultErr(Const.ERR_USER_NAME, Const.ERROR_USER_NAME);
            }

            // -- 3. 创建token --
            token = TokenService.createToken(tenantId, userPk);
            token.setValue("tenantId", tenantId);
            token.setValue("userPk", userPk);
            ok("token", token.tokenId);
            ok("expTime", UtilDate.getDateTimeStr(token.getExpTime()));

            // -- 9. 登录日志 --
            logger.info("用户 " + tenantId + "\\" + userPk + " 获取token[" + token.tokenId + "]成功 (" + UtilDate.getDateTimeString() + ")");
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
    // ------------------------------------------------------------------------
    @PostMapping("/login")
    private RestResult loginByWeb() {
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
            tenantId = UserService.parseTenantId(head("tenantId"));
            rsTenant = UserService.getTenant(dbSys, tenantId);
            if (!rsTenant.next()) {
                return ResultErr("企业代码不正确，请检查。");
            }

            // -- 1.1 验证验证码 --
            if (blVerifyCode) {
                HttpSession ss = request().getSession();
                if (ss.getAttribute("verifyCode") != null) {
                    verifyCodeSession = (String) ss.getAttribute("verifyCode");
                }
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
                setToken(rsTenant, rsUser);
                if (!tokenBoolean("isDeveloper") && rsUser.getInt("flag_menu_overdue") == 1) {
                    // -- 如果不是开发人员并且访问菜单的配置已过期，重新计算用户可访问菜单 --
                    UserService.recalUserMenu(dbTenant, userPk, tokenString("sqlUserGroupPks"));
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
    private void setToken(SqlRowSet rsTenant, SqlRowSet rsUser) throws Exception {
        int tenantId;

        String sql;
        String userPk, userName;
        String strGroupPks = "";
        String sqlGroupPks = "", sqlUserGroupPks = "";

        Token tss;
        SqlRowSet rsGroupUser;
        // -- 1. create tokenSession -------------------------
        rsTenant.first();
        tenantId = rsTenant.getInt("id");

        rsUser.first();
        userPk = rsUser.getString("pk");
        userName = rsUser.getString("name");

        // -- 2. ------------------------------------------
        tss = TokenService.createToken(tenantId, userPk);
        tss.setValue("tenantId", tenantId);
        tss.setValue("tenantName", rsTenant.getString("name"));
        tss.setValue("tenantShortName", rsTenant.getString("short_name"));
        tss.setValue("tenantExpDate", rsTenant.getString("exp_date"));
        tss.setValue("userPk", userPk);
        tss.setValue("userName", userName);
        super.entityRequest().header.setValue("token", tss.tokenId);

        ok("token", tss.tokenId);
        ok("userPk", userPk);
        ok("userName", userName);

        // -- 3. ------------------------------------------
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

        tss.setValue("sqlGroupPks", sqlGroupPks);
        tss.setValue("sqlUserGroupPks", sqlUserGroupPks);
        tss.setValue("isDeveloper", (sqlUserGroupPks.contains("'developer'") || sqlUserGroupPks.contains("'developers'")));
        ok("groupPks", strGroupPks);
    }
}