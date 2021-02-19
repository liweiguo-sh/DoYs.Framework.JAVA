package doys.framework.a0;
import doys.framework.util.UtilDate;
import doys.framework.util.UtilFile;
import doys.framework.util.UtilYml;
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
    @Override
    public void run(ApplicationArguments args) throws Exception {
        String strNow = UtilDate.getDateTimeStr(null, null);

        clearResTemp();
        System.err.println("WebApplication start at " + strNow + ", Powered by DoYs Framework");
    }

    private void clearResTemp() {
        String path;
        try {
            path = UtilYml.getTempRootPath();

            if (!UtilFile.emptyPath(path)) {
                System.err.println("清空临时目录失败，请检查。");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}