/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-05-06
 * 通用视图controller模板类
 *****************************************************************************/
package com.doys.example.templet;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.db.DBFactory;
import com.doys.framework.core.entity.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/xxx/xxxx")
public class ViewControllerTemplet extends BaseController {
    @Autowired
    DBFactory jtMaster;

    @PostMapping("/xxxxx")
    public RestResult getXXX(@RequestBody Map<String, String> req) {
        String para1 = req.get("para1");
        // ------------------------------------------------
        try {
            ok("result", "resultString");
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}