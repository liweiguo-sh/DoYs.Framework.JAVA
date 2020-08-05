package com.doys.aprint.label;
import com.doys.aprint.base.LabelTableService;
import com.doys.framework.config.Const;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import com.doys.framework.util.UtilString;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
@RestController
@RequestMapping("/aprint/label")
public class LabelController extends BaseController {
    @RequestMapping("/getLabelContentById")
    private RestResult getLabelContentById() {
        int labelId = inInt("labelId");
        String sql;
        SqlRowSet rs;
        // ------------------------------------------------
        try {
            sql = "SELECT * FROM base_label WHERE id = ?";
            rs = dbBus.getRowSet(sql, labelId);
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
        boolean blFind = false;
        int id = inInt("id");

        String sql;
        String content = in("content");
        String vars = in("vars");
        String varName;

        ArrayList<HashMap<String, Object>> listVars = new ArrayList<>();
        SqlRowSet rs;
        // ------------------------------------------------
        try {
            sql = "UPDATE base_label SET content = ?, vars = ? WHERE id = ?";
            dbBus.exec(sql, content, vars, id);

            // -- 添加新变量 -----------------------------------
            String[] arrVars = vars.split(Const.CHAR3);
            for (int i = 0; i < arrVars.length; i++) {
                String[] arrVar = arrVars[i].split(Const.CHAR4);
                String name = arrVar[0];
                String value = (arrVar.length == 2 ? arrVar[1] : "");

                HashMap<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("value", value);
                listVars.add(map);
            }
            LabelTableService.dynamicAddLabelTableColumn(dbBus, id, listVars);

            // -- 删除无效的旧变量 --------------------------------
            sql = "SELECT name FROM base_label_variable WHERE label_id = ?";
            rs = dbBus.getRowSet(sql, id);
            while (rs.next()) {
                blFind = false;
                varName = rs.getString("name");
                for (HashMap<String, Object> map : listVars) {
                    if (varName.equalsIgnoreCase((String) map.get("name"))) {
                        blFind = true;
                        break;
                    }
                }
                if (!blFind) {
                    LabelTableService.dynamicDelLabelTableColumn(dbBus, id, varName);
                }
            }
        } catch (Exception e) {
            return ResultErr(e);
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
            sql = "SELECT para_code, para_value FROM base_customer_para WHERE customer_id = ?";
            rsCustomerPara = dbBus.getRowSet(sql, customerId);
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
            sql = "SELECT para_code, para_value FROM base_product_pn_para WHERE product_pn_id = ?";
            rsProductPnPara = dbBus.getRowSet(sql, customerId);
            ok("dtbProductPnPara", rsProductPnPara);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }
}
