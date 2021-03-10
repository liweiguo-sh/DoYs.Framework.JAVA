package doys.framework.core.base;
import doys.framework.database.DBFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

public class BaseControllerTenant extends BaseController {
    @Autowired
    @Qualifier("tenantDBFactory")
    protected DBFactory dbTenant;

    @Autowired
    protected DataSourceTransactionManager dsTM;
    @Autowired
    protected TransactionDefinition tDEF;

    protected void commit(TransactionStatus tStatus) {
        if (tStatus == null) return;

        dsTM.commit(tStatus);
    }
    protected void rollback(TransactionStatus tStatus) {
        try {
            if (tStatus != null) {
                if (!tStatus.isCompleted()) {
                    dsTM.rollback(tStatus);
                }
            }
        } catch (Exception e) {
            err(e);
        }
    }
}