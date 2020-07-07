package com.doys.ems.monitor;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("ems/monitor/overview_water_meter")
public class OverviewWaterMeter extends BaseController {
    @RequestMapping("/getWaterMeter")
    private RestResult getWaterMeter() {
        String sql;
        String locationKey = in("locationKey");
        String sqlLocation = getSqlByLocationKey(locationKey);

        SqlRowSet rs;
        // ------------------------------------------------
        try {
            sql = "SELECT room.fullname, t.id, t.name, t.rt_num, t.status_run, t.status_run_text "
                + "FROM base_water_meter t INNER JOIN base_room room ON t.room_id = room.id "
                + "WHERE " + sqlLocation;
            rs = dbBus.getRowSet(sql);
            ok("dtbWaterMeter", rs);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
    private String getSqlByLocationKey(String locationKey) {
        String sqlReturn;
        String[] arr = locationKey.split("_");
        int nLevel = arr.length;

        if (nLevel == 5) {
            sqlReturn = "room.id = " + arr[nLevel - 1];
        }
        else if (nLevel == 4) {
            sqlReturn = "room.floor_id = " + arr[nLevel - 1];
        }
        else if (nLevel == 3) {
            sqlReturn = "room.building_id = " + arr[nLevel - 1];
        }
        else if (nLevel == 2) {
            sqlReturn = "room.area_id = " + arr[nLevel - 1];
        }
        else {
            sqlReturn = "1 = 1";
        }
        return sqlReturn;
    }
}