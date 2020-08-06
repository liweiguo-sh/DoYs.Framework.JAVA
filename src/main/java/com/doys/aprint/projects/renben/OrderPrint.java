package com.doys.aprint.projects.renben;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
@RestController
@RequestMapping("/aprint/projects/renben/order_print")
public class OrderPrint extends BaseController {
    @RequestMapping("/getOrderByOrderNumber")
    private RestResult getOrderByOrderNumber() {
        boolean flagActive = false;

        String orderNumber = in("orderNumber");
        String sql;

        SqlRowSet rsOrder;
        // ------------------------------------------------
        try {
            sql = "SELECT flag_active FROM cfg_jdbc WHERE pk = 'order'";
            flagActive = (dbBus.getInt(sql, 0) == 1);

            if (flagActive) {
                getOrderFromOracle(orderNumber);
            }
            else {
                // -- 模拟ORACLE订单 --
                sql = "SELECT * FROM T_ORDER WHERE order_number = ?";
                rsOrder = dbBus.getRowSet(sql, orderNumber);
                ok("dtbOrder", rsOrder);
            }
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
    private void getOrderFromOracle(String orderNumber) throws Exception {
        String sql;
        String url, driverClassName, username, password;
        String tablename, fieldname;

        Connection conn = null;
        Statement stmt = null;
        ResultSet rsOrder = null;
        SqlRowSet rsJdbc;
        // ------------------------------------------------
        try {
            // -- 取订单数据库连接参数 --
            sql = "SELECT * FROM cfg_jdbc WHERE pk = 'order'";
            rsJdbc = dbBus.getRowSet(sql);
            rsJdbc.next();
            url = rsJdbc.getString("url");
            driverClassName = rsJdbc.getString("driver");
            username = rsJdbc.getString("username");
            password = rsJdbc.getString("password");
            tablename = rsJdbc.getString("tablename");
            fieldname = rsJdbc.getString("fieldname");

            // -- 打开订单数据库连接 --
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, username, password);

            // -- 取订单数据 --
            sql = "SELECT * FROM " + tablename + " WHERE " + fieldname + " = '" + orderNumber + "'";
            stmt = conn.createStatement();
            rsOrder = stmt.executeQuery(sql);

            ok("dtbOrder", rsOrder);
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getErrorCode() == 17002) {
                throw new Exception("访问订单数据库失败，请检查订单服务器网络及服务是否可用。");
            }
            throw e;
        } finally {
            try {
                if (rsOrder != null) {
                    rsOrder.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping("/getLabelByCustomer")
    private RestResult getLabelByCustomer() {
        String sql;
        String customerCode = in("customerCode");

        SqlRowSet rsLabel;
        // ------------------------------------------------
        try {
            if (customerCode.equals("")) {
                sql = "SELECT id, code, name FROM base_label ORDER BY code, name";
                rsLabel = dbBus.getRowSet(sql);
            }
            else {
                sql = "SELECT label.id, label.code, label.name FROM base_label label "
                    + "INNER JOIN cfg_label_customer cfg ON label.id = cfg.label_id "
                    + "INNER JOIN base_customer c ON cfg.customer_id = c.id "
                    + "WHERE c.code = ? ORDER BY label.code, label.name";
                rsLabel = dbBus.getRowSet(sql, customerCode);
            }
            ok("dtbLabel", rsLabel);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}