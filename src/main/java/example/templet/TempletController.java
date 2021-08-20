/******************************************************************************
 * Copyright (C), 2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2021-08-19
 * 通用controller模板类
 *****************************************************************************/
package example.templet;
import doys.framework.core.base.BaseControllerTenant;
import doys.framework.core.entity.RestResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/project/module")
public class TempletController extends BaseControllerTenant {
    @RequestMapping("/xxxxx")
    public RestResult getXXX() {
        String para1 = in("para1");
        // ------------------------------------------------
        try {
            ok("result", "resultString");
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}