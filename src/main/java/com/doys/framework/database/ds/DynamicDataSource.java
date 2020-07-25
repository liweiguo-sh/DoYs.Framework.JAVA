package com.doys.framework.database.ds;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
public class DynamicDataSource extends AbstractRoutingDataSource {
    private int tenantId = 0;
    // ------------------------------------------------------------------------
    @Override
    protected DataSource determineTargetDataSource() {
        try {
            int tenantId = UtilDDS.getTenantId();
            this.tenantId = tenantId;
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

    public int getTenantId() {
        return this.tenantId;
    }
}