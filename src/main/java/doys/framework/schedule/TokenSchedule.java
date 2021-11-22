/******************************************************************************
 * Copyright (C), 2020-2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2021-11-22
 * @modify_date 2021-11-22
 * Token监控类
 *****************************************************************************/
package doys.framework.schedule;
import doys.framework.core.base.BaseTenantScheduleService;
import doys.framework.database.DBFactory;
import doys.framework.util.UtilYml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TokenSchedule extends BaseTenantScheduleService {
    @Autowired
    protected DBFactory dbSys;

    // ------------------------------------------------------------------------
    @Scheduled(cron = "0 0 * * * ?")        // -- cron = "0 0 * * * ?" 每整点执行一次 --
    private void nightScheduled() {
        int result;
        int timeout = UtilYml.getTimeout();

        String sql;
        // -----------------------------------------------
        try {
            sql = "DELETE FROM sys_token WHERE TIMESTAMPDIFF(MINUTE, renew_time, NOW()) >= ?";
            result = dbSys.exec(sql, timeout);
            if (result > 0) {
                sql = "DELETE FROM sys_token_value WHERE token_id NOT IN (SELECT token_id FROM sys_token)";
                dbSys.exec(sql);

                logger.info("delete " + result + " records from sys_token");
            }

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }
}