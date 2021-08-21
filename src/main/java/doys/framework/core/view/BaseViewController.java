/******************************************************************************
 * Copyright (C), 2020-2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-05-15
 * @modify_date 2021-08-21
 * 通用视图控制类, 用于通用视图
 *****************************************************************************/
package doys.framework.core.view;
import doys.framework.a0.Const;
import doys.framework.core.base.BaseController;
import doys.framework.core.entity.RestResult;
import doys.framework.database.DBFactory;
import doys.framework.util.UtilString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.support.rowset.SqlRowSet;
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
    @Qualifier("tenantDBFactory")
    protected DBFactory dbBus;

    // -- view ----------------------------------------------------------------
    @PostMapping("/getViewSchema")
    private RestResult getViewSchema() {
        String viewPk = in("viewPk");
        String flowPks = in("flowPks");
        String treePk;

        SqlRowSet rsView, rsViewField, rsFlowNode, rsTree, rsTreeLevel;
        // ------------------------------------------------
        try {
            this.BeforeInit();

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

        SqlRowSet rsTree, rsTreeNode;
        // ------------------------------------------------
        try {
            rsTree = BaseViewService.getTree(dbSys, treePk);
            rsTree.first();

            rsTreeNode = BaseViewService.getTreeNode(dbSys, dbBus, treePk, nodeLevel, nodeValue);
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
        String sql, sqlFilter = in("filter");
        String sqlData, sqlOrderBy;
        String sqlUserDefDS;

        SqlRowSet rsView, rsViewData;
        // ------------------------------------------------
        try {
            rsView = BaseViewService.getView(dbSys, viewPk);

            // -- 1. 处理自定义数据源 -----------------------------
            sqlUserDefDS = getUseDefDataSource();
            if (sqlUserDefDS == null || sqlUserDefDS.equals("")) {
                sqlData = rsView.getString("sql_data_source");
            }
            else {
                sqlData = sqlUserDefDS;
            }
            sqlOrderBy = getViewOrderBy();
            if (sqlOrderBy.equals("")) {
                sqlOrderBy = rsView.getString("sql_orderby");
            }

            // -- 2. 取总记录行数 -----------------------------------
            if (pageNum == 0) {
                sql = "SELECT COUNT(1) FROM (" + sqlData + ") t ";
                if (!sqlFilter.equals("")) {
                    sql += "WHERE " + sqlFilter;
                }

                sql = BeforeReplace(sql);
                sql = ReplaceSql(sql);
                sql = AfterReplace(sql);
                ok("totalRows", dbBus.getInt(sql));
                pageNum = 1;
            }

            // -- 3. 取分页数据 ------------------------------------
            sql = "SELECT * FROM (" + sqlData + ") t ";
            if (!sqlFilter.equals("")) {
                sql += "WHERE " + sqlFilter + " ";
            }
            if (!UtilString.KillNull(sqlOrderBy).equals("")) {
                sql += "ORDER BY " + sqlOrderBy + " ";
            }
            sql += "LIMIT " + Const.MAX_PAGE_ROWS * (pageNum - 1) + ", " + Const.MAX_PAGE_ROWS;

            // -- 4. 执行sql，读取数据 ---------------------------
            sql = BeforeReplace(sql);
            sql = ReplaceSql(sql);
            sql = AfterReplace(sql);
            rsViewData = dbBus.getRowSet(sql);
            ok("dtbViewData", rsViewData);
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
            rsView = BaseViewService.getView(dbSys, viewPk);

            rsViewField = BaseViewService.getViewBaseField(dbSys, viewPk, rsView.getString("table_pk"));
            ok("dtbViewField", rsViewField);

            HashMap<String, SqlRowSet> mapDS = BaseViewService.getViewDS(dbBus, rsViewField);
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
        String tableName;

        SqlRowSet rsView, rsFormData;
        // ------------------------------------------------
        try {
            rsView = BaseViewService.getView(dbSys, viewPk);
            tableName = rsView.getString("table_name");

            rsFormData = BaseViewService.getFormData(dbBus, tableName, id);
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
        String sql, tableName, sqlDataSource;

        HashMap<String, Object> form = inForm("form");
        SqlRowSet rsView, rsFormData;
        // ------------------------------------------------
        try {
            // -- 1. pretreatment --
            rsView = BaseViewService.getView(dbSys, viewPk);
            tableName = rsView.getString("table_name");
            sqlDataSource = rsView.getString("sql_data_source");

            // -- 2.1 beforeSave --
            dbBus.beginTrans();
            if (!BeforeSave(blAddnew, id)) {
                return ResultErr();
            }
            // -- 3.2. save --
            if (blAddnew) {
                id = BaseViewService.insert(dbBus, tableName, form, this.session());
            }
            else {
                BaseViewService.update(dbSys, dbBus, tableName, form, this.session());
            }
            // -- 2.3 afterSave --
            if (!AfterSave(blAddnew, id)) {
                return ResultErr();
            }
            dbBus.commit();

            // -- 9. 返回当前行更新后的基础表数据和视图数据 --
            rsFormData = BaseViewService.getFormData(dbBus, tableName, id);
            ok("dtbFormData", rsFormData);

            sql = "SELECT * FROM (" + sqlDataSource + ") t WHERE id = ?";
            sql = BeforeReplace(sql);
            sql = ReplaceSql(sql);
            sql = AfterReplace(sql);
            ok("dtbViewData", dbBus.getRowSet(sql, id));
        } catch (Exception e) {
            TransactionStatus status = dbBus.getTransactionStatus();
            if (status != null && !status.isCompleted()) {
                try {
                    dbBus.rollback();
                } catch (Exception e1) {
                    return ResultErr(e1);
                }
            }
            return ResultErr(e);
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
        // ------------------------------------------------
        try {
            rsView = BaseViewService.getView(dbSys, viewPk);
            tableName = rsView.getString("table_name");

            // -- 2.1 beforeDelete --
            dbBus.beginTrans();
            if (!BeforeDelete(id)) {
                return ResultErr();
            }
            // -- 2.2 delete --
            BaseViewService.delete(dbBus, tableName, id);
            // -- 2.3 afterDelete --
            if (!AfterDelete(id)) {
                return ResultErr();
            }
            dbBus.commit();

            if (idNext > 0) {
                rsFormData = BaseViewService.getFormData(dbBus, tableName, idNext);
                ok("dtbFormData", rsFormData);
            }
        } catch (Exception e) {
            TransactionStatus status = dbBus.getTransactionStatus();
            if (status != null && !status.isCompleted()) {
                try {
                    dbBus.rollback();
                } catch (Exception e1) {
                    return ResultErr(e1);
                }
            }
            return ResultErr(e);
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
        // ------------------------------------------------
        try {
            rsView = BaseViewService.getView(dbSys, viewPk);
            tableName = rsView.getString("table_name");

            rsFlowButton = BaseViewService.getFlowButton(dbSys, flowPk, buttonPk);
            rsFlowButton.next();

            // -- 2.1 beforeSave --
            dbBus.beginTrans();
            if (!BeforeFlowClick(id, rsFlowButton)) {
                return ResultErr();
            }
            // -- 2.2 doFlow --
            BaseViewService.doFlow(dbBus, tableName, id, rsFlowButton, ssValue("userPk"));
            // -- 2.3 afterDoFlow --
            if (!AfterFlowClick(id, rsFlowButton)) {
                return ResultErr();
            }
            dbBus.commit();

            // -- 9. 返回结果 --
            if (idNext == 0) {
                idNext = id;
            }
            rsFormData = BaseViewService.getFormData(dbBus, tableName, idNext);
            ok("dtbFormData", rsFormData);
        } catch (Exception e) {
            TransactionStatus status = dbBus.getTransactionStatus();
            if (status != null && !status.isCompleted()) {
                try {
                    dbBus.rollback();
                } catch (Exception e1) {
                    return ResultErr(e1);
                }
            }
            return ResultErr(e);
        }
        return ResultOk();
    }
    @PostMapping("/doClick")
    private RestResult doClick() {
        long id = inInt("id");

        String buttonName = in("buttonName");
        // ------------------------------------------------
        try {
            dbBus.beginTrans();

            if (!ButtonClick(id, null, buttonName)) {
                return ResultErr();
            }

            dbBus.commit();
        } catch (Exception e) {
            TransactionStatus status = dbBus.getTransactionStatus();
            if (status != null && !status.isCompleted()) {
                try {
                    dbBus.rollback();
                } catch (Exception e1) {
                    return ResultErr(e1);
                }
            }
            return ResultErr(e);
        }
        return ResultOk();
    }

    // -- sub class override --------------------------------------------------
    protected void BeforeInit() throws Exception {
    }

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
    protected boolean BeforeFlowClick(long id, SqlRowSet rsFlowButton) throws Exception {
        return true;
    }
    protected boolean AfterFlowClick(long id, SqlRowSet rsFlowButton) throws Exception {
        return true;
    }

    protected boolean ButtonClick(long id, SqlRowSet rsViewButton, String buttonName) throws Exception {
        return true;
    }

    protected String BeforeReplace(String sql) {
        return sql;
    }
    private String ReplaceSql(String sql) {
        sql = sql.replaceAll("%userPk%", ssValue("userPk"));
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
    protected String getViewOrderBy() {
        return "";
    }
}