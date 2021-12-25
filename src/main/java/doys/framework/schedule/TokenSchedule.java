/******************************************************************************
 * Copyright (C), 2020-2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2021-11-22
 * @modify_date 2021-12-25
 * Token监控类
 *****************************************************************************/
package doys.framework.schedule;
import doys.framework.core.TokenService;
import doys.framework.core.base.BaseTenantScheduleService;
import doys.framework.database.DBFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TokenSchedule extends BaseTenantScheduleService {
    @Autowired
    protected DBFactory dbSys;

    // ------------------------------------------------------------------------
    @Scheduled(initialDelay = 15 * 1000, fixedDelay = 60 * 60 * 1000)
    private void hourSchedule() {
        logger.info("TokenService.removeTimeoutToken");
        TokenService.removeTimeoutToken();
    }
}