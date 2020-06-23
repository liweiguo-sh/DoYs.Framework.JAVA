package com.doys.framework.system.service;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.database.DBFactory;

import java.util.ArrayList;
public class CommonService extends BaseService {
    public static void clearGarbageData(DBFactory dbSys) throws Exception {
        int result;

        String sql;

        ArrayList<String> listSql = new ArrayList<>();
        // ------------------------------------------------
        listSql.add("DELETE FROM sys_table WHERE database_pk NOT IN (SELECT pk FROM sys_database)");
        listSql.add("DELETE FROM sys_field WHERE table_pk NOT IN (SELECT pk FROM sys_table)");

        listSql.add("DELETE FROM sys_menu WHERE LEFT(pk, 6) NOT IN (SELECT pk FROM (SELECT pk FROM sys_menu) t) OR LEFT(pk, 9) NOT IN (SELECT pk FROM (SELECT pk FROM sys_menu) t)");

        listSql.add("DELETE FROM sys_view_field WHERE view_pk NOT IN (SELECT pk FROM sys_view)");
        listSql.add("DELETE FROM sys_view_button WHERE view_pk NOT IN (SELECT pk FROM sys_view)");

        listSql.add("DELETE FROM sys_flow_node WHERE flow_pk NOT IN (SELECT pk FROM sys_flow)");
        listSql.add("DELETE FROM sys_flow_button WHERE flow_pk NOT IN (SELECT pk FROM sys_flow)");

        listSql.add("DELETE FROM sys_tree_level WHERE tree_pk NOT IN (SELECT pk FROM sys_tree)");
        // ------------------------------------------------
        for (int i = 0; i < listSql.size(); i++) {
            sql = listSql.get(i);
            result = dbSys.exec(sql);
        }
    }
}