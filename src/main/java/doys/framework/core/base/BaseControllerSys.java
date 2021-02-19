package doys.framework.core.base;
import doys.framework.database.DBFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseControllerSys extends BaseController {
    @Autowired
    protected DBFactory dbSys;
}
