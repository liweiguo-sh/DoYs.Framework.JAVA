/******************************************************************************
 * Copyright (C), 2020-2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-07-07
 * @create_date 2021-02-08
 * 动态数据源工具类
 *****************************************************************************/
package doys.framework.database.ds;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import doys.framework.core.ex.SessionTimeoutException;
import doys.framework.database.DBFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UtilTDS {
    @Autowired
    private DBFactory _dbSys;

    @Value("${spring.datasource-tenant.url:}")
    private String _url;
    @Value("${spring.datasource-tenant.driver-class-name}")
    private String _driveClassName;
    @Value("${spring.datasource-tenant.hikari.maximum-pool-size:12}")      // -- 默认值写法, 避免配置文件未配置该项出错 --
    private int _maximumPoolSize;

    private static String url;
    private static String driveClassName;
    private static int maximumPoolSize;

    private static DBFactory dbSys;
    private static ConcurrentHashMap<String, DataSource> mapDS = new ConcurrentHashMap<>();
    // -- init ----------------------------------------------------------------
    @PostConstruct
    private void initDynamicDataSourceProperty() {
        dbSys = _dbSys;

        url = _url;
        driveClassName = _driveClassName;
        maximumPoolSize = _maximumPoolSize;
    }

    // -- DataSource ----------------------------------------------------------
    public static DataSource getDatasource(int tenantId) throws Exception {
        String dataSourceName = getDataSourceName(tenantId);

        if (!mapDS.containsKey(dataSourceName)) {
            createDataSource(dataSourceName);

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
    private static void createDataSource(String dataSourceName) throws Exception {
        int tenantId = UtilTDS.getTenantId();

        String sql, _username, _password;
        String urlDynamic = url;

        SqlRowSet rs;
        // ------------------------------------------------
        sql = "SELECT ip, port, username, password, db.name database_name, t.name "
            + "FROM sys_tenant t INNER JOIN sys_database db ON t.database_pk = db.pk INNER JOIN sys_instance inst ON db.instance_pk = inst.pk "
            + "WHERE t.id = ?";
        rs = dbSys.getRowSet(sql, tenantId);
        if (rs.next()) {
            urlDynamic = urlDynamic.replaceAll("\\{ip}", rs.getString("ip"));
            urlDynamic = urlDynamic.replaceAll("\\{port}", rs.getString("port"));
            urlDynamic = urlDynamic.replaceAll("\\{database}", rs.getString("database_name"));

            _username = rs.getString("username");
            _password = rs.getString("password");
        }
        else {
            throw new Exception("未找到租户(tenantId = " + tenantId + ")数据库，请检查。");
        }
        // ------------------------------------------------
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(urlDynamic);
        hikariConfig.setUsername(_username);
        hikariConfig.setPassword(_password);
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
        }
    }
    public static String getTenantDbName() throws Exception {
        // -- 租户的 sys_database.pk 必须和 sys_database.name 相同 --
        int tenantId = UtilTDS.getTenantId();

        String sql = "SELECT db.name database_name "
            + "FROM sys_tenant t INNER JOIN sys_database db ON t.database_pk = db.pk INNER JOIN sys_instance inst ON db.instance_pk = inst.pk "
            + "WHERE t.id = ?";

        return dbSys.getValue(sql, "", tenantId);
    }
    public static String getSysDbName(DBFactory dbSys) throws Exception {
        return dbSys.getValue("SELECT name FROM sys_database WHERE pk = 'sys'");
    }

    // -- DBFactory -----------------------------------------------------------
    public static DBFactory getDBFactory(int tenantId) throws Exception {
        return new DBFactory(getDatasource(tenantId));
    }
}