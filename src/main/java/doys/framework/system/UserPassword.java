/******************************************************************************
 * Copyright 2020-2021, doys-next.com
 * Author: David.Li
 * Create Date: 2021-12-24
 * Modify Date: 2021-12-25
 * Description: 用户密码业务
 *****************************************************************************/
package doys.framework.system;
import doys.framework.core.base.BaseControllerTenant;
import doys.framework.core.entity.RestResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user_password")
public class UserPassword extends BaseControllerTenant {
    // ------------------------------------------------------------------------
    @PostMapping("/changePassword")
    private RestResult loginByApi() {
        String userPk;
        String oldPassword = in("oldPassword");
        String newPassword = in("newPassword");
        // ------------------------------------------------
        try {
            userPk = tokenString("userPk");
            if (!UserService.verfyPassword(dbTenant, userPk, oldPassword)) {
                return ResultErr("旧密码不正确，请检查。");
            }

            UserService.savePassword(dbTenant, userPk, newPassword);
            logger.info("用户 " + userPk + " 密码修改成功。");
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}