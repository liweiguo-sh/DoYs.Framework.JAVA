/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-08-11
 * 前台主界面，首次加载时，获取必要的用户上下文信息
 *****************************************************************************/
package com.doys.framework.system.controller;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import com.doys.framework.database.ds.UtilDDS;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/framework/TopWin")
public class TopWinController extends BaseController {
    @RequestMapping("/getTopWin")
    private RestResult getTopWin() {
        int tenantId;
        // ------------------------------------------------
        try {
            tenantId = UtilDDS.getTenantId();

            ok("tenantId", tenantId);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}
