package com.doys.framework.config;
import org.springframework.jdbc.datasource.DelegatingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceDelegating extends DelegatingDataSource {
    private String catalogName;
    //创建时需要传入数据源和catalogName
    public DataSourceDelegating(DataSource dataSource, String catalogName) {
        super(dataSource);
        this.catalogName = catalogName;
    }
    //重写getConnection方法去重新set catalogName的值
    @Override
    public Connection getConnection() throws SQLException {
        final Connection connection = super.getConnection();
        connection.setCatalog(this.catalogName);
        return connection;
    }
}