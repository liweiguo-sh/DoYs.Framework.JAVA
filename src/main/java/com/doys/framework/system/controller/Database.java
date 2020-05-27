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
import com.doys.framework.core.db.DBSchema;
import com.doys.framework.core.entity.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/system/database")
public class Database extends BaseController {
    @Autowired
    DBFactory dbMaster;

    @PostMapping("/refresh")
    public RestResult refreshDBStruct(@RequestBody Map<String, String> req) {
        String para1 = req.get("para1");
        // ------------------------------------------------
        try {
            DBSchema schema = new DBSchema(dbMaster);
            schema.refreshDBStruct("db_", "");
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}