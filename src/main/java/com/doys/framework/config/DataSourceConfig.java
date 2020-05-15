package com.doys.framework.config;
import com.doys.framework.core.db.DBFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    //region -- 默认数据源：aprint --
    @Primary
    @Bean(name = "aprintDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties aprintDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "aprintDataSource")
    public DataSource aprintDataSource() {
        return aprintDataSourceProperties().initializeDataSourceBuilder().build();
    }

    /*
    @Primary
    @Bean(name = "aprintJdbcTemplet")
    public JdbcTemplate aprintJdbcTemplet(@Qualifier("aprintDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    */

    @Primary
    @Bean(name = "aprintDBFactory")
    public DBFactory aprintDBFactory(@Qualifier("aprintDataSource") DataSource dataSource) {
        return new DBFactory(dataSource);
    }

    //endregion

    //region -- 数据源：db2 --
    @Bean(name = "db2DataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.db2")
    public DataSourceProperties db2DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "db2DataSource")
    public DataSource db2DataSource() {
        return db2DataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "db2JdbcTemplate")
    public JdbcTemplate db2JdbcTemplate(@Qualifier("db2DataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    //endregion
}