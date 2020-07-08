/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-07-07
 * 动态数据源工具类
 *****************************************************************************/
package com.doys.framework.database.ds;
import com.doys.framework.core.ex.SessionTimeoutException;
import com.doys.framework.database.DBFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class UtilDDS {
    @Autowired
    private DBFactory dbSys;

    @Value("${spring.datasource-dynamic.url:}")
    private String _url;
    @Value("${spring.datasource-dynamic.username}")
    private String _username;
    @Value("${spring.datasource-dynamic.password}")
    private String _password;
    @Value("${spring.datasource-dynamic.driver-class-name}")
    private String _driveClassName;
    @Value("${spring.datasource-dynamic.hikari.maximum-pool-size:12}")      // -- 默认值写法, 避免配置文件未配置该项出错 --
    private int _maximumPoolSize;


    private static String prefix;
    private static String url;
    private static String username;
    private static String password;
    private static String driveClassName;
    private static int maximumPoolSize;

    private static ConcurrentHashMap<String, DataSource> mapDS = new ConcurrentHashMap<>();
    // -- init ----------------------------------------------------------------
    @PostConstruct
    private void initDynamicDataSourceProperty() {
        try {
            prefix = dbSys.getValue("SELECT name FROM sys_database WHERE pk = 'prefix'");
        } catch (Exception e) {
            System.err.println("The system database is misconfigured or unavailable, please check.");
            return;
        }

        // ------------------------------------------------------------------------
        url = _url;
        username = _username;
        password = _password;
        driveClassName = _driveClassName;

        maximumPoolSize = _maximumPoolSize;
    }

    // -- DataSource ----------------------------------------------------------
    public static DataSource getDatasource(int tenantId) throws Exception {
        String dataSourceName = getDataSourceName(tenantId);

        if (!mapDS.containsKey(dataSourceName)) {
            String databaseName = prefix + tenantId;
            createDataSource(dataSourceName, databaseName);

            if (!mapDS.containsKey(dataSourceName)) {
                System.err.println("Failed to create dynamic data source, please check.");
                return null;
            }
        }
        return mapDS.get(dataSourceName);
    }

    private static String getDataSourceName(int tenantId) {
        return "Hikari-dds-" + tenantId;
    }
    private static void createDataSource(String dataSourceName, String databaseName) {
        String urlDynamic = url.replaceAll("\\{database}", databaseName);

        HikariConfig hikariConfig = new HikariConfig();
        // ------------------------------------------------
        hikariConfig.setJdbcUrl(urlDynamic);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName(driveClassName);

        hikariConfig.setPoolName(dataSourceName);
        hikariConfig.setAutoCommit(true);

        hikariConfig.setMaximumPoolSize(maximumPoolSize);
        // ------------------------------------------------
        mapDS.put(dataSourceName, new HikariDataSource(hikariConfig));
    }
    private static void close() {
        for (DataSource ds : mapDS.values()) {
            ((HikariDataSource) ds).close();
        }
    }

    // -- Tenant --------------------------------------------------------------
    public static int getTenantId() throws Exception {
        try {
            return (int) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession().getAttribute("tenantId");
        } catch (NullPointerException e) {
            throw new SessionTimeoutException();
        } catch (Exception e) {
            throw e;
        }
    }
    public static String getTenantDbName(DBFactory dbSys) throws Exception {
        return prefix + UtilDDS.getTenantId();
    }
    // -- DBFactory -----------------------------------------------------------
    public static DBFactory getDBFactory(int tenantId) throws Exception {
        return new DBFactory(getDatasource(tenantId));
    }
}