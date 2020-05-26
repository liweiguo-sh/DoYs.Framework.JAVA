/******************************************************************************
 * Copyright 2020, doys-next.com
 * Author: David.Li
 * Create Date: 2020-04-10
 * Modify Date: 2020-04-18
 * Description: 用户登录
 *****************************************************************************/
package com.doys.framework.system.controller;
import com.doys.framework.common.UtilEnv;
import com.doys.framework.common.image.ImageVerifyCode;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.db.DBFactory;
import com.doys.framework.core.entity.RestResult;
import com.doys.framework.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    DBFactory jtMaster;

    @Value("${global.login.verifyPassword}")
    private boolean blVerifyPassword;
    @Value("${global.login.verifyCode}")
    private boolean blVerifyCode;
    @Value("${global.login.superPassword}")
    private String superPassword;

    // ------------------------------------------------------------------------
    @PostMapping("/login")
    private RestResult login() {
        int tenantId = 0;

        String dbName = "";
        String userkey = in("userkey");
        String password = in("password");
        String loginTime = in("loginTime");
        String verifyCode = in("verifyCode");
        String verifyCodeSession = "";

        SqlRowSet rsUser, rsTenant;
        // ------------------------------------------------
        try {
            // -- 0. read tenant infomation --
            tenantId = parseTenantId(in("tenantId"));
            rsTenant = UserService.getTenant(jtMaster, tenantId);
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
                verifyCodeSession = getSessionValue("verifyCode");
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
                UserService.verifyUser(jtMaster, dbName, userkey, password, loginTime, superPassword);
            }

            // -- 2. 返回用户信息 --
            rsUser = UserService.getUser(jtMaster, dbName, userkey);
            if (rsUser.next()) {
                setSession(rsUser);
            }
            else {
                return ResultErr("用户不存在，请检查。");
            }

            // -- 3. 登录日志 --
            logger.info("用户 " + userkey + " 登录成功, session_id = " + this.session().getId());
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
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
        HttpSession ss = session();

        rsUser.first();
        ok("userkey", rsUser.getString("user_key"));
        ok("username", rsUser.getString("user_name"));

        ss.setAttribute("userkey", rsUser.getString("user_key"));
        ss.setAttribute("username", rsUser.getString("user_name"));
    }
}