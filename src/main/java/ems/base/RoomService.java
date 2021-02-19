package ems.base;
import doys.framework.database.DBFactory;
public class RoomService {
    public static void updateFullname(DBFactory dbBus) throws Exception {
        String sql = "UPDATE base_room SET fullname = CONCAT_WS(' \\\\ ', area_name, building_name, floor_name, name)";
        dbBus.exec(sql);
    }
}