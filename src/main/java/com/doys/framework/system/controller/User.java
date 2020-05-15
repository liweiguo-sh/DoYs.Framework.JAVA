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
import com.doys.framework.core.entity.RestResult;
import com.doys.framework.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/user")
public class User extends BaseController {
    @Autowired
    JdbcTemplate jtMaster;

    @Value("${global.login.verifyPassword}")
    private boolean blVerifyPassword;
    @Value("${global.login.verifyCode}")
    private boolean blVerifyCode;
    @Value("${global.login.superPassword}")
    private String superPassword;

    // ------------------------------------------------------------------------
    @PostMapping("/login")
    private RestResult Login(@RequestBody Map<String, String> req) {
        boolean blPassword = false;

        String sql = "";
        String userkey = req.get("userkey");
        String password = req.get("password");
        String loginTime = req.get("loginTime");
        String verifyCode = req.get("verifyCode");
        String verifyCodeSession = "";

        SqlRowSet rowSet;
        // ------------------------------------------------
        try {
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
                blPassword = UserService.verifyUser(jtMaster, userkey, password, loginTime, superPassword);
            }

            // -- 2. 返回用户信息 --
            sql = "SELECT user_key, user_name FROM sys_user WHERE user_key = ?";
            rowSet = jtMaster.queryForRowSet(sql, userkey);
            if (rowSet.next()) {
                ok("userkey", rowSet.getString("user_key"));
                ok("username", rowSet.getString("user_name"));
            }
            else {
                return ResultErr("用户不存在，请检查。");
            }

            // -- 3. 登录日志 --
            logger.info("用户 " + userkey + " 登录成功, session_id = " + ss.getId());
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
}