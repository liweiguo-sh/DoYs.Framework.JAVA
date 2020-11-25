package com.doys.aprint.print;
import com.doys.aprint.task.TaskService;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import com.doys.framework.database.DBFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
@RestController
@RequestMapping("/aprint/quick_print")
public class QuickPrintController extends BaseController {
    @Autowired
    DataSourceTransactionManager dstm;
    @Autowired
    TransactionDefinition tDef;
    // ------------------------------------------------------------------------
    @RequestMapping("/getInitData")
    private RestResult getInitData() {
        String sql;

        SqlRowSet rsLabel, rsCustomer, rsProductPn;
        // ------------------------------------------------
        try {
            sql = "SELECT id, code, name FROM base_label ORDER BY code, name";
            rsLabel = dbBus.getRowSet(sql);
            ok("dtbLabel", rsLabel);

            sql = "SELECT id, code, name FROM base_customer ORDER BY code, name";
            rsCustomer = dbBus.getRowSet(sql);
            ok("dtbCustomer", rsCustomer);

            sql = "SELECT pn.id, p.name product_name, pn.pn "
                + "FROM base_product p INNER JOIN base_product_pn pn ON p.id = pn.product_id ORDER BY p.name, pn.pn";
            rsProductPn = dbBus.getRowSet(sql);
            ok("dtbProductPn", rsProductPn);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }
    @RequestMapping("/getLabelAndLabelVariableById")
    private RestResult getLabelAndLabelVariableById() {
        int labelId = inInt("labelId");
        String sql;
        SqlRowSet rsLabel;
        // ------------------------------------------------
        try {
            sql = "SELECT * FROM base_label WHERE id = ?";
            rsLabel = dbBus.getRowSet(sql, labelId);
            ok("dtbLabel", rsLabel);

            ok("dtbLabelVariable", QuickPrintService.getLabelVariable(dbBus, labelId));
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    // ------------------------------------------------------------------------
    @RequestMapping("/createTask")
    private RestResult createTask() {
        int taskId;
        int labelId = inInt("labelId");
        int qty = inInt("qty");
        int copies = inInt("copies");

        String sql;
        String userPk = this.ssValue("userPk");

        ArrayList<HashMap<String, Object>> variables = inArrayList("variables");
        SqlRowSet rsTask;

        TransactionStatus tStatus = null;
        // ------------------------------------------------
        try {
            dstm.setDataSource(dbBus.getDataSource());
            tStatus = dstm.getTransaction(tDef);

            taskId = TaskService.createTask(dbBus, labelId, userPk);
            TaskService.generatePrintData(dbBus, labelId, qty, taskId, variables);
            dstm.commit(tStatus);

            sql = "SELECT * FROM core_task WHERE id = ?";
            rsTask = dbBus.getRowSet(sql, taskId);
            ok("dtbTask", rsTask);

            ok("dtbLabelVariable", QuickPrintService.getLabelVariable(dbBus, labelId));
        } catch (Exception e) {
            DBFactory.rollback(dstm, tStatus);
            return ResultErr(e);
        }
        return ResultOk();
    }
    @RequestMapping("/deleteTask")
    private RestResult deleteTask() {
        int taskId = inInt("taskId");
        // ------------------------------------------------
        try {
            TaskService.deleteTask(dbBus, taskId);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
    @RequestMapping("/getTaskData")
    private RestResult getTaskData() {
        int labelId = inInt("labelId");
        int taskId = inInt("taskId");
        int rowNoFrom = inInt("rowNoFrom");
        int rowNoTo = inInt("rowNoTo");

        SqlRowSet rsTaskData;
        // ------------------------------------------------
        try {
            rsTaskData = TaskService.getTaskData(dbBus, labelId, taskId, rowNoFrom, rowNoTo);
            ok("dtbTaskData", rsTaskData);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}