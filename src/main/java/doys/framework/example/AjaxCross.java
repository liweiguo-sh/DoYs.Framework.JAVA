/******************************************************************************
 * Copyright (C), 2020-2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2021-03-23
 * Ajax跨域访问测试类
 *****************************************************************************/
package doys.framework.example;
import doys.framework.core.base.BaseController;
import doys.framework.core.entity.RestResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/AjaxCross")
public class AjaxCross extends BaseController {
    @RequestMapping("/test1")
    private RestResult test1() {
        String p1 = in("p1");
        // ------------------------------------------------
        try {
            ok("result", "服务端收到客户端参数 p1 = " + p1);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}