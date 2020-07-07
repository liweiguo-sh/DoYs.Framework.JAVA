package com.doys.framework.database.ds;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.sql.DataSource;
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected DataSource determineTargetDataSource() {
        try {
            int tenantId = (int) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession().getAttribute("tenantId");
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