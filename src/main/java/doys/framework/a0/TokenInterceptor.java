package doys.framework.a0;
import doys.framework.a2.structure.InterceptorStatus;
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

public class TokenInterceptor extends BaseTop implements HandlerInterceptor {
    private static String[] FREE_TOKEN_CLZ = {
        "xxx"
    };
    // ------------------------------------------------------------------------
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        int result;

        ResourceHttpRequestHandler handlerResource;
        InterceptorStatus is = new InterceptorStatus();
        // ------------------------------------------------
        try {
            // -- 1. beforeHandle(子类先执行拦截) --
            result = beforeHandle(request, response, handler);
            if (result == InterceptorStatus.PASS) {
                // -- 1.1 子类无条件放行，父类不再继续判断 --
                return true;
            }
            else if (result == InterceptorStatus.DENIED) {
                return false;
            }

            if (handler instanceof HandlerMethod) {
                // -- 2. 验证token --
                checkToken(is, request, handler);
                if (!is.getInterceptResult()) {
                    if (doNoTokenRequest(request, (HandlerMethod) handler)) {
                        is.setPermit();
                    }
                }

                // -- 3. beforeHandle(子类后执行拦截) --
                result = afterHandle(request, response, handler, is);
                if (result == InterceptorStatus.PASS) {
                    return true;
                }
                else if (result == InterceptorStatus.DENIED) {
                    return false;
                }

                if (is.getInterceptResult()) {
                    if (result == InterceptorStatus.DENIED) {
                        // -- 3.1 父类虽然通过，但是子类拒绝。以子类为准 --
                        return false;
                    }
                }
                else {
                    if (result == InterceptorStatus.PASS) {
                        // -- 3.2 父类虽然没通过，但是子类强制放行 --
                        logger.info("父类不通过，但是子类无条件放行。");
                    }
                    else {
                        is.writeDeniedResponse(response);
                        return false;
                    }
                }
            }
            else if (handler instanceof ResourceHttpRequestHandler) {
                handlerResource = (ResourceHttpRequestHandler) handler;
                // logger.info("debug here, handlerResource");
            }
            else {
                logger.info("debug here");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
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

    // -- Token ---------------------------------------------------------------
    private void checkToken(InterceptorStatus is, HttpServletRequest request, Object handler) throws Exception {
        String tokenId = getTokenId(request);
        if (!tokenId.equals("")) {
            Token token = TokenService.getToken(tokenId);
            if (token != null && !token.timeout()) {
                token.renew();                                  // -- 1. 有效token：续租(新请求触发，token存在且未超时) --
                request.getSession().setAttribute("tenantId", token.tenantId);
            }
            else {
                is.setInterceptType(Const.ERR_TIMEOUT);       // -- 2. token超时 --
            }
        }
        else {
            is.setInterceptType(Const.ERR_NO_TOKEN);          // -- 3. 没有token或token不存在 --
        }
    }

    private boolean doNoTokenRequest(HttpServletRequest request, HandlerMethod handlerMethod) {
        String clz = handlerMethod.getBeanType().getName();

        if (clz.equals("doys.framework.system.User")) {
            int tenantId = getTenantId(request);
            if (tenantId > 0) {
                request.getSession().setAttribute("tenantId", tenantId);
                logger.info("tenantId = " + tenantId + ", 登录请求");
            }
            else {
                String methodName = handlerMethod.getMethod().getName();
                if (methodName.equals("getVerifyCode")) {
                    logger.info("取验证码");
                }
                else {
                    logger.info("非法登录请求，没有参数tenantId");
                }
            }
            return true;
        }
        else {
            // -- 免token的类 --
            for (int i = FREE_TOKEN_CLZ.length - 1; i >= 0; i--) {
                if (FREE_TOKEN_CLZ[i].equals(clz)) {
                    return true;
                }
            }
        }
        return false;
    }
    private int getTenantId(HttpServletRequest request) {
        return UserService.parseTenantId(request.getHeader("tenantId"));
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

    // -- sub class override --------------------------------------------------
    protected int beforeHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return InterceptorStatus.UNKNOWN;
    }
    protected int afterHandle(HttpServletRequest request, HttpServletResponse response, Object handler, InterceptorStatus is) {
        return InterceptorStatus.UNKNOWN;
    }
}