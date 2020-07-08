package com.doys.framework.database.ds;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected DataSource determineTargetDataSource() {
        try {
            int tenantId = UtilDDS.getTenantId();
            return UtilDDS.getDatasource(tenantId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected Object determineCurrentLookupKey() {
        return null;
    }
}