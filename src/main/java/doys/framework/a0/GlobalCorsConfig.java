package doys.framework.a0;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class GlobalCorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // -- 1.1 允许任何域名使用 --
        //corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedOrigin("*");
        // -- 1.2 允许跨域携带cookie --
        corsConfiguration.setAllowCredentials(true);

        // -- 2. 允许任何头 --
        corsConfiguration.addAllowedHeader("*");
        // -- 3. 允许任何方法（post、get等） --
        corsConfiguration.addAllowedMethod("*");

        return corsConfiguration;
    }
}