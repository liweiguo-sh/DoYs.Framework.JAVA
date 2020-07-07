package com.doys.ems.test;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/ems/test/test1")
public class Test1 extends BaseController {
    @RequestMapping("/t1")
    private RestResult doT1() {
        // ------------------------------------------------
        try {
            logger.info("test1, debug here");
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }
}