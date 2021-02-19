package ems.base;
import doys.framework.core.view.BaseViewControllerTenant;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ems/base/area_view")
public class AreaViewTenant extends BaseViewControllerTenant {
    @Override
    protected boolean AfterSave(boolean addnew, long id) throws Exception {
        String sql;
        String name = in("name");
        // ------------------------------------------------
        if (addnew) {
        }
        else {
            sql = "UPDATE base_building SET area_name = ? WHERE area_id = ?";
            dbTenant.exec(sql, name, id);

            sql = "UPDATE base_floor SET area_name = ? WHERE area_id = ?";
            dbTenant.exec(sql, name, id);

            sql = "UPDATE base_room SET area_name = ? WHERE area_id = ?";
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
        sql = "SELECT COUNT(1) FROM base_building WHERE area_id = ?";
        result = dbTenant.getInt(sql, 0, id);
        if (result > 0) {
            err("当前区域存在下级建筑数据，不能删除。");
            return false;
        }
        // ------------------------------------------------
        return true;
    }
}