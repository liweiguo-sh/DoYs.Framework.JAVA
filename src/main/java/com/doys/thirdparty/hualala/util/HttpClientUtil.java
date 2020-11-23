package com.doys.thirdparty.hualala.util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.http.impl.client.HttpClients.createDefault;
/**
 * 发送Http请求工具类, get post请求
 */
public class HttpClientUtil {
    private static final Log logger = LogFactory.getLog(HttpClientUtil.class);
    private static final int DEFAULT_TIMEOUT = 60000;

    public static String senderPost(String url, Map<String, String> params, Long groupID, Long shopID, String traceID) throws IOException {
        CloseableHttpClient httpClient = createDefault();
        CloseableHttpResponse response = null;
        String result = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig
                .custom()
                .setSocketTimeout(DEFAULT_TIMEOUT)
                .setConnectTimeout(DEFAULT_TIMEOUT)
                .build();//设置请求和传输超时时间

            httpPost.setConfig(requestConfig);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            //将groupID与shopID放入header中传输，2.0接口header中参数不参与签名,traceID必传
            httpPost.setHeader("groupID", groupID.toString());
            httpPost.setHeader("shopID", shopID.toString());
            httpPost.setHeader("traceID", traceID);

            List<BasicNameValuePair> basicNameValuePairs = new ArrayList<BasicNameValuePair>();
            for (Map.Entry<String, String> entity : params.entrySet()) {
                basicNameValuePairs.add(new BasicNameValuePair(entity.getKey(), entity.getValue()));
            }

            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(basicNameValuePairs, Consts.UTF_8);
            httpPost.setEntity(urlEncodedFormEntity);
            // 获取当前时间戳
            Long now = System.currentTimeMillis();
            response = httpClient.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String StartTime = sdf.format(date);
            // 计算耗时
            long time = System.currentTimeMillis() - now;
            logger.info("调用三方平台耗时： " + time + "毫秒");
            // 格式化请求参数
            //String requestParams = JSON.toJSONString(params);
            ObjectMapper mapper = new ObjectMapper();
            String requestParams = mapper.writeValueAsString(params);

            int httpStatus = statusLine.getStatusCode();
            logger.info(String.format("param url: %s, params: %s, response status: %s",
                url, requestParams, httpStatus));

            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, Consts.UTF_8);
            logger.info(String.format("response data: %s", result));
            return result;
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                logger.error("close http client failed", e);
            }
        }
    }
}