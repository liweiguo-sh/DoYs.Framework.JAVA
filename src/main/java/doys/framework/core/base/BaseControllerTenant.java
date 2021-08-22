package doys.framework.core.base;
import doys.framework.database.DBFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class BaseControllerTenant extends BaseController {
    @Autowired
    @Qualifier("tenantDBFactory")
    protected DBFactory dbTenant;
}