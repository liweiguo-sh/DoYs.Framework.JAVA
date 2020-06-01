package com.doys.aprint.label;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.db.DBFactory;
import com.doys.framework.core.entity.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/aprint/labelQuickPrint")
public class LabelQuickPrintController extends BaseController {
    @Autowired
    protected DBFactory dbMaster;

    @RequestMapping("/getInitData")
    private RestResult getInitData() {
        String sql;

        SqlRowSet rsLabel, rsCustomer, rsProductPn;
        // ------------------------------------------------
        try {
            sql = "SELECT id, code, name FROM ..base_label ORDER BY code, name";
            rsLabel = dbMaster.getRowSet(sql);
            ok("dtbLabel", rsLabel);

            sql = "SELECT id, code, name FROM ..base_customer ORDER BY code, name";
            rsCustomer = dbMaster.getRowSet(sql);
            ok("dtbCustomer", rsCustomer);

            sql = "SELECT pn.id, p.name product_name, pn.pn "
                + "FROM ..base_product p INNER JOIN ..base_product_pn pn ON p.id = pn.product_id ORDER BY p.name, pn.pn";
            rsProductPn = dbMaster.getRowSet(sql);
            ok("dtbProductPn", rsProductPn);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }
}