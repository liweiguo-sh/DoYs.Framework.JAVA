package com.doys.aprint.operator;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.database.DBFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;
public class RptTaskService extends BaseService {
    public static void runTaskStatistics(DBFactory dbSys, String lastDate) throws Exception {
        String sql;
        String lastPeriod = lastDate.substring(0, 7);

        SqlRowSet rsTenant;
        // -- 1. 删除系统库统计数据 --
        sql = "DELETE FROM rpt_task_day WHERE task_date >= ?";
        dbSys.exec(sql, lastDate);
        sql = "DELETE FROM rpt_task_month WHERE task_period >= ?";
        dbSys.exec(sql, lastPeriod);

        // -- 2. 循环商户 --
        sql = "SELECT id, database_name FROM sys_tenant";
        rsTenant = dbSys.getRowSet(sql);
        while ((rsTenant.next())) {
            int tenantId = rsTenant.getInt("id");
            String dbName = "aprint_" + tenantId;

            // -- 2.0 临时代码，修补错误的历史数据 --
            if (lastDate.equals("2020-01-01")) {
                int qtyTask = 0;
                sql = "SELECT label_id, id FROM " + dbName + ".core_task WHERE bus_date >= ?";
                SqlRowSet rs = dbSys.getRowSet(sql, lastDate);
                while (rs.next()) {
                    int taskId = rs.getInt("id");
                    int labelId = rs.getInt("label_id");
                    try {
                        sql = "SELECT COUNT(1) FROM " + dbName + ".x_label_" + labelId + " WHERE task_id = ?";
                        qtyTask = dbSys.getInt(sql, 0, taskId);

                        sql = "UPDATE " + dbName + ".core_task SET qty = ? WHERE id = ?";
                        dbSys.exec(sql, qtyTask, taskId);
                    } catch (Exception e) {
                        System.out.println("旧数据错误，忽略。");
                    }
                }
            }

            // -- 2.1 清除商户数据 --
            sql = "DELETE FROM " + dbName + ".rpt_task_day WHERE task_date >= ?";
            dbSys.exec(sql, lastDate);

            // -- 2.2 统计商户数据 --
            sql = "INSERT INTO " + dbName + ".rpt_task_day "
                + "SELECT DATE(bus_date) task_date, COUNT(1) qty_task, SUM(qty) qty_print "
                + "FROM " + dbName + ".core_task "
                + "WHERE bus_date >= ? GROUP BY DATE(bus_date)";
            dbSys.exec(sql, lastDate);

            // -- 2.3 将商户数据导入到运营商 --
            sql = "INSERT INTO rpt_task_day "
                + "SELECT ? tenant_id, task_date, qty_task, qty_print "
                + "FROM " + dbName + ".rpt_task_day "
                + "WHERE task_date >= ?";
            dbSys.exec(sql, tenantId, lastDate);
        }

        // -- 3. 运营商日数据汇总到月数据 --
        sql = "INSERT INTO rpt_task_month "
            + "SELECT tenant_id, DATE_FORMAT(task_date, '%Y-%m') period, SUM(qty_task) qty_task, SUM(qty_print) qty_print "
            + "FROM rpt_task_day WHERE task_date >= ? GROUP BY tenant_id, period";
        dbSys.exec(sql, lastPeriod + "-01");
    }
}