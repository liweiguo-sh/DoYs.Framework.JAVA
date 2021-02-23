package doys.framework.a2;
import doys.framework.core.view.BaseViewController;
import doys.framework.database.DBFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/sys/base_view")
public class ViewControllerSys extends BaseViewController {
    @Autowired
    protected DBFactory dbBus;

    @PostConstruct
    private void replaceBus() {
        super.dbBus = this.dbBus;
    }
}