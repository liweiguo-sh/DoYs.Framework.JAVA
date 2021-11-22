/******************************************************************************
 * Copyright (C), 2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2021-11-21
 * @modify_date 2021-11-21
 * 基于Token的会话类
 *****************************************************************************/
package doys.framework.core;
import doys.framework.database.DBFactory;
import doys.framework.database.ds.UtilTDS;
import doys.framework.util.UtilDate;
import doys.framework.util.UtilYml;

import java.time.LocalDateTime;
import java.util.HashMap;

public class Token {
    private static int RENEW_MINITUS = 10;                  // -- 需要重新记录renew_time的时间间隔(单位：分钟) --

    public int tenantId;
    public String userPk;

    public String tokenId;
    public LocalDateTime dtLogin;
    public LocalDateTime dtRenew;

    private HashMap<String, Object> mapValue = new HashMap<>();
    // -- check timeout & renew ---------------------------
    public boolean checkTimeout() throws Exception {
        if (UtilDate.getDateTimeDiff(dtRenew) / 1000 / 60 > UtilYml.getTimeout()) {
            return true;   // -- timeout --
        }

        // -- renew ---------------------------------------
        if (UtilDate.getDateTimeDiff(dtRenew) / 1000 / 60 > RENEW_MINITUS) {
            String sql = "UPDATE sys_token SET renew_time = ? WHERE token = ?";
            DBFactory dbSys = UtilTDS.getDbSys();

            dbSys.exec(sql, UtilDate.getDateTimeStr());
            dtRenew = LocalDateTime.now();
        }
        return false;
    }

    // -- set value ---------------------------------------
    public void setValue(String key, Object value) throws Exception {
        setValue(key, value, false);
    }
    public void setValue(String key, Object value, boolean save) throws Exception {
        if (mapValue.containsKey(key)) {
            mapValue.replace(key, value);
        }
        else {
            mapValue.put(key, value);
        }

        // ------------------------------------------------
        if (!save) return;

        int result;
        String sql;

        DBFactory dbBus = UtilTDS.getDBFactory(tenantId);

        sql = "SELECT COUNT(1) FROM sys_token_key WHERE token = ? AND tss_key = ?";
        result = dbBus.getInt(sql, 0, tokenId, key);
        if (result == 0) {
            sql = "INSERT INTO sys_token (token, tss_key, tss_value) VALUES (?, ?, ?)";
            dbBus.exec(sql, tokenId, key, value);
        }
        else {
            sql = "UPDATE sys_token SET tss_value = ? WHERE token = ? AND tss_key = ?";
            dbBus.exec(sql, value, tokenId, key);
        }
    }

    // -- get value ---------------------------------------
    public String getString(String key) {
        return (String) mapValue.getOrDefault(key, "");
    }
    public String getString(String key, String defaultValue) {
        return (String) mapValue.getOrDefault(key, defaultValue);

    }

    public int getInt(String key) {
        return (int) mapValue.getOrDefault(key, 0);

    }
    public int getInt(String key, int defaultValue) {
        return (int) mapValue.getOrDefault(key, defaultValue);

    }
    public boolean getBoolean(String key) {
        return (boolean) mapValue.getOrDefault(key, false);
    }
    public boolean getBoolean(String key, String defaultValue) {
        return (boolean) mapValue.getOrDefault(key, defaultValue);
    }

    public Object getValue(String key) {
        return mapValue.getOrDefault(key, "");
    }
    public Object getValue(String key, Object defaultValue) {
        return mapValue.getOrDefault(key, defaultValue);
    }
}