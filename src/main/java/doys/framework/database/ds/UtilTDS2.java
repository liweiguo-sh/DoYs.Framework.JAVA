/******************************************************************************
 * Copyright (C), 2020-2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-07-07
 * @modify_date 2021-02-04
 * TDS(Tenant Data Source) static tool class
 *****************************************************************************/
package doys.framework.database.ds;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import doys.framework.core.ex.UnexpectedException;
import doys.framework.database.DBFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UtilTDS2 {
    @Value("${spring.datasource.url:}")
    private String _urlSys;
    @Value("${spring.datasource.username}")
    private String _usernameSys;
    @Value("${spring.datasource.password}")
    private String _passwordSys;
    @Value("${spring.datasource.driver-class-name}")
    private String _driveClassNameSys;
    @Value("${spring.datasource.hikari.maximum-pool-size:12}")      // -- 默认值写法, 避免配置文件未配置该项出错 --
    private int _maximumPoolSizeSys;

    private static String urlSys;
    private static String usernameSys;
    private static String passwordSys;
    private static String driveClassNameSys;
    private static int maximumPoolSizeSys;


    private static String dsPrefix = "Hikari-";
    private static String sysDsName = dsPrefix + "sys";
    private static DBFactory dbSys;

    private static ConcurrentHashMap<String, DataSource> mapDS = new ConcurrentHashMap<>();
    // -- init ----------------------------------------------------------------
    @PostConstruct
    private void initSysDataSourceProperty() {
        urlSys = _urlSys;
        usernameSys = _usernameSys;
        passwordSys = _passwordSys;
        driveClassNameSys = _driveClassNameSys;

        maximumPoolSizeSys = _maximumPoolSizeSys;

        createSysDataSource();
    }

    private static void createSysDataSource() {
        if (mapDS.containsKey(sysDsName)) {
            System.err.println("debug here");
            return;
        }

        // ------------------------------------------------
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName(sysDsName);

        hikariConfig.setJdbcUrl(urlSys);
        hikariConfig.setUsername(usernameSys);
        hikariConfig.setPassword(passwordSys);
        hikariConfig.setDriverClassName(driveClassNameSys);

        hikariConfig.setAutoCommit(true);
        hikariConfig.setMaximumPoolSize(maximumPoolSizeSys);

        // ------------------------------------------------
        mapDS.put(sysDsName, new HikariDataSource(hikariConfig));
    }
    private static void createDataSource(String dsName, String ip, String port, String databaseName, String username, String password) {
        String url = "jdbc:mysql://" + ip + ":" + port + "/" + databaseName
            + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&rewriteBatchedStatements=true&serverTimezone=Asia/Shanghai&autoReconnect=true&zeroDateTimeBehavior=convertToNull";

        HikariConfig hikariConfig = new HikariConfig();
        // ------------------------------------------------
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName(driveClassNameSys);

        hikariConfig.setPoolName(dsName);
        hikariConfig.setAutoCommit(true);

        hikariConfig.setMaximumPoolSize(15);
        // ------------------------------------------------
        mapDS.put(dsName, new HikariDataSource(hikariConfig));
    }
    private static DataSource getSysDataSource() throws Exception {
        if (mapDS.containsKey(sysDsName)) {
            return mapDS.get(sysDsName);
        }
        else {
            throw new UnexpectedException("getSysDataSouce");
        }
    }

    public static DBFactory getDBFactory(String databasePk) throws Exception {
        return getDBFactory(databasePk, 0);
    }
    public static DBFactory getDBFactory(String databasePk, int tenantId) throws Exception {
        String sql;
        String dsName = dsPrefix + databasePk;

        SqlRowSet rs;

        // -- 1. 首次使用时创建数据源 --
        if (!mapDS.containsKey(dsName)) {
            if (dbSys == null) {
                dbSys = new DBFactory(getSysDataSource());
            }

            sql = "SELECT db.pk, ip, port, username, password, db.name database_name "
                + "FROM sys_instance inst INNER JOIN sys_database db ON inst.pk = db.instance_pk WHERE db.pk = ?";
            rs = dbSys.getRowSet(sql, databasePk);
            if (rs.next()) {
                String ip = rs.getString("ip");
                String port = rs.getString("port");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String databaseName = rs.getString("database_name");

                createDataSource(dsName, ip, port, databaseName, username, password);
            }
            else {
                throw new UnexpectedException("未找到 database.pk (" + databasePk + ") 的记录，请检查。");
            }
        }

        // -- 2. 返回结果 --
        return new DBFactory(mapDS.get(dsName));
    }
}