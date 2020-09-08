/******************************************************************************
 * DoYs Web Application v1.0
 * Author: David.Li
 * Create Date: 2020-04-10
 * Modify Date: 2020-09-06
 * Copyright 2020, doys-next.com
 *****************************************************************************/
package com.doys.framework;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.doys", nameGenerator = WebApplication.SpringBeanNameGenerator.class)
public class WebApplication {
    public static class SpringBeanNameGenerator extends AnnotationBeanNameGenerator {
        @Override
        protected String buildDefaultBeanName(BeanDefinition definition) {
            return definition.getBeanClassName();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}