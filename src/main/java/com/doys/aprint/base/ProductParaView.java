package com.doys.aprint.base;
import com.doys.framework.core.view.BaseViewController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprint/base/product_para_view")
public class ProductParaView extends BaseViewController {
    @Override
    protected boolean AfterSave(boolean addnew, long id) throws Exception {
        int productId = inInt("product_id");

        String sql;
        String code = in("code");
        String name = in("name");
        String defaultValue = in("default_value");
        // ------------------------------------------------
        if (addnew) {
            sql = "INSERT INTO ..base_product_pn_para (product_id, product_para_id, product_pn_id, para_code, para_name, para_value) "
                + "SELECT product_id, ? product_para_id, id product_pn_id, ? para_code, ? para_name, ? para_value "
                + "FROM ..base_product_pn WHERE product_id = ?";
            dbSys.exec(sql, id, code, name, defaultValue, productId);
        }
        else {
            sql = "UPDATE ..base_product_pn_para SET para_code = ?, para_name = ? WHERE product_para_id = ?";
            dbSys.exec(sql, code, name, id);
        }

        return true;
    }

    @Override
    protected boolean AfterDelete(long id) throws Exception {
        String sql = "DELETE FROM ..base_product_pn_para WHERE product_para_id = ?";
        dbSys.exec(sql, id);

        return true;
    }
}