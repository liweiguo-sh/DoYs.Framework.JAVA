package com.doys.aprint.base;
import com.doys.framework.core.view.BaseViewController;
import com.doys.framework.util.UtilDate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprint/base/customer_view")
public class CustomerView extends BaseViewController {
    @Override
    protected boolean BeforeSave(boolean addnew, long id) {
        String sql = "", strValue = "";
        String strDate = UtilDate.getDateTimeString();
        // ------------------------------------------------

        return true;
    }
    @Override
    protected boolean AfterSave(boolean addnew, long id) throws Exception {
        String sql;
        // ------------------------------------------------
        if (addnew) {
            sql = "INSERT INTO base_customer_para (customer_id, para_code, para_name, para_value) "
                + "SELECT ? customer_id, code para_code, name para_name, default_value para_value "
                + "FROM base_para_def WHERE category = 'customer'";
            dbBus.exec(sql, id);
        }

        return true;
    }

    @Override
    protected boolean AfterDelete(long id) throws Exception {
        String sql = "DELETE FROM base_customer_para WHERE customer_id = ?";
        dbBus.exec(sql, id);

        return true;
    }
}