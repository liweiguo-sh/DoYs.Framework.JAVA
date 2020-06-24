package com.doys.aprint.base;
import com.doys.framework.core.view.BaseViewController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprint/base/product_pn_view")
public class ProductPnView extends BaseViewController {
    @Override
    protected boolean AfterSave(boolean addnew, long id) throws Exception {
        int productId = inInt("product_id");

        String sql;
        // ------------------------------------------------
        sql = "DELETE FROM base_product_pn_para WHERE product_id <> ?";
        dbBus.exec(sql, productId);

        sql = "INSERT INTO base_product_pn_para (product_id, product_para_id, product_pn_id, para_code, para_name, para_value) "
            + "SELECT product_id, id product_para_id, ? product_pn_id, code para_code, name para_name, default_value para_value "
            + "FROM base_product_para "
            + "WHERE product_id = ? AND code NOT IN (SELECT para_code FROM base_product_pn_para WHERE product_pn_id = ?)";
        dbBus.exec(sql, id, productId, id);

        return true;
    }

    @Override
    protected boolean AfterDelete(long id) throws Exception {
        String sql = "DELETE FROM base_product_pn_para WHERE product_pn_id = ?";
        dbBus.exec(sql, id);

        return true;
    }
}