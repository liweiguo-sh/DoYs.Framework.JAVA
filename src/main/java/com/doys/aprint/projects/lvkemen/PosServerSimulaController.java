package com.doys.aprint.projects.lvkemen;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lvkemen/pos_simula_server")
public class PosServerSimulaController extends BaseController {
    @RequestMapping("/request1")
    public RestResult request1() {
        String resultString;
        String appId = in("appId");
        // ------------------------------------------------
        try {
            logger.info("appId = " + appId);

            String timeStamp = request().getHeader("time-stamp");
            String dataSignature = request().getHeader("data-signature");
            logger.info("time-stamp = " + timeStamp);
            logger.info("data-signature = " + dataSignature);

            resultString = "this is pos simula server renturn result 123";
            ok("result", resultString);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}