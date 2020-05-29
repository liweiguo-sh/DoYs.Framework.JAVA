/******************************************************************************
 * Copyright 2020, doys-next.com
 * Author: David.Li
 * Create Date: 2020-05-02
 * Modify Date: 2020-05-02
 * Description: 获取子系统、用户菜单
 *****************************************************************************/
package com.doys.framework.system.controller;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.db.DBFactory;
import com.doys.framework.core.entity.RestResult;
import com.doys.framework.system.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/sys_menu")
public class Menu extends BaseController {
    @Autowired
    DBFactory jtMaster;

    // ------------------------------------------------------------------------
    @GetMapping("/getSystem")
    private RestResult getSystem() {
        String userkey;

        SqlRowSet rsSystem;
        // ------------------------------------------------
        try {
            userkey = (String) this.session().getAttribute("userkey");
            rsSystem = MenuService.getSystem(jtMaster, userkey);

            ok("dtbSystem", rsSystem);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }

    @PostMapping("/getMenuByUser")
    private RestResult getMenuByUser(@RequestBody Map<String, String> req) {
        String userkey;
        String systemKey = req.get("systemKey");

        SqlRowSet rsMenu;
        // ------------------------------------------------
        try {
            userkey = (String) this.session().getAttribute("userkey");
            rsMenu = MenuService.getMenuByUser(jtMaster, systemKey, userkey);

            ok("dtbMenu", rsMenu);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }
}