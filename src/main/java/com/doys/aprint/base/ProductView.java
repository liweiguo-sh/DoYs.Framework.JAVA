package com.doys.aprint.base;
import com.doys.framework.core.view.BaseViewController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprint/base/product_view")
public class ProductView extends BaseViewController {
    @Override
    protected boolean AfterDelete(long id) throws Exception {
        String sql;

        sql = "DELETE FROM ..base_product_pn WHERE product_id = ?";
        dbSys.exec(sql, id);

        sql = "DELETE FROM ..base_product_para WHERE product_id = ?";
        dbSys.exec(sql, id);

        sql = "DELETE FROM ..base_product_pn_para WHERE product_id = ?";
        dbSys.exec(sql, id);

        return true;
    }
}