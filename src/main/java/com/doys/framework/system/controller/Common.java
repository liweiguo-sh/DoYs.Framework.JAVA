/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-05-07
 * 数据库管理
 *****************************************************************************/
package com.doys.framework.system.controller;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.db.DBFactory;
import com.doys.framework.core.entity.RestResult;
import com.doys.framework.system.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/system/common")
public class Common extends BaseController {
    @Autowired
    DBFactory dbSys;

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