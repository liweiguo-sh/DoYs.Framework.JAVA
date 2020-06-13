package com.doys.aprint.print;
import com.doys.aprint.task.TaskService;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.db.DBFactory;
import com.doys.framework.core.entity.RestResult;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
@RestController
@RequestMapping("/aprint/quick_print")
public class QuickPrintController extends BaseController {
    @Autowired
    protected DBFactory dbSys;

    @RequestMapping("/getInitData")
    private RestResult getInitData() {
        String sql;

        SqlRowSet rsLabel, rsCustomer, rsProductPn;
        // ------------------------------------------------
        try {
            sql = "SELECT id, code, name FROM ..base_label ORDER BY code, name";
            rsLabel = dbSys.getRowSet(sql);
            ok("dtbLabel", rsLabel);

            sql = "SELECT id, code, name FROM ..base_customer ORDER BY code, name";
            rsCustomer = dbSys.getRowSet(sql);
            ok("dtbCustomer", rsCustomer);

            sql = "SELECT pn.id, p.name product_name, pn.pn "
                + "FROM ..base_product p INNER JOIN ..base_product_pn pn ON p.id = pn.product_id ORDER BY p.name, pn.pn";
            rsProductPn = dbSys.getRowSet(sql);
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
        SqlRowSet rsLabel, rsLabelVariable;
        // ------------------------------------------------
        try {
            sql = "SELECT * FROM ..base_label WHERE id = ?";
            rsLabel = dbSys.getRowSet(sql, labelId);
            ok("dtbLabel", rsLabel);

            sql = "SELECT * FROM ..base_label_variable WHERE label_id = ? ORDER BY sequence, name";
            rsLabelVariable = dbSys.getRowSet(sql, labelId);
            ok("dtbLabelVariable", rsLabelVariable);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }

    @RequestMapping("/createTask")
    private RestResult createTask() {
        int taskId;
        int labelId = inInt("labelId");
        int qty = inInt("qty");
        int copies = inInt("copies");

        String userkey = this.ssValue("userkey");

        ArrayList<LinkedTreeMap<String, Object>> variables = inArrayList("variables");
        // ------------------------------------------------
        try {
            taskId = TaskService.createQuickPrintTask(dbSys, labelId, userkey);
            PrintService.generatePrintData(dbSys, labelId, qty, taskId, variables);

            ok("taskId", taskId);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
    @RequestMapping("/deleteTask")
    private RestResult deleteTask() {
        int taskId = inInt("taskId");
        // ------------------------------------------------
        try {
            PrintService.deleteTask(dbSys, taskId);
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
            rsTaskData = PrintService.getTaskData(dbSys, labelId, taskId, rowNoFrom, rowNoTo);
            ok("dtbTaskData", rsTaskData);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

}