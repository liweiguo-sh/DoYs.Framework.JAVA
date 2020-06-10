package com.doys.aprint.label;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.db.DBFactory;
import com.doys.framework.core.entity.RestResult;
import com.doys.framework.util.UtilString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/aprint/label")
public class LabelController extends BaseController {
    @Autowired
    protected DBFactory dbMaster;

    @RequestMapping("/getLabelContentById")
    private RestResult getLabelContentById() {
        int labelId = inInt("labelId");
        String sql;
        SqlRowSet rs;
        // ------------------------------------------------
        try {
            sql = "SELECT * FROM ..base_label WHERE id = ?";
            rs = dbMaster.getRowSet(sql, labelId);
            if (rs.next()) {
                ok("id", rs.getString("id"));
                ok("type", rs.getString("type"));
                ok("name", rs.getString("name"));
                ok("version", rs.getString("version"));
                ok("content", UtilString.KillNull(rs.getString("content")));
                ok("vars", UtilString.KillNull(rs.getString("vars")));
            }
            else {
                return ResultErr("标签记录不存在，请检查。");
            }
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }
    @RequestMapping("/saveLabelContent")
    private RestResult saveLabelContent() {
        int id = inInt("id");

        String sql;
        String content = in("content");
        String vars = in("vars");
        // ------------------------------------------------
        try {
            sql = "UPDATE ..base_label SET content = ?, vars = ? WHERE id = ?";
            dbMaster.exec(sql, content, vars, id);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }

    @RequestMapping("/getCustomerParaByCustomerId")
    private RestResult getCustomerParaByCustomerId() {
        int customerId = inInt("customerId");

        String sql;
        SqlRowSet rsCustomerPara;
        // ------------------------------------------------
        try {
            sql = "SELECT para_code, para_value FROM ..base_customer_para WHERE customer_id = ?";
            rsCustomerPara = dbMaster.getRowSet(sql, customerId);
            ok("dtbCustomerPara", rsCustomerPara);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }
    @RequestMapping("/getProductPnParaByProductPnId")
    private RestResult getProductPnParaByProductPnId() {
        int customerId = inInt("productPnId");

        String sql;
        SqlRowSet rsProductPnPara;
        // ------------------------------------------------
        try {
            sql = "SELECT para_code, para_value FROM ..base_product_pn_para WHERE product_pn_id = ?";
            rsProductPnPara = dbMaster.getRowSet(sql, customerId);
            ok("dtbProductPnPara", rsProductPnPara);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }
}
