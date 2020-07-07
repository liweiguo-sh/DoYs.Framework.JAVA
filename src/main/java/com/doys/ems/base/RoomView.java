package com.doys.ems.base;
import com.doys.framework.core.view.BaseViewController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ems/base/room_view")
public class RoomView extends BaseViewController {
    @Override
    protected boolean AfterSave(boolean addnew, long id) throws Exception {
        String sql;
        // ------------------------------------------------
        if (addnew) {
            sql = "UPDATE base_room r INNER JOIN base_floor f ON r.floor_id = f.id "
                + "SET r.area_id = f.area_id, r.area_name = f.area_name, r.building_id = f.building_id, r.building_name = f.building_name, r.floor_name = f.name "
                + "WHERE r.id = ?";
            dbBus.exec(sql, id);
        }
        else {
            sql = "UPDATE base_room SET fullname = CONCAT_WS(' \\\\ ', area_name, building_name, floor_name, name) WHERE id = ?";
            dbBus.exec(sql, id);
        }
        // ------------------------------------------------
        return true;
    }

    @Override
    protected boolean BeforeDelete(long id) throws Exception {
        int result = 0;

        String sql;
        // ------------------------------------------------
        sql = "SELECT COUNT(1) FROM ..base_water_meter WHERE room_id = ?";
        result = dbSys.getInt(sql, id);
        if (result > 0) {
            err("当前房间存在水表设备数据，不能删除。");
            return false;
        }
        // ------------------------------------------------
        return true;
    }
}