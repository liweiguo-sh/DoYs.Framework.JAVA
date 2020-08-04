package com.doys.example.test;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import com.doys.framework.database.DBFactory;
import com.doys.framework.database.ds.UtilDDS;
import com.doys.framework.database.dtb.DataTable;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/test/dds")
public class DynamicDsController extends BaseController {
    @RequestMapping("/test1")
    private RestResult test1() throws Exception {
        String sql;
        SqlRowSet rs;
        // ------------------------------------------------
        try {
            sql = "SELECT * FROM sys_database";
            rs = dbSys.getRowSet(sql);
            while (rs.next()) {
                logger.info("name = " + rs.getString("name"));
            }

            sql = "SELECT * FROM base_area LIMIT 3";
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
            DBFactory dbf = UtilDDS.getDBFactory(100);

            SqlRowSet rs = dbf.getRowSet("SELECT name FROM base_area LIMIT 3");
            while (rs.next()) {
                logger.info("name = " + rs.getString("name"));
            }
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
    @RequestMapping("/test3")
    private RestResult test3() throws Exception {
        try {
            DataTable dtb = dbSys.getDataTable("SELECT id, name FROM sys_view_field limit 2");
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}