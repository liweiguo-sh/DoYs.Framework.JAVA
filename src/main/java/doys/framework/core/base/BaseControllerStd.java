package doys.framework.core.base;
import doys.framework.database.DBFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class BaseControllerStd extends BaseController {
    @Autowired
    protected DBFactory dbSys;

    @Autowired
    @Qualifier("tenantDBFactory")
    protected DBFactory dbTenant;
}