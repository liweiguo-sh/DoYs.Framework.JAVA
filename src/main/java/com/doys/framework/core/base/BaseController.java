package com.doys.framework.core.base;
import com.doys.framework.common.UtilDataSet;
import com.doys.framework.config.Const;
import com.doys.framework.core.entity.RestError;
import com.doys.framework.core.entity.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@Scope("request")
public class BaseController extends BaseTop {
    private ThreadLocal<RestResult> tRestResult = new ThreadLocal<>();
    private ThreadLocal<RestError> tRestError = new ThreadLocal<>();

    @Autowired
    protected HttpSession ss;

    // -- private common method -----------------------------------------------
    private void dispose() {
        tRestResult.remove();
        tRestError.remove();
    }

    private RestResult getRestResult() {
        RestResult result = tRestResult.get();
        if (result == null) {
            result = new RestResult();
            tRestResult.set(result);
        }
        return result;
    }
    private RestError getRestError() {
        RestError error = tRestError.get();
        if (error == null) {
            error = new RestError();
            tRestError.set(error);
        }
        return error;
    }

    // -- public common method ------------------------------------------------
    protected String getSessionValue(String attrName) {
        return _getSessionValue(attrName, "").toString();
    }
    private Object _getSessionValue(String attrName, Object defaultValue) {
        Object object = ss.getAttribute(attrName);
        if (object == null) {
            return defaultValue;
        }
        else {
            return object;
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
}