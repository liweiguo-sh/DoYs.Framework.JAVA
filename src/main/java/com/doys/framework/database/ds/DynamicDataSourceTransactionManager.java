package com.doys.framework.database.ds;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
public class DynamicDataSourceTransactionManager extends DataSourceTransactionManager {
    public DynamicDataSourceTransactionManager(DataSource dataSource) {
        super(dataSource);
    }
}
