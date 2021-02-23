package ems.base;
import doys.framework.core.view.BaseViewController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ems/base/electricity_meter_view")
public class ElectricityMeterView extends BaseViewController {
    @Override
    protected boolean AfterSave(boolean addnew, long id) throws Exception {
        String sql;
        // ------------------------------------------------
        if (addnew) {

        }
        else {

        }
        // ------------------------------------------------
        return true;
    }

    @Override
    protected boolean BeforeDelete(long id) throws Exception {
        int result = 0;

        String sql;
        // ------------------------------------------------
        sql = "SELECT COUNT(1) FROM core_electricity WHERE electricity_meter_id = ?";
        result = dbBus.getInt(sql, 0, id);
        if (result > 0) {
            err("当前电表存在历史数据，不能删除。");
            return false;
        }
        // ------------------------------------------------
        return true;
    }
}