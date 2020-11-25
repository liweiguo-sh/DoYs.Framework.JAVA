package com.doys.thirdparty.hualala.util;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
        // -- 查询店铺列表 --
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

    public static Map<String, Object> sendPost(String urlApi, long groupId, long shopId, long APP_KEY, String APP_SECRET) throws Exception {
        long timestamp = System.currentTimeMillis();
        String responseString;
        String traceID = UUID.randomUUID().toString();
        ObjectMapper mapper = new ObjectMapper();

        BaseVo baseVo = new BaseVo();
        baseVo.setGroupID(groupId);
        baseVo.setShopID(shopId);

        Map<String, Object> signMap = new HashMap<>();
        signMap.put("timestamp", timestamp);
        signMap.put("version", "2.0");
        signMap.put("appKey", APP_KEY);
        signMap.put("requestBody", baseVo);

        SignModel signModel = SignUtil.getMapSign(signMap, APP_SECRET);
        String jsonString = mapper.writeValueAsString(baseVo);
        String requestBody = AESUtil.AESEncode(APP_SECRET, jsonString);

        Map<String, String> params = new HashMap<>();
        params.put("timestamp", String.valueOf(timestamp));
        params.put("version", "2.0");
        params.put("appKey", String.valueOf(APP_KEY));
        params.put("requestBody", requestBody);
        params.put("signature", signModel.getGeneratorSig());

        responseString = HttpClientUtil.sendPost(urlApi, params, groupId, shopId, traceID);
        return mapper.readValue(responseString, Map.class);
    }
}