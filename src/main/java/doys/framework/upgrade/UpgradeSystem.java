package doys.framework.upgrade;
import doys.framework.core.base.BaseControllerStd;
import doys.framework.core.entity.RestResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/framework/upgrade")
public class UpgradeSystem extends BaseControllerStd {
    @Value("${upgrade-database.entity-packages}")
    private String entityPackages;
    // ------------------------------------------------------------------------
    @RequestMapping("/database")
    public RestResult upgradeDatabase() {
        try {
            UpgradeDatabaseService.upgrade(dbSys, entityPackages);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}