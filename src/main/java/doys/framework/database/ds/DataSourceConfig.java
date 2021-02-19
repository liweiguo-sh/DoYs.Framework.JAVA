/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-06-23
 * 数据源配置类，支持动态数据源
 *****************************************************************************/
package doys.framework.database.ds;
import doys.framework.database.DBFactory;
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

    // -- 租户数据源 ---------------------------------------------------------------
    @Bean(name = "tenantDataSource")
    public TenantDataSource getTenantDataSource() {
        ConcurrentHashMap<Object, Object> mapDataSource = new ConcurrentHashMap<>();
        mapDataSource.put("sys", getSysDataSource(getSysDataSourceProperties()));

        TenantDataSource tenantDataSource = new TenantDataSource();
        tenantDataSource.setTargetDataSources(mapDataSource);
        tenantDataSource.setDefaultTargetDataSource(getSysDataSource(getSysDataSourceProperties()));

        return tenantDataSource;
    }

    @Bean(name = "tenantDBFactory")
    public DBFactory getTenantDBFactory(@Qualifier("tenantDataSource") TenantDataSource tenantDataSource) {
        return new DBFactory(tenantDataSource);
    }
}