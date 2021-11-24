package doys.framework.a0;
import com.fasterxml.jackson.databind.ObjectMapper;
import doys.framework.core.Token;
import doys.framework.core.TokenService;
import doys.framework.core.base.BaseTop;
import doys.framework.system.UserService;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

public class TokenInterceptor extends BaseTop implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        int tenantId = 0;
        String clz, tokenId;

        HandlerMethod handlerMethod;
        ResourceHttpRequestHandler handlerResource;
        // ------------------------------------------------
        try {
            tokenId = getTokenId(request);
            if (!tokenId.equals("")) {
                Token token = TokenService.getToken(tokenId);
                if (token != null) {
                    tenantId = token.tenantId;
                    request.getSession().setAttribute("tenantId", tenantId);
                }
            }
            if (tenantId <= 0) {
                tenantId = getTenantId(request);
            }

            if (handler instanceof HandlerMethod) {
                handlerMethod = (HandlerMethod) handler;
                clz = handlerMethod.getBeanType().getName();

                if (clz.equals("doys.framework.system.User")) {
                    logger.info("tenantId = " + tenantId + ", 登录请求");
                }
                else {
                    if (tokenId.equals("")) {
                        responseNoToken(response);
                        return false;
                    }

                    Token token = TokenService.getToken(tokenId);
                    if (token == null || token.checkTimeout()) {
                        responseTimeout(response);
                        return false;
                    }
                }
            }
            else if (handler instanceof ResourceHttpRequestHandler) {
                handlerResource = (ResourceHttpRequestHandler) handler;
            }
            else {
                logger.info("debug here");
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    private void responseNoToken(HttpServletResponse response) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        map.put("ok", false);
        map.put("code", Const.ERR_NO_TOKEN);
        map.put("error", Const.ERROR_NO_TOKEN);

        ObjectMapper mapper = new ObjectMapper();
        String responseString = mapper.writeValueAsString(map);
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        response.getWriter().write(responseString);
    }
    private void responseTimeout(HttpServletResponse response) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        map.put("ok", false);
        map.put("code", Const.ERR_TIMEOUT);
        map.put("error", Const.ERROR_TIMEOUT);

        ObjectMapper mapper = new ObjectMapper();
        String responseString = mapper.writeValueAsString(map);
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        response.getWriter().write(responseString);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // -- 执行完方法之后进执行(Controller方法调用之后)，但是此时还没进行视图渲染 --
        //logger.info("-- 2. postHandle --");
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // -- 整个请求都处理完咯，DispatcherServlet也渲染了对应的视图咯，此时我可以做一些清理的工作了") --
        //logger.info("-- 2. postHandle --");
    }

    // ------------------------------------------------------------------------
    private int getTenantId(HttpServletRequest request) {
        int tenantId = UserService.parseTenantId(request.getHeader("tenantId"));
        if (tenantId > 0) {
            request.getSession().setAttribute("tenantId", tenantId);
        }
        return tenantId;
    }
    private String getTokenId(HttpServletRequest request) {
        String tokenId = request.getHeader("token");

        if (tokenId == null || tokenId.equals("")) {
            tokenId = request.getHeader("authorization");       // -- 老王客户端 --
        }

        if (tokenId == null || tokenId.equals("")) {
            return "";
        }
        return tokenId;
    }
}