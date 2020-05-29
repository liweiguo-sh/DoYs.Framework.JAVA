package com.doys.aprint.base;
import com.doys.framework.core.view.BaseViewController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprint/base/customer_para_def_view")
public class CustomerParaDefView extends BaseViewController {
    @Override
    protected boolean AfterSave(boolean addnew, long id) throws Exception {
        String sql;
        String code = in("code");
        String name = in("name");
        String defaultValue = in("default_value");

        sql = "INSERT INTO ..base_customer_para (customer_id, para_code, para_name, para_value) "
            + "SELECT id customer_id, ? para_code, ? para_name, ? para_value FROM ..base_customer "
            + "WHERE id NOT IN (SELECT customer_id FROM ..base_customer_para WHERE para_code = ?)";
        dbMaster.exec(sql, code, name, defaultValue, code);

        return true;
    }
    @Override
    protected boolean AfterDelete(long id) throws Exception {
        String sql;
        String code = in("code");

        sql = "DELETE FROM ..base_customer_para WHERE para_code = ?";
        dbMaster.exec(sql, code);

        return true;
    }
}
