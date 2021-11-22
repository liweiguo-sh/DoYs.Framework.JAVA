package doys.framework.a2.structure;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;

public class EntityHttpServletRequestHeader {
    private HashMap<String, String> mapHeader = new HashMap<>();
    // ------------------------------------------------------------------------
    public EntityHttpServletRequestHeader(HttpServletRequest request) {
        String key, value;

        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            key = headerNames.nextElement();
            value = request.getHeader(key);
            mapHeader.put(key.toLowerCase(), value);
        }
    }

    public void setValue(String headName, String headValue) {
        if (mapHeader.containsKey(headName)) {
            mapHeader.replace(headName, headValue);
        }
        else {
            mapHeader.put(headName, headValue);
        }
    }
    // ------------------------------------------------------------------------
    public int getInt(String headName) {
        return getInt(headName, 0);
    }
    public int getInt(String headName, int defaultValue) {
        String value = getString(headName, String.valueOf(defaultValue));
        return Integer.parseInt(value);
    }

    public String getString(String headName) {
        return getString(headName, "");
    }
    public String getString(String headName, String defaultValue) {
        headName = headName.toLowerCase();
        if (mapHeader.containsKey(headName)) {
            return mapHeader.get(headName);
        }
        return defaultValue;
    }
}