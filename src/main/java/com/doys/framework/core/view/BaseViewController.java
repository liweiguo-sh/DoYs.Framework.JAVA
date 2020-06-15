/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-05-15
 * 通用视图控制类, 用于通用视图
 *****************************************************************************/
package com.doys.framework.core.view;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.db.DBFactory;
import com.doys.framework.core.entity.RestResult;
import com.doys.framework.util.UtilString;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/core/base_view")
public class BaseViewController extends BaseController {
    @Autowired
    protected DBFactory dbSys;
    @Autowired
    DataSourceTransactionManager dstm;
    @Autowired
    TransactionDefinition tDef;

    // -- view ----------------------------------------------------------------
    @PostMapping("/getViewSchema")
    private RestResult getViewSchema() {
        String viewPk = in("viewPk");
        String flowPks = in("flowPks");
        String treePk;

        SqlRowSet rsView, rsViewField, rsFlowNode, rsTree, rsTreeLevel;
        // ------------------------------------------------
        try {
            rsView = BaseViewService.getView(dbSys, viewPk);
            ok("dtbView", rsView);
            rsView.beforeFirst();
            if (rsView.next()) {
                treePk = UtilString.KillNull(rsView.getString("tree_pk"));
            }
            else {
                return ResultErr("视图 " + viewPk + " 不存在，请检查。");
            }

            // -- view field --
            rsViewField = getViewField(viewPk);
            ok("dtbViewField", rsViewField);

            // -- flow tree --
            if (!flowPks.equals("")) {
                rsFlowNode = BaseViewService.getFlowNode(dbSys, flowPks);
                ok("dtbFlowNode", rsFlowNode);
            }

            // -- navigate tree --
            if (!treePk.equals("")) {
                rsTree = BaseViewService.getTree(dbSys, treePk);
                ok("dtbTree", rsTree);
                rsTreeLevel = BaseViewService.getTreeLevel(dbSys, treePk);
                ok("dtbTreeLevel", rsTreeLevel);
            }
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
    @PostMapping("/getTreeNode")
    private RestResult getTreeNode() {
        int nodeLevel = inInt("nodeLevel");

        String treePk = in("treePk");
        String nodeValue = in("nodeValue");

        SqlRowSet rsTreeNode;
        // ------------------------------------------------
        try {
            rsTreeNode = BaseViewService.getTreeNode(dbSys, treePk, nodeLevel, nodeValue);
            ok("dtbTreeNode", rsTreeNode);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    @PostMapping("/getViewData")
    private RestResult getViewData() {
        int pageNum = inInt("pageNum");

        String viewPk = in("viewPk");
        String sqlFilter = in("filter");
        String sqlUserDefDS;

        SqlRowSet rsView, rsViewData;
        HashMap<String, Long> mapRef = (pageNum == 0 ? new HashMap<>() : null);
        // ------------------------------------------------
        try {
            rsView = BaseViewService.getView(dbSys, viewPk);

            sqlUserDefDS = getUseDefDataSource();
            rsViewData = BaseViewService.getViewData(dbSys, rsView, pageNum, sqlFilter, mapRef, sqlUserDefDS);
            ok("dtbViewData", rsViewData);

            if (pageNum == 0) {
                ok("totalRows", mapRef.get("totalRows"));
            }
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    // -- view form -----------------------------------------------------------
    @PostMapping("/getFormSchema")
    private RestResult getFormSchema(@RequestBody Map<String, String> req) {
        String viewPk = req.get("viewPk");
        String flowPks = req.get("flowPks");

        SqlRowSet rsView, rsViewField, rsFlowButton, rsViewButton;
        // ------------------------------------------------
        try {
            //rsView = BaseViewService.getView(jtMaster, viewPk);
            //ok("dtbView", rsView);
            rsViewField = this.getViewField(viewPk);
            //ok("dtbViewField", rsViewField);

            HashMap<String, SqlRowSet> mapDS = BaseViewService.getViewDS(dbSys, rsViewField);
            for (Map.Entry<String, SqlRowSet> entry : mapDS.entrySet()) {
                ok("dtbCDS_" + entry.getKey(), entry.getValue());
            }

            if (!flowPks.equals("")) {
                rsFlowButton = BaseViewService.getFlowButton(dbSys, flowPks);
                ok("dtbFlowButton", rsFlowButton);
            }

            rsViewButton = BaseViewService.getViewButton(dbSys, viewPk);
            ok("dtbViewButton", rsViewButton);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
    @PostMapping("/getFormData")
    private RestResult getFormData() {
        int id = inInt("id");

        String viewPk = in("viewPk");

        SqlRowSet rsView, rsFormData;
        // ------------------------------------------------
        try {
            rsView = BaseViewService.getView(dbSys, viewPk);
            rsFormData = BaseViewService.getFormData(dbSys, rsView, id);
            ok("dtbFormData", rsFormData);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    // -- crud process and flow -----------------------------------------------
    @PostMapping("/save")
    private RestResult save() {
        long id = inInt("id");
        boolean blAddnew = (id == 0);

        String viewPk = in("viewPk");
        String tableName;

        LinkedTreeMap<String, Object> form = inForm("form");
        SqlRowSet rsView, rsFormData, rsViewData;

        TransactionStatus tStatus = null;
        // ------------------------------------------------
        try {
            // -- 1. pretreatment --
            rsView = BaseViewService.getView(dbSys, viewPk);
            rsView.first();
            tableName = rsView.getString("table_name");

            // -- 2.1 beforeSave --
            tStatus = dstm.getTransaction(tDef);
            if (!BeforeSave(blAddnew, id)) {
                return ResultErr();
            }
            // -- 3.2. save --
            if (blAddnew) {
                id = BaseViewService.insert(dbSys, tableName, form);
            }
            else {
                BaseViewService.update(dbSys, tableName, form);
            }
            // -- 2.3 afterSave --
            if (!AfterSave(blAddnew, id)) {
                return ResultErr();
            }
            dstm.commit(tStatus);

            // -- 9. 返回当前行更新后的基础表数据和视图数据 --
            rsFormData = BaseViewService.getFormData(dbSys, rsView, id);
            ok("dtbFormData", rsFormData);
            rsViewData = BaseViewService.getViewDataOne(dbSys, rsView, id);
            ok("dtbViewData", rsViewData);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
            rollback(tStatus);
        }
        return ResultOk();
    }
    @PostMapping("/delete")
    private RestResult delete() {
        long id = inInt("id");
        long idNext = inInt("idNext", 0);

        String viewPk = in("viewPk");
        String tableName;

        SqlRowSet rsView, rsFormData;
        TransactionStatus tStatus = null;
        // ------------------------------------------------
        try {
            rsView = BaseViewService.getView(dbSys, viewPk);
            rsView.first();
            tableName = rsView.getString("table_name");

            // -- 2.1 beforeDelete --
            tStatus = dstm.getTransaction(tDef);
            if (!BeforeDelete(id)) {
                return ResultErr();
            }
            // -- 2.2 delete --
            BaseViewService.delete(dbSys, tableName, id);
            // -- 2.3 afterDelete --
            if (!AfterDelete(id)) {
                return ResultErr();
            }
            dstm.commit(tStatus);

            if (idNext > 0) {
                rsFormData = BaseViewService.getFormData(dbSys, rsView, idNext);
                ok("dtbFormData", rsFormData);
            }
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
            rollback(tStatus);
        }
        return ResultOk();
    }

    @PostMapping("/doFlow")
    private RestResult doFlow() {
        long id = inInt("id");
        long idNext = inInt("idNext", 0);

        String viewPk = in("viewPk");
        String flowPk = in("flowPk");
        String buttonPk = in("buttonPk");
        String tableName;

        SqlRowSet rsView, rsFlowButton, rsFormData;
        TransactionStatus tStatus = null;
        // ------------------------------------------------
        try {
            rsView = BaseViewService.getView(dbSys, viewPk);
            rsView.first();
            tableName = rsView.getString("table_name");

            rsFlowButton = BaseViewService.getFlowButton(dbSys, flowPk, buttonPk);

            // -- 2.1 beforeSave --
            tStatus = dstm.getTransaction(tDef);
            if (!BeforeFlowClick(id, rsFlowButton)) {
                return ResultErr();
            }
            // -- 2.2 doFlow --
            BaseViewService.doFlow(dbSys, tableName, id, rsFlowButton);
            // -- 2.3 afterDoFlow --
            if (!AfterFlowClick(id, rsFlowButton)) {
                return ResultErr();
            }
            dstm.commit(tStatus);

            // -- 9. 返回结果 --
            if (idNext == 0) {
                idNext = id;
            }
            rsFormData = BaseViewService.getFormData(dbSys, rsView, idNext);
            ok("dtbFormData", rsFormData);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
            rollback(tStatus);
        }
        return ResultOk();
    }

    // -- common --------------------------------------------------------------
    private void rollback(TransactionStatus tStatus) {
        try {
            if (tStatus != null) {
                if (!tStatus.isCompleted()) {
                    dstm.rollback(tStatus);
                }
            }
        } catch (Exception e) {
            err(e);
        }
    }

    // -- sub class override --------------------------------------------------
    protected boolean BeforeSave(boolean addnew, long id) throws Exception {
        return true;
    }
    protected boolean AfterSave(boolean addnew, long id) throws Exception {
        return true;
    }
    protected boolean BeforeDelete(long id) throws Exception {
        return true;
    }
    protected boolean AfterDelete(long id) throws Exception {
        return true;
    }
    protected boolean BeforeFlowClick(long id, SqlRowSet rsFlowButton) {
        return true;
    }
    protected boolean AfterFlowClick(long id, SqlRowSet rsFlowButton) {
        return true;
    }

    protected String BeforeReplace(String sql) {
        return sql;
    }
    private String ReplaceSql(String sql) {
        return sql;
    }
    protected String AfterReplace(String sql) {
        return sql;
    }

    // -- sub class override 2 ------------------------------------------------
    protected SqlRowSet getViewField(String viewPk) throws Exception {
        return BaseViewService.getViewField(dbSys, viewPk);
    }
    protected String getUseDefDataSource() {
        return null;
    }
}