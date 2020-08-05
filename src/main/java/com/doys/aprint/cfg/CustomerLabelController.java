package com.doys.aprint.cfg;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprint/cfg/customer_label")
public class CustomerLabelController extends BaseController {
    @RequestMapping("/getCustomerAndLabel")
    private RestResult getCustomerAndLabel() {
        String sql;

        SqlRowSet rsCustomer, rsLabel;
        // ------------------------------------------------
        try {
            sql = "SELECT id, name FROM base_customer ORDER BY name";
            rsCustomer = dbBus.getRowSet(sql);
            ok("dtbCustomer", rsCustomer);

            sql = "SELECT id, code, name FROM base_label ORDER BY name, code";
            rsLabel = dbBus.getRowSet(sql);
            ok("dtbLabel", rsLabel);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    @RequestMapping("/getLabelByCustomer")
    private RestResult getLabelByCustomer() {
        int customerId = inInt("customerId");

        String sql;

        SqlRowSet rsLabel;
        // ------------------------------------------------
        try {
            sql = "SELECT label_id FROM cfg_label_customer WHERE customer_id = ?";
            rsLabel = dbBus.getRowSet(sql, customerId);
            ok("dtbLabel", rsLabel);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    @RequestMapping("/saveCustomerLabel")
    private RestResult saveCustomerLabel() {
        int customerId = inInt("customerId");

        String sql;
        String labelIds = in("labelIds");
        // ------------------------------------------------
        try {
            sql = "DELETE FROM cfg_label_customer WHERE customer_id = ?";
            dbBus.exec(sql, customerId);

            if (labelIds.length() > 0) {
                sql = "INSERT INTO cfg_label_customer (label_id, customer_id) "
                    + "SELECT id label_id, ? customer_id FROM base_label WHERE id IN (" + labelIds + ")";
                dbBus.exec(sql, customerId);
            }
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}