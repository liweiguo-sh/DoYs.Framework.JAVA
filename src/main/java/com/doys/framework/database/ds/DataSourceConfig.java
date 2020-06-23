/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-06-23
 * 数据源配置类，支持动态数据源
 *****************************************************************************/
package com.doys.framework.database.ds;
import com.doys.framework.database.DBFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class DataSourceConfig {
    // -- 默认数据源(主数据源)，即系统库数据源 -------------------------------------------------
    @Primary
    @Bean(name = "sysDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties getSysDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "sysDataSource")
    public DataSource getSysDataSource(@Qualifier("sysDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean(name = "sysDBFactory")
    public DBFactory getSysDBFactory(@Qualifier("sysDataSource") DataSource dataSource) {
        return new DBFactory(dataSource);
    }

    // -- 动态数据源(业务数据源)，即租户库数据源 ------------------------------------------------
    @Bean(name = "dynamicDataSource")
    public DynamicDataSource getDynamicDataSource() {
        ConcurrentHashMap<Object, Object> mapDataSource = new ConcurrentHashMap<>();
        mapDataSource.put("sys", getSysDataSource(getSysDataSourceProperties()));

        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(mapDataSource);
        dynamicDataSource.setDefaultTargetDataSource(getSysDataSource(getSysDataSourceProperties()));

        return dynamicDataSource;
    }

    @Bean(name = "dynamicDBFactory")
    public DBFactory getBusDBFactory(@Qualifier("dynamicDataSource") DynamicDataSource dynamicDataSource) {
        return new DBFactory(dynamicDataSource);
    }
}