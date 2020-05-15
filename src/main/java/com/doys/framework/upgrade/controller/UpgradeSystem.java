package com.doys.framework.upgrade.controller;

import com.doys.framework.upgrade.service.SystemUpgradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/upgradeSystem")
public class UpgradeSystem {
    @Autowired
    SystemUpgradeService systemUpgradeService;
    @Value("${upgrade-database.entity-packages}")
    private String entityPackages;

    @RequestMapping("/getEntityPath")
    public String getEntityPath() {
        HashMap<String, String[]> map = new HashMap<>();
        String[] arrEntityPath = entityPackages.split(";");
        map.put("arrEntityPath", arrEntityPath);

        return "ok";
    }

    @RequestMapping("/upgrade")
    public String upgrade(@RequestBody Map<String, String> para) {
        String entityPaths = para.get("entityPaths");

        HashMap<String, String> map = new HashMap<>();
        try {
            if (!entityPaths.equals("")) {
                systemUpgradeService.upgradeDatabase(entityPaths);
            }
            //  systemUpgradeService.upgradeMenu();
            map.put("result", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ":  数据库升级成功");
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        return "ok";
    }
}