/******************************************************************************
 * Copyright (C), 2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2021-11-21
 * @modify_date 2021-11-22
 * 基于Token的会话服务类
 *****************************************************************************/
package doys.framework.core;
import doys.framework.a2.base.ENTITY_RECORD;
import doys.framework.core.base.BaseService;
import doys.framework.database.DBFactory;
import doys.framework.database.ds.UtilTDS;
import doys.framework.util.UtilDate;
import doys.framework.util.UtilDigest;
import doys.framework.util.UtilYml;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.time.LocalDateTime;
import java.util.HashMap;

public class TokenService extends BaseService {
    private static String RANDOM_FOR_TOKEN = "zSaSN^zphT";

    private static HashMap<String, Token> mapToken = new HashMap<>();
    // ------------------------------------------------------------------------
    public static Token createToken(int tenantId, String userPk) throws Exception {
        String tokenId, seed, nowString;

        LocalDateTime dtNow = LocalDateTime.now();
        // ------------------------------------------------
        seed = RANDOM_FOR_TOKEN + "_" + tenantId + "_" + userPk + "_" + UtilDate.getMilliSecond(dtNow);
        nowString = UtilDate.getDateTimeStr(dtNow);
        tokenId = UtilDigest.MD5(seed);

        // ------------------------------------------------
        Token token = new Token();
        token.tenantId = tenantId;
        token.userPk = userPk;
        token.tokenId = tokenId;
        token.dtLogin = dtNow;
        token.dtRenew = dtNow;
        mapToken.put(token.tokenId, token);

        // ------------------------------------------------
        DBFactory dbSys = UtilTDS.getDbSys();
        ENTITY_RECORD record = new ENTITY_RECORD(dbSys, "sys_token");
        try {
            record
                .setValue("tenant_id", tenantId)
                .setValue("user_pk", userPk)
                .setValue("token_id", tokenId)
                .setValue("login_time", nowString)
                .setValue("renew_time", nowString)
                .Save();
        } catch (Exception e) {
            logger.error("需要先升级数据库。");
        }
        // ------------------------------------------------
        return token;
    }

    public static synchronized Token getToken(String tokenId) throws Exception {
        String sql;

        Token token = null;
        SqlRowSet rsSession;
        DBFactory dbSys;
        // ------------------------------------------------
        if (mapToken.containsKey(tokenId)) {
            token = mapToken.get(tokenId);
        }
        else {
            dbSys = UtilTDS.getDbSys();

            sql = "SELECT * FROM sys_token WHERE token_id = ?";
            rsSession = dbSys.getRowSet(sql, tokenId);
            if (rsSession.next()) {
                token = new Token();
                token.tokenId = tokenId;
                token.tenantId = rsSession.getInt("tenant_id");
                token.userPk = rsSession.getString("user_pk");
                token.dtLogin = UtilDate.getDateTime(rsSession.getString("login_time"));
                token.dtRenew = UtilDate.getDateTime(rsSession.getString("renew_time"));
                mapToken.put(tokenId, token);

                sql = "SELECT token_key, token_value FROM sys_token_value WHERE token_id = ?";
                rsSession = dbSys.getRowSet(sql, tokenId);
                while (rsSession.next()) {
                    token.setValue(rsSession.getString("token_key"), rsSession.getString("token_value"));
                }
                token.clearSave();
            }
        }
        return token;
    }

    public static void removeTimeoutToken() {
        int result = 0;
        int timeout = UtilYml.getTimeout();

        String sql;

        Token token;
        DBFactory dbSys;
        // ------------------------------------------------
        try {
            Object[] keys = mapToken.keySet().toArray();
            for (Object key : keys) {
                token = mapToken.get(key);
                if (token.timeout()) {
                    result++;
                    mapToken.remove(key);
                }
            }

            if (result > 0) {
                dbSys = UtilTDS.getDbSys();
                sql = "DELETE FROM sys_token WHERE TIMESTAMPDIFF(MINUTE, renew_time, NOW()) >= ?";
                result = dbSys.exec(sql, timeout);
                if (result > 0) {
                    sql = "DELETE FROM sys_token_value WHERE token_id NOT IN (SELECT token_id FROM sys_token)";
                    dbSys.exec(sql);

                    logger.info("delete " + result + " records from sys_token");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
    }
    public static void deleteTokenSession(String token) throws Exception {
        String sql;

        DBFactory dbSys;
        Token tss = mapToken.getOrDefault(token, null);
        // ------------------------------------------------
        if (tss == null) return;
        dbSys = UtilTDS.getDbSys();

        sql = "DELETE FROM sys_token_value WHERE token = ?";
        dbSys.exec(sql, token);
        sql = "DELETE FROM sys_token WHERE token = ?";
        dbSys.exec(sql, token);

        mapToken.remove(token);
    }
}