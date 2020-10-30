package com.doys.aprint.operator;
import com.doys.framework.core.view.BaseViewController;
import com.doys.framework.util.UtilDate;
import com.doys.framework.util.UtilString;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprint/operator/RptTaskView")
public class RptTaskView extends BaseViewController {
    @Override
    protected void BeforeInit() throws Exception {
        int result;

        String sql;
        String lastDate, today = UtilDate.getDateStr();
        // ------------------------------------------------
        try {
            sql = "SELECT MAX(task_date) last_date FROM rpt_task_day";
            lastDate = dbSys.getValue(sql, "");
            if (UtilString.equals(lastDate, today)) {
                return;                     // -- 当天执行过统计工作 --
            }
            else if (lastDate.equals("")) {
                lastDate = "2020-01-01";    // -- 第一次执行 --
            }

            RptTaskService.runTaskStatistics(dbSys, lastDate);
        } catch (Exception e) {
            throw e;
        }
    }
}
