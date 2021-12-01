/******************************************************************************
 * Copyright (C), 2020-2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-07-07
 * @create_date 2021-11-22
 * TDS(Tenant Data Source)商户动态数据源工具类
 *****************************************************************************/
package doys.framework.database.ds;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import doys.framework.core.Token;
import doys.framework.core.TokenService;
import doys.framework.core.ex.CommonException;
import doys.framework.core.ex.SessionTimeoutException;
import doys.framework.database.DBFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

    private static String sysDbName, prefixDbName;
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
            createDataSource(dataSourceName, tenantId);

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
    private static void createDataSource(String dataSourceName, int tenantId) throws Exception {
        String sql, dbname, _username, _password;
        String urlDynamic = url;

        SqlRowSet rs;
        // ------------------------------------------------
        dbname = dbSys.getValue("SELECT name FROM sys_database WHERE pk = 'prefix'") + tenantId;
        sql = "SELECT ip, port, username, password "
            + "FROM sys_tenant t INNER JOIN sys_instance inst ON t.instance_pk = inst.pk "
            + "WHERE t.id = ?";
        rs = dbSys.getRowSet(sql, tenantId);
        if (rs.next()) {
            urlDynamic = urlDynamic.replaceAll("\\{ip}", rs.getString("ip"));
            urlDynamic = urlDynamic.replaceAll("\\{port}", rs.getString("port"));
            urlDynamic = urlDynamic.replaceAll("\\{database}", dbname);

            _username = rs.getString("username");
            _password = rs.getString("password");
        }
        else {
            throw new CommonException("未找到租户(tenantId = " + tenantId + ")数据库，请检查。");
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
    public static HttpServletRequest getRequest() throws Exception {
        HttpServletRequest request;
        try {
            request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            return request;
        } catch (NullPointerException e) {
            throw new SessionTimeoutException();
        }
    }
    public static HttpSession getSession() throws Exception {
        HttpSession ss;
        try {
            ss = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
            return ss;
        } catch (NullPointerException e) {
            throw new SessionTimeoutException();
        }
    }

    public static int getTenantId() throws Exception {
        try {
            return (int) getSession().getAttribute("tenantId");
        } catch (NullPointerException e) {
            throw new SessionTimeoutException();
        }
    }
    public static String getUserPk() throws Exception {
        try {
            String tokenId = getRequest().getHeader("token");
            Token token = TokenService.getToken(tokenId);

            return token.userPk;
        } catch (NullPointerException e) {
            throw new SessionTimeoutException();
        }
    }
    public static String getTenantDbName() throws Exception {
        int tenantId = UtilTDS.getTenantId();

        if (prefixDbName == null || prefixDbName.equals("")) {
            prefixDbName = dbSys.getValue("SELECT name FROM sys_database WHERE pk = 'prefix'");
        }

        return prefixDbName + tenantId;
    }
    public static String getSysDbName(DBFactory dbSys) throws Exception {
        if (sysDbName == null || sysDbName.equals("")) {
            sysDbName = dbSys.getValue("SELECT name FROM sys_database WHERE pk = 'sys'");
        }
        return sysDbName;
    }

    // -- DBFactory -----------------------------------------------------------
    public static DBFactory getDbSys() {
        return dbSys;
    }
    public static DBFactory getDBFactory(int tenantId) throws Exception {
        return new DBFactory(getDatasource(tenantId));
    }
}