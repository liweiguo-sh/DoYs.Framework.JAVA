package com.doys.framework.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class ResRunConfig implements WebMvcConfigurer {
    @Value("${global.resRunPath}")
    private String resRunPath;
    @Value("${global.resTempPath}")
    private String resTempPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resRun/**").addResourceLocations("file:" + resRunPath + "/");
        registry.addResourceHandler("/resTemp/**").addResourceLocations("file:" + resTempPath + "/");
    }
}