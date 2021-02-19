package ems.base;
import doys.framework.core.view.BaseViewControllerTenant;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ems/base/floor_view")
public class FloorViewTenant extends BaseViewControllerTenant {
    @Override
    protected boolean AfterSave(boolean addnew, long id) throws Exception {
        String sql;
        String name = in("name");
        // ------------------------------------------------
        if (addnew) {
            sql = "UPDATE base_floor f INNER JOIN ..base_building b ON f.building_id = b.id "
                + "SET f.area_id = b.area_id, f.area_name = b.area_name, f.building_name = b.name WHERE f.id = ?";
            dbTenant.exec(sql, id);
        }
        else {
            sql = "UPDATE base_room SET floor_name = ? WHERE floor_id = ?";
            dbTenant.exec(sql, name, id);

            RoomService.updateFullname(dbTenant);
        }
        // ------------------------------------------------
        return true;
    }

    @Override
    protected boolean BeforeDelete(long id) throws Exception {
        int result = 0;

        String sql;
        // ------------------------------------------------
        sql = "SELECT COUNT(1) FROM base_room WHERE floor_id = ?";
        result = dbTenant.getInt(sql, 0, id);
        if (result > 0) {
            err("当前楼层存在下级房间数据，不能删除。");
            return false;
        }
        // ------------------------------------------------
        return true;
    }
}