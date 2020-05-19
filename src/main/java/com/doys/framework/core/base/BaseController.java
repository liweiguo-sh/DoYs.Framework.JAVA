package com.doys.framework.core.base;
import com.doys.framework.common.UtilDataSet;
import com.doys.framework.common.UtilEnv;
import com.doys.framework.config.Const;
import com.doys.framework.core.db.DBFactory;
import com.doys.framework.core.entity.RestError;
import com.doys.framework.core.entity.RestResult;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
public class BaseController extends BaseTop {
    private ThreadLocal<HashMap<String, Object>> tlHashMapIn = new ThreadLocal<>();
    private ThreadLocal<RestResult> tlRestResult = new ThreadLocal<>();
    private ThreadLocal<RestError> tlRestError = new ThreadLocal<>();

    // -- request\response\session and ThreadLocal object offer and dispose ---
    protected HttpSession session() {
        HttpSession ss = request().getSession();
        return ss;
    }
    public HttpServletRequest request() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }
    public HttpServletResponse response() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
    }

    private HashMap<String, Object> getHashMapIn() {
        HashMap<String, Object> hashMapIn = tlHashMapIn.get();
        if (hashMapIn == null) {

            String strLine = "", jsonString = "";
            StringBuilder builder = new StringBuilder();
            try {
                ServletInputStream inputStream = request().getInputStream();
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                while ((strLine = bufReader.readLine()) != null) {
                    builder.append(strLine);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("读取Request数据遇到未知错误，请检查。");
            }
            jsonString = builder.toString();
            if (debug) {
                logger.info("request => " + jsonString);
            }

            hashMapIn = (new Gson()).fromJson(jsonString, HashMap.class);
            tlHashMapIn.set(hashMapIn);
        }
        return hashMapIn;
    }
    private RestResult getRestResult() {
        RestResult result = tlRestResult.get();
        if (result == null) {
            result = new RestResult();
            tlRestResult.set(result);
        }
        return result;
    }
    private RestError getRestError() {
        RestError error = tlRestError.get();
        if (error == null) {
            error = new RestError();
            tlRestError.set(error);
        }
        return error;
    }
    private void dispose() {
        tlRestResult.remove();
        tlRestError.remove();
        tlHashMapIn.remove();
    }

    // -- public common method ------------------------------------------------
    protected String getSessionValue(String attrName) {
        return _getSessionValue(attrName, "").toString();
    }
    private Object _getSessionValue(String attrName, Object defaultValue) {
        Object object = this.session().getAttribute(attrName);
        if (object == null) {
            return defaultValue;
        }
        else {
            return object;
        }
    }

    // -- public inXXX --------------------------------------------------------
    protected LinkedTreeMap<String, Object> inForm(String parameterName) {
        Object obj = _inObject(parameterName, null);
        if (obj == null) {
            return null;
        }
        return (LinkedTreeMap<String, Object>) obj;
    }

    protected int inInt(String parameterName) {
        return _inInt(parameterName, 0);
    }
    protected int inInt(String parameterName, int defaultValue) {
        return _inInt(parameterName, defaultValue);
    }
    private int _inInt(String parameterName, int defaultValue) {
        Object parameterValue = getRequestParameter(parameterName);
        if (parameterValue == null) {
            return defaultValue;
        }
        if (parameterValue instanceof Double) {
            return ((Double) parameterValue).intValue();
        }
        return (int) parameterValue;
    }

    protected String in(String parameterName) {
        return (String) _inObject(parameterName, "");
    }
    protected String in(String parameterName, String defaultValue) {
        return (String) _inObject(parameterName, defaultValue);
    }
    private String _inString(String parameter, String defaultValue) {
        Object obj = _inObject(parameter, defaultValue);
        if (obj == null) {
            return null;
        }
        // -- check sql inject ----------------------------
        String parameterValue = (String) obj;
        if (!DBFactory.checkSqlInjection(parameterValue)) {
            logger.error("Suspicious SQL injection statement is found, value is ignored. " + parameterValue);
            return "";
        }
        return parameterValue;
    }

    private Object _inObject(String parameterName, String defaultValue) {
        Object parameterValue;
        Object parameterObject = getRequestParameter(parameterName);
        if (parameterObject == null) {
            return defaultValue;
        }
        parameterValue = parameterObject;
        return parameterValue;
    }
    private Object getRequestParameter(String parameterName) {
        if (this.getHashMapIn().containsKey(parameterName)) {
            return this.getHashMapIn().get(parameterName);
        }
        else {
            return null;
        }
    }

    // -- ok, err -------------------------------------------------------------
    protected void ok(String key, int value) throws Exception {
        _ok(key, value);
    }
    protected void ok(String key, String value) throws Exception {
        _ok(key, value);
    }
    protected void ok(String key, SqlRowSet rowSet) throws Exception {
        key = key + Const.CHAR1 + "datatable";
        _ok(key, UtilDataSet.getRowSetString(rowSet));
    }
    private void _ok(String key, Object value) throws Exception {
        RestResult result = getRestResult();
        result.put(key, value);
    }

    protected void err(String strErr) {
        _err(strErr);
    }
    protected void err(Exception e) {
        e.printStackTrace();
        logger.error(e.getMessage());
        _err(e.getMessage());
    }
    private void _err(String strErr) {
        RestError error = getRestError();
        error.add(strErr);
    }

    // -- resultOk, resultErr -------------------------------------------------
    protected RestResult ResultOk() {
        return _ResultOk();
    }
    private RestResult _ResultOk() {
        RestResult result = getRestResult();
        result.put(Const.ok, true);

        dispose();
        return result;
    }

    protected RestResult ResultErr() {
        return _ResultErr();
    }
    protected RestResult ResultErr(Exception e) {
        err(e);
        return _ResultErr();
    }
    protected RestResult ResultErr(String strErr) {
        err(strErr);
        return _ResultErr();
    }
    private RestResult _ResultErr() {
        RestError error = getRestError();
        RestResult result = new RestResult();   // -- 重新初始化, 清空ok信息 --

        // ------------------------------------------------
        result.put(Const.ok, false);
        result.put("error", error.toString());

        // ------------------------------------------------
        dispose();
        return result;
    }

    // -- public debug method -------------------------------------------------
    public void printParameter() {
        UtilEnv.getWebRootPath();

        HashMap<String, Object> map = getHashMapIn();
        for (String key : map.keySet()) {
            logger.info(String.format("%-15s", key) + "=>  " + map.get(key).toString());
        }
    }
}