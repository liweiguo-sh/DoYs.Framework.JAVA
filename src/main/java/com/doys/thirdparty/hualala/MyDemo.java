package com.doys.thirdparty.hualala;
import com.doys.thirdparty.hualala.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
public class MyDemo {
    public static void main(String[] args) {
        try {
            BaseVo baseVo = new BaseVo();
            baseVo.setGroupID(ApiConst_LvKeMen.GROUP_ID);
            baseVo.setShopID(ApiConst_LvKeMen.SHOP_ID);
            //参与签名字段集合
            Long timestamp = System.currentTimeMillis();

            Map<String, Object> signMap = new HashMap<String, Object>();
            signMap.put("timestamp", timestamp);
            signMap.put("version", "2.0");
            signMap.put("appKey", ApiConst_LvKeMen.APP_KEY);
            signMap.put("requestBody", baseVo);

            //调用SDK方法getMapSign，生成公共参数signature的值，generatorStr：签名字段拼接，generatorSig：签名加密结果
            SignModel signModel = SignUtil.getMapSign(signMap, ApiConst_LvKeMen.APP_SECRET);
            //签名拼接字符串
            System.out.println("generatorStr : " + signModel.getGeneratorStr());
            //签名加密生成字符串
            System.out.println("generatorSig : " + signModel.getGeneratorSig());

            //调用SDK方法将业务参数json进行AES加密，生成requestBody的值
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(baseVo);
            String requestBody = AESUtil.AESEncode(ApiConst_LvKeMen.APP_SECRET, jsonString);

            //创建公共参数列表键值对map
            Map<String, String> params = new HashMap<String, String>();
            params.put("timestamp", timestamp.toString());
            params.put("version", "2.0");
            params.put("appKey", ApiConst_LvKeMen.APP_KEY.toString());
            params.put("requestBody", requestBody);
            params.put("signature", signModel.getGeneratorSig());

            //请求标识id
            String traceID = UUID.randomUUID().toString();

            //2.0接口groupID,shopID,traceID放入header中传输，groupID和traceID必传
            String response = HttpClientUtil.senderPost(ApiConst.getUrl_queryGroupFoodSubInfoList(ApiConst_LvKeMen.TESTING),
                params, ApiConst_LvKeMen.GROUP_ID, ApiConst_LvKeMen.SHOP_ID, traceID);
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}