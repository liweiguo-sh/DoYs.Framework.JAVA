package com.doys.framework.upgrade.controller;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import com.doys.framework.upgrade.service.UpgradeDatabaseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/framework/upgrade")
public class UpgradeSystemController extends BaseController {
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