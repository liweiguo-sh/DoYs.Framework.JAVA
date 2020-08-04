package com.doys.framework.core.base;
import com.doys.framework.config.Const;
import com.doys.framework.core.entity.RestError;
import com.doys.framework.core.entity.RestResult;
import com.doys.framework.database.DBFactory;
import com.doys.framework.util.UtilDataSet;
import com.doys.framework.util.UtilEnv;
import com.doys.framework.util.UtilResultSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
public class BaseController extends BaseTop {
    @Autowired
    protected DBFactory dbSys;
    @Autowired
    @Qualifier("dynamicDBFactory")
    protected DBFactory dbBus;

    private ThreadLocal<HashMap<String, Object>> tlHashMapIn = new ThreadLocal<>();
    private ThreadLocal<RestResult> tlRestResult = new ThreadLocal<>();
    private ThreadLocal<RestError> tlRestError = new ThreadLocal<>();

    // -- request\response\session and ThreadLocal object offer and dispose ---
    protected HttpSession session() {
        return request().getSession();
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
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                while ((strLine = bufReader.readLine()) != null) {
                    builder.append(strLine);
                }

                jsonString = builder.toString();
                if (debug) {
                    logger.info("request => " + jsonString);
                }

                ObjectMapper mapper = new ObjectMapper();
                hashMapIn = mapper.readValue(jsonString, HashMap.class);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("读取解析Request数据遇到未知错误，请检查。");
            }
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
    protected String ssValue(String attrName) {
        return _getSessionValue(attrName, "").toString();
    }
    protected boolean ssBoolean(String attrName) {
        return (boolean) _getSessionValue(attrName, false);
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
    protected HashMap<String, Object> inForm(String parameterName) {
        Object obj = _inObject(parameterName, null);
        if (obj == null) {
            return null;
        }
        return (HashMap<String, Object>) obj;
    }
    protected ArrayList<HashMap<String, Object>> inArrayList(String parameterName) {
        Object obj = _inObject(parameterName, null);
        if (obj == null) {
            return null;
        }
        return (ArrayList<HashMap<String, Object>>) obj;
    }

    protected int inInt(String parameterName) {
        return _inInt(parameterName, 0);
    }
    protected int inInt(String parameterName, int defaultValue) {
        return _inInt(parameterName, defaultValue);
    }
    private int _inInt(String parameterName, int defaultValue) {
        Object parameterValue = _inObject(parameterName, defaultValue);
        if (parameterValue instanceof Double) {
            return ((Double) parameterValue).intValue();
        }
        else if (parameterValue instanceof Integer) {
            return (Integer) parameterValue;
        }
        return Integer.parseInt((String) parameterValue);
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
    private Object _inObject(String parameterName, Object defaultValue) {
        HashMap<String, Object> map = this.getHashMapIn();
        Object parameterValue = null;

        if (map.containsKey(parameterName)) {
            parameterValue = map.get(parameterName);
        }
        else {
            if (map.containsKey("form")) {
                HashMap<String, Object> mapForm = (HashMap<String, Object>) map.get("form");
                if (mapForm.containsKey(parameterName)) {
                    parameterValue = mapForm.get(parameterName);
                }
            }
        }

        if (parameterValue == null) {
            parameterValue = defaultValue;
        }
        return parameterValue;
    }

    // -- setValue ------------------------------------------------------------
    protected void setFormValue(String key, Object value) throws Exception {
        HashMap<String, Object> map = this.getHashMapIn();
        HashMap<String, Object> mapForm = (HashMap<String, Object>) map.get("form");
        if (mapForm.containsKey(key)) {
            mapForm.replace(key, value);
        }
        else {
            mapForm.put(key, value);
        }
    }

    // -- ok, err -------------------------------------------------------------
    protected void ok(String key, long value) throws Exception {
        _ok(key, value);
    }
    protected void ok(String key, String value) throws Exception {
        _ok(key, value);
    }
    protected void ok(String key, SqlRowSet rowSet) throws Exception {
        if (rowSet == null) {
            _ok(key, "");
        }
        else {
            key = key + Const.CHAR1 + "datatable";
            _ok(key, UtilDataSet.getRowSetString(rowSet));
        }
    }
    protected void ok(String key, ResultSet rs) throws Exception {
        if (rs == null) {
            _ok(key, "");
        }
        else {
            key = key + Const.CHAR1 + "datatable";
            _ok(key, UtilResultSet.getRowSetString(rs));
        }
    }
    private void _ok(String key, Object value) throws Exception {
        RestResult result = getRestResult();
        result.put(key, value);
    }

    protected void err(String strErr) {
        _err(strErr);
    }
    protected void err(Exception e) {
        logger.error(e.getMessage());
        e.printStackTrace();
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