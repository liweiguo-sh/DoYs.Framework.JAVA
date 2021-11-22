/******************************************************************************
 * Copyright 2020, doys-next.com
 * Author: David.Li
 * Create Date: 2020-05-02
 * Modify Date: 2020-05-02
 * Description: 获取子系统、用户菜单
 *****************************************************************************/
package doys.framework.system.controller;
import doys.framework.core.base.BaseControllerStd;
import doys.framework.core.entity.RestResult;
import doys.framework.system.service.MenuService;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys_menu")
public class Menu extends BaseControllerStd {
    @GetMapping("/getSystem")
    private RestResult getSystem() {
        SqlRowSet rsSystem;
        // ------------------------------------------------
        try {
            rsSystem = MenuService.getSystem(dbSys, dbTenant, tokenString("sqlUserGroupPks"));

            ok("dtbSystem", rsSystem);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    @PostMapping("/getMenuByUser")
    private RestResult getMenuByUser() {
        String systemKey = in("systemKey");

        SqlRowSet rsMenu;
        // ------------------------------------------------
        try {
            rsMenu = MenuService.getMenuByUser(dbSys, systemKey, tokenString("sqlUserGroupPks"));

            ok("dtbMenu", rsMenu);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}