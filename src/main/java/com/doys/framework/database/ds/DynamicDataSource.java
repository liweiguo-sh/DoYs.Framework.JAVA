package com.doys.framework.database.ds;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.sql.DataSource;
import java.util.Map;
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Value("${spring.datasource-dynamic.url}")
    protected String url;
    @Value("${spring.datasource-dynamic.username}")
    protected String username;
    @Value("${spring.datasource-dynamic.password}")
    protected String password;
    @Value("${spring.datasource-dynamic.driver-class-name}")
    protected String driveClassName;

    private String prefix;
    private Map<Object, Object> mapDS;
    // ------------------------------------------------------------------------
    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        this.mapDS = targetDataSources;
        super.setTargetDataSources(this.mapDS);
    }
    @Override
    protected DataSource determineTargetDataSource() {
        int tenantId;
        String dynamicDatabaseName;
        // -- 1. 得到动态数据库名称 --
        if (prefix == null) {
            if (mapDS.containsKey("sys")) {
                DataSource dsSys = (DataSource) mapDS.get("sys");
                JdbcTemplate jtSys = new JdbcTemplate(dsSys);
                Map<String, Object> map = jtSys.queryForMap("SELECT name FROM sys_database WHERE pk = 'prefix'");
                prefix = (String) map.get("name");
            }
            else {
                System.err.println("没有配置系统库数据源，请检查。");
                return null;
            }
        }
        tenantId = (int) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession().getAttribute("tenantId");
        dynamicDatabaseName = prefix + tenantId;

        // -- 2. 如不存在，创建动态数据库数据源 --
        if (!mapDS.containsKey(dynamicDatabaseName)) {
            DataSource ds = addNewDataSource((dynamicDatabaseName));
            if (ds != null) {
                mapDS.put(dynamicDatabaseName, ds);
            }
            else {
                return null;
            }
        }

        // -- 9. 返回动态数据源 --
        return (DataSource) mapDS.get(dynamicDatabaseName);
    }
    @Override
    protected Object determineCurrentLookupKey() {
        return null;
    }

    private DataSource addNewDataSource(String dynamicDatabaseName) {
        String urlDynamic = url.replaceAll("\\{database}", dynamicDatabaseName);

        HikariConfig hikariConfig = new HikariConfig();
        // ------------------------------------------------

        hikariConfig.setJdbcUrl(urlDynamic);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName(driveClassName);

        hikariConfig.setPoolName("Hikari-dds-" + dynamicDatabaseName);
        hikariConfig.setAutoCommit(true);
        //hikariConfig.setMaximumPoolSize(getPropertyInt("dds.maximum-pool-size", 10));

        // ------------------------------------------------
        return new HikariDataSource(hikariConfig);
    }
}