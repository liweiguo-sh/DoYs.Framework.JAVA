/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-08-11
 * 前台主界面，首次加载时，获取必要的用户上下文信息
 *****************************************************************************/
package doys.framework.system.controller;
import doys.framework.core.base.BaseControllerStd;
import doys.framework.core.entity.RestResult;
import doys.framework.database.ds.UtilTDS;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/framework/TopWin")
public class TopWinController extends BaseControllerStd {
    @RequestMapping("/getTopWin")
    private RestResult getTopWin() {
        int tenantId;
        // ------------------------------------------------
        try {
            tenantId = UtilTDS.getTenantId();

            ok("tenantId", tenantId);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}
