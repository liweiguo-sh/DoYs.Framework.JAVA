package ems.bus;
import doys.framework.core.view.BaseViewControllerTenant;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/ems/bus/request_repair_view")
public class RequestRepairViewTenant extends BaseViewControllerTenant {
    @Override
    protected boolean BeforeSave(boolean addnew, long id) throws Exception {

        if (addnew) {


        }
        return true;
    }
}
