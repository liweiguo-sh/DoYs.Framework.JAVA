/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-05-07
 * 数据库管理
 *****************************************************************************/
package doys.framework.system.controller;
import doys.framework.core.base.BaseControllerStd;
import doys.framework.core.entity.RestResult;
import doys.framework.system.service.ViewService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/view")
public class ViewController extends BaseControllerStd {
    @PostMapping("/refresh")
    public RestResult refreshDBStruct() {
        String viewPk = in("viewPk");
        // ------------------------------------------------
        try {
            ViewService.refreshViewField(dbSys, dbTenant, viewPk);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}