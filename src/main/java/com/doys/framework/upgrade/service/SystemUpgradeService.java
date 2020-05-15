package com.doys.framework.upgrade.service;

import com.doys.framework.upgrade.db.UpgradeDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemUpgradeService {
    @Autowired
    UpgradeDatabaseService upgradeDatabase;

    /**
     * 升级数据库
     *
     * @param entityPaths 实体类路径。默认为空，从配置文件中读取，也可以通过参数指定
     * @throws Exception
     */
    public void upgradeDatabase(String entityPaths) throws Exception {
        upgradeDatabase.upgrade(entityPaths);
    }

    public void upgradeMenu() throws Exception {
        //upgradeMenu.upgrade();
    }

    public void upgradeUView() {
    }
}