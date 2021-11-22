package doys.framework.a2.structure;
import com.fasterxml.jackson.databind.ObjectMapper;
import doys.framework.core.base.BaseTop;
import doys.framework.database.DBFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

public class EntityHttpServletRequestBody extends BaseTop {
    public HashMap<String, Object> mapBody;
    // ------------------------------------------------------------------------
    public EntityHttpServletRequestBody(HttpServletRequest request) throws Exception {
        String strLine, jsonString;
        StringBuilder builder = new StringBuilder();

        ServletInputStream inputStream = request.getInputStream();
        BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        while ((strLine = bufReader.readLine()) != null) {
            builder.append(strLine);
        }
        jsonString = builder.toString();
        if (debug) {
            System.out.println("request => " + jsonString);
        }

        // -- 1. 读取JSON数据 --
        if (!jsonString.equals("")) {
            ObjectMapper mapper = new ObjectMapper();
            mapBody = mapper.readValue(jsonString, HashMap.class);
            mapBody.put("__requestString__", jsonString);
        }
        else {
            mapBody = new HashMap<>();
        }

        // -- 2. 读取GET数据 --
        String paraName = "", paraValue = "";
        Enumeration<String> eu = request.getParameterNames();
        while (eu.hasMoreElements()) {
            paraName = eu.nextElement();
            paraValue = request.getParameter(paraName);
            mapBody.put(paraName, paraValue);
        }
    }

    public String getRequestString() {
        return getString("__requestString__");
    }
    public void setValue(String parameterName, Object parameterValue) {
        mapBody.put(parameterName, parameterValue);
    }
    // ------------------------------------------------------------------------
    public HashMap<String, Object> getForm(String parameterName) {
        Object obj = getObject(parameterName, null);
        if (obj == null) {
            return null;
        }
        return (HashMap<String, Object>) obj;
    }
    public HashMap<String, String> getHashMap(String parameterName) {
        Object obj = getObject(parameterName, null);
        if (obj == null) {
            return null;
        }
        return (HashMap<String, String>) obj;
    }
    public ArrayList<Object> getArrayList(String parameterName) {
        Object obj = getObject(parameterName, null);
        if (obj == null) {
            return null;
        }
        return (ArrayList<Object>) obj;
    }

    public boolean getBool(String parameterName, boolean defaultValue) {
        Object parameterValue = getObject(parameterName, defaultValue);
        if (parameterValue instanceof Boolean) {
            return ((Boolean) parameterValue);
        }
        else if (parameterValue instanceof Integer) {
            return ((Integer) parameterValue) == 1;
        }
        else if (parameterValue instanceof String) {
            return ((String) parameterValue).equalsIgnoreCase("true") || ((String) parameterValue).equalsIgnoreCase("1");
        }
        else {
            return false;
        }
    }

    public int getInt(String parameterName) {
        return _getInt(parameterName, 0);
    }
    public int getInt(String parameterName, int defaultValue) {
        return _getInt(parameterName, defaultValue);
    }
    private int _getInt(String parameterName, int defaultValue) {
        Object parameterValue = getObject(parameterName, defaultValue);
        if (parameterValue instanceof Boolean) {
            return ((Boolean) parameterValue) ? 1 : 0;
        }
        else if (parameterValue instanceof Double) {
            return ((Double) parameterValue).intValue();
        }
        else if (parameterValue instanceof Integer) {
            return (Integer) parameterValue;
        }
        else if (parameterValue instanceof String) {
            if (parameterValue.equals("")) {
                return defaultValue;
            }
        }
        return Integer.parseInt((String) parameterValue);
    }

    public String getString(String parameterName) {
        return (String) getObject(parameterName, "");
    }
    public String getString(String parameter, String defaultValue) {
        Object obj = getObject(parameter, defaultValue);
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
    public Object getObject(String parameterName) {
        return getObject(parameterName, null);
    }
    public Object getObject(String parameterName, Object defaultValue) {
        Object parameterValue = null;

        if (mapBody.containsKey(parameterName)) {
            parameterValue = mapBody.get(parameterName);
        }
        else {
            if (mapBody.containsKey("form")) {
                HashMap<String, Object> mapForm = (HashMap<String, Object>) mapBody.get("form");
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
}