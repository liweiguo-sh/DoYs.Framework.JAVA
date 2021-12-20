package doys.framework.a0;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// -- 停用类(注释@Configuration) --
//@Configuration
public class TokenInterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // -- 实现WebMvcConfigurer不会导致静态资源被拦截 --
        registry.addInterceptor(new TokenInterceptor()).addPathPatterns("/**");
    }
}