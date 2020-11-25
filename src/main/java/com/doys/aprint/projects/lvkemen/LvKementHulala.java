/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-11-24
 * 绿客门的Hualala服务类
 *****************************************************************************/
package com.doys.aprint.projects.lvkemen;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.core.ex.UnexpectedException;
import com.doys.framework.database.DBFactory;
import com.doys.framework.database.dtb.DataTable;
import com.doys.thirdparty.hualala.util.ApiConst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class LvKementHulala extends BaseService {
    public static ArrayList<HashMap<String, String>> getAllShop() throws Exception {
        String url = ApiConst.getUrl_getAllShop(LvkemenConst.TESTING);
        Map<String, String> mapShop;
        HashMap<String, String> map2;
        Map<String, Object> map = ApiConst.sendPost(url, LvkemenConst.GROUP_ID, 0, LvkemenConst.APP_KEY, LvkemenConst.APP_SECRET);
        if (!map.get("code").toString().equals("000")) {
            throw new UnexpectedException(map.get("message").toString());
        }

        ArrayList<HashMap<String, String>> list2 = new ArrayList<>();
        ArrayList<Map<String, String>> lstShop = (ArrayList<Map<String, String>>) ((Map<String, Object>) map.get("data")).get("shopInfoList");
        for (int i = 0; i < lstShop.size(); i++) {
            mapShop = lstShop.get(i);

            map2 = new HashMap<>();
            map2.put("shopId", mapShop.get("shopID"));
            map2.put("shopName", mapShop.get("shopName"));
            map2.put("shopAddress", mapShop.get("shopAddress"));
            list2.add(map2);
        }

        return list2;
    }
    public static ArrayList<HashMap<String, String>> getOpenFood(int shopId) throws Exception {
        String url = ApiConst.getUrl_getOpenFood(LvkemenConst.TESTING);
        Map<String, Object> mapResponse = ApiConst.sendPost(url, LvkemenConst.GROUP_ID, shopId, LvkemenConst.APP_KEY, LvkemenConst.APP_SECRET);
        if (!mapResponse.get("code").toString().equals("000")) {
            throw new UnexpectedException(mapResponse.get("message").toString());
        }

        ArrayList<HashMap<String, String>> listReturn = new ArrayList<>();
        ArrayList<Map<String, Object>> listFood = (ArrayList<Map<String, Object>>) ((Map<String, Object>) mapResponse.get("data")).get("foodList");
        for (Map<String, Object> mapFood : listFood) {
            ArrayList<HashMap<String, String>> listUnit = (ArrayList<HashMap<String, String>>) mapFood.get("units");
            for (HashMap<String, String> mapUnit : listUnit) {
                HashMap<String, String> mapList = new HashMap<>();

                //mapList.put("foodId", mapFood.get("foodID").toString());
                mapList.put("unitKey", mapUnit.get("unitKey"));
                mapList.put("foodCode", mapFood.get("foodCode").toString());
                mapList.put("foodName", mapFood.get("foodName").toString());
                mapList.put("foodAliasName", mapFood.get("foodAliasName").toString());
                mapList.put("foodMnemonicCode", mapFood.get("foodMnemonicCode").toString());

                mapList.put("unit", mapUnit.get("unit"));
                mapList.put("price", mapUnit.get("price"));
                if (mapFood.get("tagNames") == null) {
                    mapList.put("tagNames", "");
                }
                else {
                    mapList.put("tagNames", mapFood.get("tagNames").toString());
                }

                listReturn.add(mapList);
            }
        }

        return listReturn;
    }

    public static int saveFoods(DBFactory dbBus, int shopId, ArrayList<HashMap<String, String>> foods) throws Exception {
        int nFind, nInsert = 0, nUpdate = 0;

        String sql;
        String unitKey, foodCode, foodName, foodAliasName, unit, price, tagNames, mnemonicCode;
        String[] arrFind = new String[1];

        DataTable dtbFood;
        DataTable.DataRow dataRow;
        // ------------------------------------------------
        sql = "SELECT * FROM t_food WHERE shop_id = ? ORDER BY unit_key";
        dtbFood = dbBus.getDataTable(sql, shopId);
        dtbFood.Sort("unit_key");

        for (HashMap<String, String> food : foods) {
            unitKey = food.get("unitKey");
            foodCode = food.get("foodCode");
            foodName = food.get("foodName");
            foodAliasName = food.get("foodAliasName");
            unit = food.get("unit");
            price = food.get("price");
            tagNames = food.get("tagNames");
            mnemonicCode = food.get("foodMnemonicCode");
            if (mnemonicCode.length() > 250) {
                mnemonicCode = mnemonicCode.substring(0, 250);
            }

            arrFind[0] = unitKey;
            nFind = dtbFood.Find(arrFind);
            if (nFind < 0) {
                sql = "INSERT INTO t_food (shop_id, unit_key, food_code, food_name, food_alias_name, unit, price, tag_names, food_mnemonic_code) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                dbBus.exec(sql, shopId, unitKey, foodCode, foodName, foodAliasName, unit, price, tagNames, mnemonicCode);
                nInsert++;
            }
            else {
                dataRow = dtbFood.Row(nFind);
                if (!dataRow.DataCell("food_code").equals(foodCode) || !dataRow.DataCell("food_name").equals(foodName)
                    || !dataRow.DataCell("food_alias_name").equals(foodAliasName) || !dataRow.DataCell("unit").equals(unit)
                    || !dataRow.DataCell("price").equals(price) || !dataRow.DataCell("tag_names").equals(tagNames)) {
                    // -- 数据发生变化 --
                    sql = "UPDATE t_food SET food_code = ?, food_name = ?, food_alias_name = ?, unit = ?, price = ?, tag_names = ?, food_mnemonic_code = ? " +
                        "WHERE unit_key = ?";
                    dbBus.exec(sql, foodCode, foodName, foodAliasName, unit, price, tagNames, mnemonicCode, unitKey);
                    nUpdate++;
                }
            }
        }
        return nInsert + nUpdate;
    }
}