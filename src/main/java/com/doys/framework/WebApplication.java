/******************************************************************************
 * DoYs Web Application v1.0
 * Author: David.Li
 * Create Date: 2020-04-10
 * Modify Date: 2020-04-10
 * Copyright 2020, doys-next.com
 *****************************************************************************/
package com.doys.framework;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.doys")
@SpringBootApplication
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
