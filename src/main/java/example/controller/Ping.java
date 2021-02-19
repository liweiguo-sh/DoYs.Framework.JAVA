package example.controller;
import doys.framework.util.UtilDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ping")
public class Ping {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/sayHi")
    public String sayHello() {
        String strReturn = "";

        strReturn = "Hi 你好，this is server side response,  now is " + UtilDate.getDateTimeString() + ".";
        logger.info(strReturn);
        return strReturn;
    }

    @RequestMapping("/doLongWork")
    private String doLongWork() throws Exception {
        long seconds = 1500;
        String strReturn = "";
        // ------------------------------------------------
        try {
            logger.info("Do long work bgin " + UtilDate.getDateTimeString());
            Thread.sleep(seconds);
            strReturn = "A complex task took me " + seconds + " milliseconds";
            logger.info("Do long work end  " + UtilDate.getDateTimeString());

            return strReturn;
        } catch (Exception e) {
            throw e;
        }
    }
}
