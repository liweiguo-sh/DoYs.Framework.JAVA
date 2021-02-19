package ems.home;
import doys.framework.core.base.BaseControllerStd;
import doys.framework.core.entity.RestResult;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/ems/home/echats")
public class HomeECharts extends BaseControllerStd {
    @RequestMapping("/getLeft2")
    private RestResult getLeft2() {
        String sql;

        SqlRowSet rs;
        // ------------------------------------------------
        try {
            sql = "SELECT '水表' y, COUNT(1) s FROM core_water_alarm UNION ALL "
                + "SELECT '电表' y, COUNT(1) s FROM core_electricity_alarm UNION ALL "
                + "SELECT '燃气表' y, SUM(3) s FROM core_electricity_alarm";
            rs = dbTenant.getRowSet(sql);
            ok("dtb", rs);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }

    @RequestMapping("/getMiddleTop")
    private RestResult getMiddleTop() {
        String sql;

        SqlRowSet rs;
        // ------------------------------------------------
        try {
            sql = "SELECT DATE_FORMAT(bus_day, '%d') x, SUM(num) s FROM core_electricity_day GROUP BY DATE_FORMAT(bus_day, '%d')";
            rs = dbTenant.getRowSet(sql);
            ok("dtb", rs);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }
    @RequestMapping("/getMiddleMiddleLeft")
    private RestResult getMiddleMiddleLeft() {
        String sql;

        SqlRowSet rs;
        // ------------------------------------------------
        try {
            sql = "SELECT DATE_FORMAT(bus_day, '%d') x, SUM(num) s FROM core_water_day GROUP BY DATE_FORMAT(bus_day, '%d')";
            rs = dbTenant.getRowSet(sql);
            ok("dtb", rs);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }
    @RequestMapping("/getMiddleMiddleRight")
    private RestResult getMiddleMiddleRight() {
        String sql;

        SqlRowSet rs;
        // ------------------------------------------------
        try {
            sql = "SELECT DATE_FORMAT(bus_day, '%d') x, SUM(num) s FROM core_gas_day GROUP BY DATE_FORMAT(bus_day, '%d')";
            rs = dbTenant.getRowSet(sql);
            ok("dtb", rs);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }

    @RequestMapping("/getMiddleBottomLeft")
    private RestResult getMiddleBottomLeft() {
        String sql;

        SqlRowSet rs;
        // ------------------------------------------------
        try {
            sql = "SELECT building_name name, SUM(rt_num) value "
                + "FROM core_water_day t INNER JOIN base_water_meter wm ON t.water_meter_id = wm.id INNER JOIN base_room room ON wm.room_id = room.id "
                + "GROUP BY building_name ORDER BY building_name";
            rs = dbTenant.getRowSet(sql);
            ok("dtb", rs);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }
    @RequestMapping("/getMiddleBottomCenter")
    private RestResult getMiddleBottomCenter() {
        String sql;

        SqlRowSet rs;
        // ------------------------------------------------
        try {
            sql = "SELECT building_name name, SUM(rt_num) value "
                + "FROM core_water_day t INNER JOIN base_water_meter wm ON t.water_meter_id = wm.id INNER JOIN base_room room ON wm.room_id = room.id "
                + "GROUP BY building_name ORDER BY building_name";
            rs = dbTenant.getRowSet(sql);
            ok("dtb", rs);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }
    @RequestMapping("/getMiddleBottomRight")
    private RestResult getMiddleBottomRight() {
        String sql;

        SqlRowSet rs;
        // ------------------------------------------------
        try {
            sql = "SELECT building_name name, SUM(rt_num) value "
                + "FROM core_water_day t INNER JOIN base_water_meter wm ON t.water_meter_id = wm.id INNER JOIN base_room room ON wm.room_id = room.id "
                + "GROUP BY building_name ORDER BY building_name";
            rs = dbTenant.getRowSet(sql);
            ok("dtb", rs);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }
}
