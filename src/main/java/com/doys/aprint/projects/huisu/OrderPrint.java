package com.doys.aprint.projects.huisu;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprint/projects/huisu/OrderPrint")
public class OrderPrint extends BaseController {
    @RequestMapping("/pullOrderFromUdiSystem")
    private RestResult pullOrderFromUdiSystem() {
        int newRecordCount = 0, count0 = 0, count1 = 1;

        String orderDate = in("orderDate");
        String sql;
        // ------------------------------------------------
        try {
            // -- 1. 预处理 --
            sql = "SELECT COUNT(1) FROM t_huisu_udi_order WHERE udate >= ?";
            count0 = dbBus.getInt(sql, 0, orderDate);

            // -- 2. 从UDI系统拉取生产单 --
            HuisuUdiService.pullUdiOrder(dbBus, orderDate);

            // -- 9. 返回结果 --
            sql = "SELECT COUNT(1) FROM t_huisu_udi_order WHERE udate >= ?";
            count1 = dbBus.getInt(sql, 0, orderDate);
            newRecordCount = count1 - count0;

            ok("newRecordCount", newRecordCount);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    @RequestMapping("/pullOrderDataFromUdiSystem")
    private RestResult pullOrderDataFromUdiSystem() {
        int labelMode = inInt("labelMode");

        String sql;
        String orderNumber = in("orderNumber");
        String batch;

        SqlRowSet rsOrder;
        // ------------------------------------------------
        try {
            sql = "SELECT * FROM t_huisu_udi_order WHERE single = ?";
            rsOrder = dbBus.getRowSet(sql, orderNumber);
            if (rsOrder.next()) {
                batch = rsOrder.getString("batch");
            }
            else {
                return ResultErr("生产单号：" + orderNumber + " 不存在，请检查。");
            }

            ok("batch", batch);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}