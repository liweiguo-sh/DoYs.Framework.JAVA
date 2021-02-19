package ems.monitor;
import doys.framework.a0.Const;
import doys.framework.core.base.BaseControllerStd;
import doys.framework.core.entity.RestResult;
import doys.framework.core.ex.UnexpectedException;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/ems/monitor/location")
public class Location extends BaseControllerStd {
    @RequestMapping("/getSubLocation")
    private RestResult getSubLocation() {
        String sql, locationPath = "";
        String locationKey = in("locationKey");
        String[] arrKey = locationKey.split("_");

        SqlRowSet rsLocation = null, rsSubLocation = null;
        // ------------------------------------------------
        try {
            if (arrKey.length == 1) {
                sql = "SELECT id, name FROM base_area ORDER BY sequence, name";
                rsSubLocation = dbTenant.getRowSet(sql);
            }
            else if (arrKey.length == 2) {
                sql = "SELECT id, name FROM base_building WHERE area_id = ? ORDER BY sequence, name";
                rsSubLocation = dbTenant.getRowSet(sql, arrKey[1]);

                sql = "SELECT id, name FROM base_area WHERE id = ?";
                rsLocation = dbTenant.getRowSet(sql, arrKey[1]);
                rsLocation.first();
                locationPath = "ROOT_" + rsLocation.getInt("id") + Const.CHAR2 + rsLocation.getString("name");
            }
            else if (arrKey.length == 3) {
                sql = "SELECT id, name FROM base_floor WHERE building_id = ? ORDER BY sequence, name";
                rsSubLocation = dbTenant.getRowSet(sql, arrKey[2]);

                sql = "SELECT area_id, area_name, id, name FROM base_building WHERE id = ?";
                rsLocation = dbTenant.getRowSet(sql, arrKey[2]);
                rsLocation.first();
                locationPath = "ROOT_" + rsLocation.getInt("area_id") + Const.CHAR2 + rsLocation.getString("area_name") + Const.CHAR1
                    + "ROOT_" + rsLocation.getInt("area_id") + "_" + rsLocation.getInt("id") + Const.CHAR2 + rsLocation.getString("name");
            }
            else if (arrKey.length == 4) {
                sql = "SELECT id, name FROM base_room WHERE floor_id = ? ORDER BY sequence, name";
                rsSubLocation = dbTenant.getRowSet(sql, arrKey[3]);

                sql = "SELECT area_id, area_name, building_id, building_name, id, name FROM base_floor WHERE id = ?";
                rsLocation = dbTenant.getRowSet(sql, arrKey[3]);
                rsLocation.first();
                locationPath = "ROOT_" + rsLocation.getInt("area_id") + Const.CHAR2 + rsLocation.getString("area_name") + Const.CHAR1
                    + "ROOT_" + rsLocation.getInt("area_id") + "_" + rsLocation.getInt("building_id") + Const.CHAR2 + rsLocation.getString("building_name") + Const.CHAR1
                    + "ROOT_" + rsLocation.getInt("area_id") + "_" + rsLocation.getInt("building_id") + "_" + rsLocation.getInt("id") + Const.CHAR2 + rsLocation.getString("name");
            }
            else if (arrKey.length == 5) {
                sql = "SELECT area_id, area_name, building_id, building_name, floor_id, floor_name, id, name FROM base_room WHERE id = ?";
                rsLocation = dbTenant.getRowSet(sql, arrKey[4]);
                rsLocation.first();
                locationPath = "ROOT_" + rsLocation.getInt("area_id") + Const.CHAR2 + rsLocation.getString("area_name") + Const.CHAR1
                    + "ROOT_" + rsLocation.getInt("area_id") + "_" + rsLocation.getInt("building_id") + Const.CHAR2 + rsLocation.getString("building_name") + Const.CHAR1
                    + "ROOT_" + rsLocation.getInt("area_id") + "_" + rsLocation.getInt("building_id") + "_" + rsLocation.getInt("floor_id") + Const.CHAR2 + rsLocation.getString("floor_name") + Const.CHAR1
                    + "ROOT_" + rsLocation.getInt("area_id") + "_" + rsLocation.getInt("building_id") + "_" + rsLocation.getInt("floor_id") + "_" + rsLocation.getInt("id") + Const.CHAR2 + rsLocation.getString("name");
            }
            else {
                throw new UnexpectedException();
            }

            ok("locationPath", locationPath);
            ok("dtbSubLocation", rsSubLocation);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }
}