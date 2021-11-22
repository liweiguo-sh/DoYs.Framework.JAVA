package doys.framework.core.base;
import doys.framework.a0.Const;
import doys.framework.a2.structure.EntityHttpServletRequest;
import doys.framework.core.Token;
import doys.framework.core.TokenService;
import doys.framework.core.entity.RestError;
import doys.framework.core.entity.RestResult;
import doys.framework.core.ex.CommonException;
import doys.framework.core.ex.SessionTimeoutException;
import doys.framework.core.ex.UnImplementException;
import doys.framework.core.ex.UnexpectedException;
import doys.framework.database.ds.UtilTDS;
import doys.framework.util.UtilEnv;
import doys.framework.util.UtilResultSet;
import doys.framework.util.UtilRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class BaseController extends BaseTop {
    private ThreadLocal<EntityHttpServletRequest> tlRequest = new ThreadLocal<>();
    private ThreadLocal<Token> tlToken = new ThreadLocal<>();
    private ThreadLocal<RestResult> tlRestResult = new ThreadLocal<>();
    private ThreadLocal<RestError> tlRestError = new ThreadLocal<>();

    // -- request\response\session and ThreadLocal object offer and dispose ---
    protected Token getToken() throws Exception {
        Token token = tlToken.get();
        if (token == null) {
            String tokenId = head("token");

            token = TokenService.getToken(tokenId);
            tlToken.set(token);
        }
        return token;
    }
    protected void setToken(Token tokenSession) throws Exception {
        tlToken.set(tokenSession);
    }
    protected EntityHttpServletRequest entityRequest() {
        EntityHttpServletRequest entityRequest = tlRequest.get();
        if (entityRequest == null) {
            entityRequest = new EntityHttpServletRequest(request());
            tlRequest.set(entityRequest);
        }
        return entityRequest;
    }

    public HttpServletRequest request() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }
    public HttpServletResponse response() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
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
        tlRequest.remove();
        tlToken.remove();
        tlRestResult.remove();
        tlRestError.remove();
    }

    // -- public common method ------------------------------------------------
    protected int headInt(String headName) throws Exception {
        return entityRequest().header.getInt(headName, 0);
    }
    protected int headInt(String headName, int defaultValue) throws Exception {
        return entityRequest().header.getInt(headName, defaultValue);
    }
    protected String head(String headName) throws Exception {
        return entityRequest().header.getString(headName, "");
    }
    protected String head(String headName, String defaultValue) throws Exception {
        return entityRequest().header.getString(headName, defaultValue);
    }

    protected String tokenString(String attrName) throws Exception {
        return _getTokenValue(attrName, "").toString();
    }
    protected int tokenInt(String attrName) throws Exception {
        return (int) _getTokenValue(attrName, 0);
    }
    protected boolean tokenBoolean(String attrName) throws Exception {
        return (boolean) _getTokenValue(attrName, false);
    }
    private Object _getTokenValue(String attrName, Object defaultValue) throws Exception {
        Object object = getToken().getValue(attrName);
        if (object == null) {
            return defaultValue;
        }
        else {
            return object;
        }
    }

    // -- public inXXX --------------------------------------------------------
    protected HashMap<String, Object> inForm(String parameterName) {
        return entityRequest().body.getForm(parameterName);
    }
    protected HashMap<String, String> inHashMap(String parameterName) {
        return entityRequest().body.getHashMap(parameterName);
    }
    protected ArrayList<Object> inArrayList(String parameterName) throws Exception {
        return entityRequest().body.getArrayList(parameterName);
    }

    protected boolean inBool(String parameterName, boolean defaultValue) {
        return entityRequest().body.getBool(parameterName, defaultValue);
    }
    protected int inInt(String parameterName) {
        return entityRequest().body.getInt(parameterName, 0);
    }
    protected int inInt(String parameterName, int defaultValue) {
        return entityRequest().body.getInt(parameterName, defaultValue);
    }

    protected String in(String parameterName) {
        return entityRequest().body.getString(parameterName, "");
    }
    protected String in(String parameterName, String defaultValue) throws Exception {
        return entityRequest().body.getString(parameterName, defaultValue);
    }
    protected Object inObject(String parameterName) throws Exception {
        return entityRequest().body.getObject(parameterName, null);
    }
    protected String getRequestString() throws Exception {
        return entityRequest().body.getRequestString();
    }

    // -- setValue ------------------------------------------------------------
    protected void setFormValue(String key, Object value) throws Exception {
        HashMap<String, Object> mapForm = (HashMap<String, Object>) entityRequest().body.getObject("form");
        if (mapForm.containsKey(key)) {
            mapForm.replace(key, value);
        }
        else {
            mapForm.put(key, value);
        }
    }

    // -- ok, err -------------------------------------------------------------
    protected void ok(String key, SqlRowSet rowSet) throws Exception {
        if (rowSet == null) {
            _ok(key, "");
        }
        else {
            key = key + Const.CHAR1 + "datatable";
            _ok(key, UtilRowSet.getRowSetString(rowSet));
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
    protected void ok(String key, Object object) throws Exception {
        _ok(key, object);
    }
    private void _ok(String key, Object value) throws Exception {
        RestResult result = getRestResult();
        result.put(key, value);
    }

    protected void err(String strErr) {
        _err(strErr);
    }
    protected void err(Exception e) {
        Class<? extends Exception> clz = e.getClass();
        try {
            if (clz.equals(SessionTimeoutException.class)) {
                logger.error("SessionTimeoutException");
            }
            else {
                logger.error("tenant_id = " + UtilTDS.getTenantId());
            }
        } catch (Exception e1) {
            System.err.println("debug here: protected void err(Exception e)");
        }

        if (clz.equals(CommonException.class)
            || clz.equals(UnexpectedException.class)
            || clz.equals(UnImplementException.class)
            || clz.equals(SessionTimeoutException.class)) {
            _err(e.getMessage());
        }
        else {
            e.printStackTrace();
            _err(e.toString());
        }
    }
    private void _err(String strErr) {
        RestError error = getRestError();
        error.add(strErr);
    }
    protected void code(String code) {
        RestError error = getRestError();
        error.code = code;
        if (error.innerCode.equals("")) {
            error.innerCode = code;
        }
    }
    protected void innerCode(String innerCode) {
        RestError error = getRestError();
        error.innerCode = innerCode;
    }

    // -- resultOk, resultErr -------------------------------------------------
    protected RestResult ResultOk() {
        return _ResultOk();
    }
    private RestResult _ResultOk() {
        RestError error = getRestError();
        if (error.size() > 0) {
            return _ResultErr();
        }

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

        String message = error.toString();
        // ------------------------------------------------
        result.put(Const.ok, false);
        result.put("code", error.code);
        result.put("error", message);

        // ------------------------------------------------
        if (!error.code.equals("") || !error.innerCode.equals("")) {
            logger.error("code = " + error.code + ", innerCode = " + error.innerCode);
        }
        logger.error(message);

        // ------------------------------------------------
        dispose();
        return result;
    }

    // -- public debug method -------------------------------------------------
    public void printParameter() throws Exception {
        UtilEnv.getWebRootPath();

        HashMap<String, Object> map = entityRequest().body.mapBody;
        for (String key : map.keySet()) {
            logger.info(String.format("%-15s", key) + "=>  " + map.get(key).toString());
        }
    }
}