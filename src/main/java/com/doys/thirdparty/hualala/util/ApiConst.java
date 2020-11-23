package com.doys.thirdparty.hualala.util;
public class ApiConst {
    private static final String API_URL_TESTING = "https://dohko-open-api.hualala.com";
    public static final String API_URL_RUNTIME = "https://www-openapi.hualala.com";
    // ------------------------------------------------------------------------
    private static String getApiBaseUrl(boolean blTesting) {
        if (blTesting) {
            return API_URL_TESTING;
        }
        else {
            return API_URL_RUNTIME;
        }
    }

    public static String getUrl_getAllShop(boolean blTesting) {
        return getApiBaseUrl(blTesting) + "/doc/getAllShop";
    }

    public static String getUrl_getOpenFood(boolean blTesting) {
        // -- 查询店铺菜品列表 --
        return getApiBaseUrl(blTesting) + "/doc/getOpenFood";
    }
    public static String getUrl_queryGroupFoodSubInfoList(boolean blTesting) {
        // -- 查询集团菜品列表 --
        return getApiBaseUrl(blTesting) + "/doc/queryGroupFoodSubInfoList";
    }
}