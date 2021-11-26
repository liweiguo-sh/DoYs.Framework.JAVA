/******************************************************************************
 * Copyright (C), 2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2021-11-21
 * @modify_date 2021-11-25
 * 基于Token的会话类
 *****************************************************************************/
package doys.framework.core;
import doys.framework.core.base.BaseTop;
import doys.framework.database.DBFactory;
import doys.framework.database.ds.UtilTDS;
import doys.framework.util.UtilDate;
import doys.framework.util.UtilYml;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;

public class Token extends BaseTop {
    private static int RENEW_DELAY = 60;                    // -- 需要重新记录renew_time的时间间隔(单位：分钟) --
    private static int MAX_TOKEN_VALUE_LEN = 500;           // -- 保存到数据库中的键值最大长度 --

    public int tenantId;
    public String userPk;

    public String tokenId;
    public LocalDateTime dtLogin;
    public LocalDateTime dtRenew;

    private HashMap<String, Object> mapValue = new HashMap<>();
    private HashSet<String> setUnsaved = new HashSet<>();        // -- 尚未保存的键值集合 --
    // -- check timeout & renew ---------------------------
    public LocalDateTime getExpTime() {
        return dtRenew.plus(UtilYml.getTimeout(), ChronoUnit.MINUTES);
    }

    public boolean timeout() {
        long duration = UtilDate.getDateTimeDiff(dtRenew);
        long minutes = duration / 1000 / 60;
        long timeout = UtilYml.getTimeout() + RENEW_DELAY;

        return minutes > timeout;   // -- 超时返回true --
    }
    public void renew() throws Exception {
        long duration = UtilDate.getDateTimeDiff(dtRenew);
        long minutes = duration / 1000 / 60;

        if (minutes > RENEW_DELAY) {
            dtRenew = LocalDateTime.now();

            String sql = "UPDATE sys_token SET renew_time = NOW() WHERE token_id = ?";
            DBFactory dbSys = UtilTDS.getDbSys();
            dbSys.exec(sql, tokenId);
        }
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
        if (!key.matches("^[a-zA-z].*")) {
            // -- 非英文字母开头的键值不保存到数据库中 --
            return;
        }
        if (value != null && value.toString().length() > MAX_TOKEN_VALUE_LEN) {
            // -- 值超长，不保存 --
            if (setUnsaved.contains(key)) {
                setUnsaved.remove(key);
            }
            return;
        }
        if (setUnsaved.contains(key)) {
            return;
        }
        setUnsaved.add(key);

        // ------------------------------------------------
        if (save) {
            save();
        }


    }
    public void save() {
        int result;
        String sql, value;

        DBFactory dbBus;
        // ------------------------------------------------
        try {
            if (setUnsaved.size() > 0) {
                dbBus = UtilTDS.getDbSys();

                for (String key : setUnsaved) {
                    value = mapValue.get(key).toString();
                    sql = "SELECT COUNT(1) FROM sys_token_value WHERE token_id = ? AND token_key = ?";
                    result = dbBus.getInt(sql, 0, tokenId, key);
                    if (result == 0) {
                        sql = "INSERT INTO sys_token_value (token_id, token_key, token_value) VALUES (?, ?, ?)";
                        dbBus.exec(sql, tokenId, key, value);
                    }
                    else {
                        sql = "UPDATE sys_token_value SET token_value = ? WHERE token_id = ? AND token_key = ?";
                        dbBus.exec(sql, value, value, tokenId, key);
                    }
                }
                setUnsaved.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("数据库尚未升级.");
        }
    }
    public void clearSave() {
        setUnsaved.clear();
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