package com.doys.framework.test;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/test/dds")
public class DynamicDsController extends BaseController {
    @RequestMapping("/test1")
    private RestResult test1(HttpSession ss) throws Exception {
        String sql;
        SqlRowSet rs;
        // ------------------------------------------------
        try {
            sql = "SELECT * FROM sys_database";
            rs = dbSys.getRowSet(sql);
            while (rs.next()) {
                logger.info("name = " + rs.getString("name"));
            }

            sql = "SELECT * FROM base_label LIMIT 3";
            rs = dbBus.getRowSet(sql);
            while (rs.next()) {
                logger.info("name = " + rs.getString("name"));
            }
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    @RequestMapping("/test2")
    private RestResult test2(HttpSession ss) throws Exception {
        try {

        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
    @RequestMapping("/test3")
    private RestResult test3() throws Exception {
        try {

        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}