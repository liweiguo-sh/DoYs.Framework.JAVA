package ems.monitor;
import doys.framework.core.base.BaseControllerStd;
import doys.framework.core.entity.RestResult;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("ems/monitor/overview_meter")
public class OverviewMeter extends BaseControllerStd {
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

    @RequestMapping("/getWaterMeter")
    private RestResult getWaterMeter() {
        String sql;
        String locationKey = in("locationKey");
        String sqlLocation = getSqlByLocationKey(locationKey);
        String deviceStatus = in("deviceStatus");
        String sqlDeviceStatus = "";

        SqlRowSet rs;
        // ------------------------------------------------
        try {
            if (!deviceStatus.equals("")) {
                sqlDeviceStatus = " AND status_run = " + deviceStatus;
            }
            sql = "SELECT room.fullname, t.id, t.name, t.rt_num, t.status_run, t.status_run_text "
                + "FROM base_water_meter t INNER JOIN base_room room ON t.room_id = room.id "
                + "WHERE " + sqlLocation + sqlDeviceStatus;
            rs = dbTenant.getRowSet(sql);
            ok("dtbWaterMeter", rs);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    @RequestMapping("/getElecticityMeter")
    private RestResult getElecticityMeter() {
        String sql;
        String locationKey = in("locationKey");
        String sqlLocation = getSqlByLocationKey(locationKey);
        String deviceStatus = in("deviceStatus");
        String sqlDeviceStatus = "";

        SqlRowSet rs;
        // ------------------------------------------------
        try {
            if (!deviceStatus.equals("")) {
                sqlDeviceStatus = " AND status_run = " + deviceStatus;
            }
            sql = "SELECT room.fullname, t.id, t.name, t.rt_num, t.status_run, t.status_run_text "
                + "FROM base_electricity_meter t INNER JOIN base_room room ON t.room_id = room.id "
                + "WHERE " + sqlLocation + sqlDeviceStatus;
            rs = dbTenant.getRowSet(sql);
            ok("dtbWaterMeter", rs);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}