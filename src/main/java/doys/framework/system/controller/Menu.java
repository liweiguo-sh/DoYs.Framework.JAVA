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
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/sys_menu")
public class Menu extends BaseControllerStd {
    @GetMapping("/getSystem")
    private RestResult getSystem() {
        SqlRowSet rsSystem;
        // ------------------------------------------------
        try {
            rsSystem = MenuService.getSystem(dbSys, dbTenant, ssValue("sqlUserGroupPks"));

            ok("dtbSystem", rsSystem);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    @PostMapping("/getMenuByUser")
    private RestResult getMenuByUser(@RequestBody Map<String, String> req) {
        String userPk;
        String systemKey = req.get("systemKey");

        SqlRowSet rsMenu;
        // ------------------------------------------------
        try {
            userPk = (String) this.session().getAttribute("userPk");
            rsMenu = MenuService.getMenuByUser(dbSys, systemKey, ssValue("sqlUserGroupPks"));

            ok("dtbMenu", rsMenu);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}