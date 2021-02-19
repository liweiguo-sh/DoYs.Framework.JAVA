/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-05-07
 * 数据库管理
 *****************************************************************************/
package doys.framework.system.controller;
import doys.framework.core.base.BaseControllerSys;
import doys.framework.core.entity.RestResult;
import doys.framework.system.service.CommonService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/system/common")
public class Common extends BaseControllerSys {
    @PostMapping("/clearGarbageData")
    public RestResult clearGarbageData() {
        ArrayList<String> list = new ArrayList<>();
        // ------------------------------------------------
        try {
            CommonService.clearGarbageData(dbSys);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}