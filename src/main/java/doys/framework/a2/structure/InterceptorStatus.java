/******************************************************************************
 * Copyright (C), 2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2021-12-20
 * @modify_date 2021-12-20
 * 拦截器状态类
 *****************************************************************************/
package doys.framework.a2.structure;
import com.fasterxml.jackson.databind.ObjectMapper;
import doys.framework.a0.Const;
import doys.framework.core.base.BaseTop;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

public class InterceptorStatus extends BaseTop {
    public static int PASS = 1;                         // -- 无条件通过 --
    public static int DENIED = -1;                      // -- 决绝 --
    public static int UNKNOWN = 0;                      // -- 不判断，由父类处理 --

    private boolean permit = true;                      // -- 拦截结果 --
    private String interceptType = "";                  // -- 拦截类型 --
    // ------------------------------------------------------------------------
    public boolean getInterceptResult() {
        return permit;
    }
    public void setPermit() {
        permit = true;
        interceptType = "";
    }
    public void writeDeniedResponse(HttpServletResponse response) throws Exception {
        HashMap<String, Object> map = new HashMap<>();

        map.put("ok", false);
        if (interceptType.equals(Const.ERR_NO_TOKEN)) {
            map.put("code", Const.ERR_NO_TOKEN);
            map.put("error", Const.ERROR_NO_TOKEN);
        }
        else if (interceptType.equals(Const.ERR_TIMEOUT)) {
            map.put("code", Const.ERR_TIMEOUT);
            map.put("error", Const.ERROR_TIMEOUT);
        }
        else {
            map.put("code", Const.ERR_UNKNOWN);
            map.put("error", Const.ERROR_UNKNOWN);
        }

        ObjectMapper mapper = new ObjectMapper();
        String responseString = mapper.writeValueAsString(map);
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        response.getWriter().write(responseString);
        logger.info(responseString);
    }

    public void setInterceptType(String type) {
        this.permit = false;
        interceptType = type;
    }
}