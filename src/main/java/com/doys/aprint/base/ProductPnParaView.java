package com.doys.aprint.base;
import com.doys.framework.core.view.BaseViewController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprint/base/product_pn_para_view")
public class ProductPnParaView extends BaseViewController {
    @Override
    protected boolean BeforeSave(boolean addnew, long id) throws Exception {
        String sql;
        // ------------------------------------------------
        if (addnew) {
            sql = "INSERT INTO ..base_product_para (product_id, para_code, para_name, para_value) "
                + "SELECT ? product_id, code para_code, name para_name, default_value para_value "
                + "FROM ..base_para_def WHERE category = 'customer'";
            //dbMaster.exec(sql, id);
        }
        return true;
    }
}