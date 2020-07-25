package com.doys.aprint.projects.renben;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import com.doys.framework.database.DBFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
@RestController
@RequestMapping("/aprint/projects/renben/order_print")
public class OrderPrint extends BaseController {
    @RequestMapping("/getOrderByOrderNumber")
    private RestResult getOrderByOrderNumber() {
        String orderNumber = in("orderNumber");
        String sql;

        SqlRowSet rsOrder;
        // ------------------------------------------------
        try {
            sql = "SELECT COUNT(1) FROM t_order WHERE order_number = ?";
            if (dbBus.getInt(sql, DBFactory.NULL_NUMBER, orderNumber) == 0) {
                //getOrderFromOracle(orderNumber);
            }

            sql = "SELECT * FROM T_ORDER WHERE order_number = ?";
            rsOrder = dbBus.getRowSet(sql, orderNumber);
            ok("dtbOrder", rsOrder);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
    private void getOrderFromOracle(String orderNumber) throws Exception {
        String sql;
        String url = "jdbc:oracle:thin:@(description=(address=(protocol=tcp)(port=1521)(host=192.168.169.230))(connect_data=(service_name=orcl)))";
        String driverClassName = "oracle.jdbc.driver.OracleDriver";
        String username = "backward", password = "backward";

        String columnName, columnValue;
        String sqlInsert, sqlFields = "", sqlValues = "";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rsOrder = null;
        ResultSetMetaData rsmd;
        // ------------------------------------------------
        try {
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, username, password);

            sql = "SELECT * FROM T_ORDER WHERE order_number = '" + orderNumber + "'";
            stmt = conn.createStatement();
            rsOrder = stmt.executeQuery(sql);

            if (rsOrder.next()) {
                rsmd = rsOrder.getMetaData();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    columnName = rsmd.getColumnName(i);
                    columnValue = rsOrder.getString(columnName);
                    sqlFields += "," + columnName;
                    if (columnValue != null) {
                        sqlValues += ", '" + columnValue + "'";
                    }
                    else {
                        sqlValues += ",null";
                    }
                }
                sqlFields = sqlFields.substring(1);
                sqlValues = sqlValues.substring(1);
                sqlInsert = "INSERT INTO T_ORDER (" + sqlFields + ") VALUES (" + sqlValues + ")";
                dbBus.exec(sqlInsert);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getErrorCode() == 17002) {
                throw new Exception("访问订单数据库失败，请检查订单服务器网络及服务是否可用。");
            }
            throw e;
        } catch (Exception e) {
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
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    

}