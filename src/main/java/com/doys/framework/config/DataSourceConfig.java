package com.doys.framework.config;
import com.doys.framework.database.DBFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

//@Configuration
public class DataSourceConfig {
    //region -- 默认数据源：sys --
    @Primary
    @Bean(name = "sysDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties sysDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "sysDataSource")
    public DataSource sysDataSource() {
        return sysDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean(name = "sysDBFactory")
    public DBFactory sysDBFactory(@Qualifier("sysDataSource") DataSource dataSource) {
        return new DBFactory(dataSource);
    }

    /*
    @Primary
    @Bean(name = "aprintJdbcTemplet")
    public JdbcTemplate aprintJdbcTemplet(@Qualifier("aprintDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    */
    //endregion

    //region -- 数据源：bus --
    @Bean(name = "busDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.bus")
    public DataSourceProperties busDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "busDataSource")
    public DataSource db2DataSource() {
        return busDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "busDBFactory")
    public DBFactory busDBFactory(@Qualifier("busDataSource") DataSource dataSource) {
        return new DBFactory(dataSource);
    }

    /*
    @Bean(name = "busJdbcTemplate")
    public JdbcTemplate db2JdbcTemplate(@Qualifier("busDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    */
    //endregion
}