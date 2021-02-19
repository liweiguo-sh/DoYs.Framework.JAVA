package ems.base;
import doys.framework.core.view.BaseViewControllerTenant;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ems/base/water_meter_view")
public class WaterMeterViewTenant extends BaseViewControllerTenant {
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
        sql = "SELECT COUNT(1) FROM core_water WHERE water_meter_id = ?";
        result = dbTenant.getInt(sql, 0, id);
        if (result > 0) {
            err("当前水表存在历史数据，不能删除。");
            return false;
        }
        // ------------------------------------------------
        return true;
    }
}