package com.doys.framework.util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilHttp {
    public static HashMap<String, Object> reqFormResJson(String url, HashMap<String, String> mapForm) throws Exception {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse httpResponse = null;
        HttpPost httpPost = null;
        // ------------------------------------------------
        try {
            // -- prepare parameter --
            List<NameValuePair> params = new ArrayList<>();
            for (Map.Entry<String, String> entry : mapForm.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");

            // -- post request --
            httpclient = HttpClients.createDefault();

            httpPost = new HttpPost(url);
            httpPost.setEntity(entity);

            httpResponse = httpclient.execute(httpPost);

            // -- response result --
            HttpEntity httpEntity = httpResponse.getEntity();
            String jsonString = EntityUtils.toString(httpEntity);

            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> mapRes = mapper.readValue(jsonString, HashMap.class);
            return mapRes;
        } finally {
            try {
                if (httpclient != null) {
                    httpclient.close();
                }
                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}