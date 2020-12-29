/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-11-24
 * 绿客门Controller类
 *****************************************************************************/
package com.doys.aprint.projects.lvkemen;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import com.doys.framework.dts.base.ENTITY_RECORD;
import com.doys.framework.util.UtilString;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
@RestController
@RequestMapping("/aprint/lvkemen")
public class LvkemenController extends BaseController {
    @RequestMapping("/getAllShop")
    private RestResult getAllShop() {
        String sql;

        ArrayList<HashMap<String, String>> listShop = null;
        HashMap<String, String> map;
        SqlRowSet rs;
        // ------------------------------------------------
        try {
            // -- 1. 读取当前店铺信息 --
            sql = "SELECT * FROM T_SHOP";
            rs = dbBus.getRowSet(sql);
            while (rs.next()) {
                if (listShop == null) {
                    listShop = new ArrayList<>();
                }
                map = new ManagedMap<>();
                map.put("shopId", rs.getString("id"));
                map.put("shopName", rs.getString("name"));
                map.put("shopAddress", rs.getString("address"));
                listShop.add(map);
            }

            // -- 2. 通过接口拉取所有店铺 --
            if (listShop == null) {
                listShop = LvKementHulala.getAllShop();
            }

            // -- 9. 返回店铺列表 --
            ok("shops", listShop);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
    @RequestMapping("/getOpenFood")
    private RestResult getOpenFood() {
        int shopId = inInt("shopId");

        ArrayList<HashMap<String, String>> listFood;
        // ------------------------------------------------
        try {
            listFood = LvKementHulala.getOpenFood(shopId);
            if (listFood != null) {
                LvKementHulala.saveFoods(dbBus, shopId, listFood);
            }

            ok("foods", listFood);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    @RequestMapping("/getFoodListForPrint")
    private RestResult getFoodListForPrint() {
        int shopId = inInt("shopId");

        String sql;

        SqlRowSet rsFood;
        // ------------------------------------------------
        try {
            sql = "SELECT * FROM t_food WHERE shop_id = ? ORDER BY food_name";
            rsFood = dbBus.getRowSet(sql, shopId);

            ok("dtbFood", rsFood);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    @RequestMapping("/getLabels")
    private RestResult getLabels() {
        String sql;

        SqlRowSet rsLabel;
        // ------------------------------------------------
        try {
            sql = "SELECT id, code, name FROM base_label ORDER BY code, name";
            rsLabel = dbBus.getRowSet(sql);
            ok("dtbLabel", rsLabel);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
    @RequestMapping("/getLabelById")
    private RestResult getLabelById() {
        int labelId = inInt("labelId");
        String sql;
        SqlRowSet rsLabel;
        // ------------------------------------------------
        try {
            sql = "SELECT * FROM base_label WHERE id = ?";
            rsLabel = dbBus.getRowSet(sql, labelId);
            ok("dtbLabel", rsLabel);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    // -- 打印登记 ----------------------------------------------------------------
    @RequestMapping("/savePrintRecord")
    private RestResult savePrintRecord() {
        int labelId = inInt("labelId");
        int copies = inInt("copies");

        String userPk, taskPk;
        // ------------------------------------------------
        try {
            userPk = this.ssValue("userPk");
            taskPk = UtilString.getSN(dbBus, "task_pk", "", "T-{yy}{MM}{dd}-{5}");

            ENTITY_RECORD entity = new ENTITY_RECORD(dbBus, "core_task");
            entity
                .setValue("label_id", labelId)
                .setValue("pk", taskPk)
                .setValue("qty", copies)
                .setValue("creator", userPk);
            entity.Save();
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}