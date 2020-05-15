package com.doys.framework.interceptor;
import com.doys.framework.common.UtilDate;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 继承Application接口后项目启动时会按照执行顺序执行run方法
 * 通过设置Order的value来指定执行的顺序
 */
@Component
@Order(value = 1)
public class StartService implements ApplicationRunner {
    //@Autowired
    //SystemUpgradeService systemUpgradeService;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        String strNow = UtilDate.getDateTimeStr(null, null);
        System.err.println("WebApplication start at " + strNow + ", Powered by DoYs Framework" + "\n\n");

        //systemUpgradeService.upgradeDatabase("");
        //systemUpgradeService.upgradeMenu();
    }
}