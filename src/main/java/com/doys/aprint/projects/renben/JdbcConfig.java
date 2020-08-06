package com.doys.aprint.projects.renben;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprint/projects/renben/JdbcConfig")
public class JdbcConfig extends BaseController {
    @RequestMapping("/getJdbcConfig")
    private RestResult getJdbcConfig() {
        String sql;

        SqlRowSet rs;
        // ------------------------------------------------
        try {
            sql = "SELECT * FROM cfg_jdbc WHERE pk = 'order'";
            rs = dbBus.getRowSet(sql);
            ok("dtbCfgJdbc", rs);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
    @RequestMapping("/saveJdbcConfig")
    private RestResult saveJdbcConfig() {
        int active = inInt("flag_active");

        String sql;
        String pk = in("pk");
        String url = in("url");
        String driver = in("driver");
        String username = in("username");
        String password = in("password");
        String tablename = in("tablename");
        String field_order_number = in("field_order_number");
        String field_customer_code = in("field_customer_code");

        SqlRowSet rs;
        // ------------------------------------------------
        try {
            if (pk.equalsIgnoreCase("order")) {
                sql = "UPDATE cfg_jdbc SET url = ?, driver = ?, username = ?, password = ?, tablename = ?, field_order_number = ?, field_customer_code = ?, flag_active = ? WHERE pk = 'order'";
                dbBus.exec(sql, url, driver, username, password, tablename, field_order_number, field_customer_code, active);
            }
            else {
                sql = "INSERT INTO cfg_jdbc (pk, url, driver, username, password, tablename, field_order_number, field_customer_code, flag_active) VALUES ('order', ?, ?, ?, ?, ?, ?, ?, ?)";
                dbBus.exec(sql, url, driver, username, password, tablename, field_order_number, field_customer_code, active);
            }

            // --------------------------------------------
            sql = "SELECT * FROM cfg_jdbc WHERE pk = 'order'";
            rs = dbBus.getRowSet(sql);
            ok("dtbCfgJdbc", rs);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}