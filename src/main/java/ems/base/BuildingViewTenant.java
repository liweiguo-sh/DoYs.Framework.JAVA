package ems.base;
import doys.framework.core.view.BaseViewControllerTenant;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ems/base/building_view")
public class BuildingViewTenant extends BaseViewControllerTenant {
    @Override
    protected boolean AfterSave(boolean addnew, long id) throws Exception {
        String sql;
        String name = in("name");
        // ------------------------------------------------
        if (addnew) {
            sql = "UPDATE base_building b INNER JOIN base_area a ON b.area_id = a.id "
                + "SET b.area_name = a.name WHERE b.id = ?";
            dbTenant.exec(sql, id);
        }
        else {
            sql = "UPDATE base_floor SET building_name = ? WHERE building_id = ?";
            dbTenant.exec(sql, name, id);

            sql = "UPDATE base_room SET building_name = ? WHERE building_id = ?";
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
        sql = "SELECT COUNT(1) FROM base_floor WHERE building_id = ?";
        result = dbTenant.getInt(sql, 0, id);
        if (result > 0) {
            err("当前建筑存在下级楼层数据，不能删除。");
            return false;
        }
        // ------------------------------------------------
        return true;
    }
}