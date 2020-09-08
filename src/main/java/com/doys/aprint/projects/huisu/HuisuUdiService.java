package com.doys.aprint.projects.huisu;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.core.ex.CommonException;
import com.doys.framework.database.DBFactory;
import com.doys.framework.dts.base.ENTITY_RECORD;
import com.doys.framework.util.UtilHttp;
import com.doys.framework.util.UtilString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class HuisuUdiService extends BaseService {
    // ------------------------------------------------------------------------
    public static void pullUdiOrder(DBFactory dbBus, String udate) throws Exception {
        int result;

        String urlGetProduction = "http://udi.itrace.cn/udi/f/p/synchronizedProduction";
        String sql, id;
        String ts, sk;

        HashMap<String, String> mapForm = new HashMap<>();
        HashMap<String, Object> mapResponse;
        LinkedHashMap<String, String> map;
        // -- 1. 发送请求，从UDI系统拉取生产单 -------------------------
        ts = HuisuConst.getTS();
        sk = HuisuConst.getSK(ts);

        mapForm.put("ts", ts);
        mapForm.put("sk", sk);
        mapForm.put("udate", udate);

        mapResponse = UtilHttp.reqFormResJson(urlGetProduction, mapForm);
        if (!UtilString.equals((String) mapResponse.get("result"), "true")) {
            throw new CommonException((String) mapResponse.get("message"));
        }
        // -- 2. 处理返回结果 -----------------------------------
        ArrayList<LinkedHashMap<String, String>> maps = (ArrayList<LinkedHashMap<String, String>>) mapResponse.get("data");
        int size = maps.size();
        for (int i = 0; i < size; i++) {
            map = maps.get(i);
            id = map.get("id");
            sql = "SELECT COUNT(1) FROM t_huisu_udi_order WHERE id = ?";
            result = dbBus.getInt(sql, 0, id);

            ENTITY_RECORD entity;
            if (result == 0) {
                entity = new ENTITY_RECORD(dbBus, "t_huisu_udi_order");
            }
            else {
                entity = new ENTITY_RECORD(dbBus, "t_huisu_udi_order", id);
            }
            entity
                .setValue("id", map.get("id"))
                .setValue("batch", map.get("batch"))
                .setValue("single", map.get("single"))
                .setValue("productId", map.get("productId"))
                .setValue("name", map.get("name"))
                .setValue("planNumber", map.get("planNumber"))
                .setValue("doNumber", map.get("doNumber"))
                .setValue("pdate", map.get("pdate"))
                .setValue("cname", map.get("cname"))
                .setValue("cdate", map.get("cdate"))
                .setValue("uname", map.get("uname"))
                .setValue("udate", map.get("udate"))
                .setValue("state", map.get("state"))
                .setValue("isDel", map.get("isDel"))
                .setValue("isNewRecord", map.get("isNewRecord"))
                .Save();
        }
    }
}