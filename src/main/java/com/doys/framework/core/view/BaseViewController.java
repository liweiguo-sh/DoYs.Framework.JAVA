package com.doys.framework.core.view;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.db.DBFactory;
import com.doys.framework.core.entity.RestResult;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
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
    DBFactory jtMaster;

    @PostMapping("/getViewSchema")
    public RestResult getViewSchema(@RequestBody Map<String, String> req) {
        String viewPk = req.get("viewPk");
        String flowPk = req.get("flowPk");

        SqlRowSet rsView, rsViewField, rsFlowNode;
        // ------------------------------------------------
        try {
            rsView = BaseViewService.getView(jtMaster, viewPk);
            ok("dtbView", rsView);
            rsViewField = BaseViewService.getViewField(jtMaster, viewPk);
            ok("dtbViewField", rsViewField);

            if (!flowPk.equals("")) {
                rsFlowNode = BaseViewService.getFlowNode(jtMaster, flowPk);
                ok("dtbFlowNode", rsFlowNode);
            }
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    @PostMapping("/getViewData")
    public RestResult getViewData() {
        int pageNum = inInt("pageNum");

        String viewPk = in("viewPk");
        String sqlFilter = in("filter");

        SqlRowSet rsView, rsViewData;
        HashMap mapRef = (pageNum == 0 ? new HashMap() : null);
        // ------------------------------------------------
        try {
            rsView = BaseViewService.getView(jtMaster, viewPk);
            rsViewData = BaseViewService.getViewData(jtMaster, rsView, pageNum, sqlFilter, mapRef);
            ok("dtbViewData", rsViewData);

            if (pageNum == 0) {
                ok("totalRows", (int) mapRef.get("totalRows"));
            }
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
    @PostMapping("/getFormData")
    public RestResult getFormData() {
        int id = inInt("id");

        String viewPk = in("viewPk");

        SqlRowSet rsView, rsFormData;
        // ------------------------------------------------
        try {
            rsView = BaseViewService.getView(jtMaster, viewPk);
            rsFormData = BaseViewService.getFormData(jtMaster, rsView, id);
            ok("dtbFormData", rsFormData);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    @PostMapping("/save")
    public RestResult save() {
        long id = inInt("id");

        String viewPk = in("viewPk");
        String tableName;

        LinkedTreeMap form = inForm("form");
        SqlRowSet rsView, rsFormData, rsViewData;
        // ------------------------------------------------
        try {
            rsView = BaseViewService.getView(jtMaster, viewPk);
            rsView.first();
            tableName = rsView.getString("table_name");

            if (id == 0) {
                id = BaseViewService.insert(jtMaster, tableName, form);
            }
            else {
                BaseViewService.update(jtMaster, tableName, form);
            }

            // -- 返回当前行更新后的基础表数据和视图数据 --
            rsFormData = BaseViewService.getFormData(jtMaster, rsView, id);
            ok("dtbFormData", rsFormData);
            rsViewData = BaseViewService.getViewDataOne(jtMaster, rsView, id);
            ok("dtbViewData", rsViewData);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
    @PostMapping("/delete")
    public RestResult delete() {
        long id = inInt("id");
        long idNext = inInt("idNext", 0);

        String viewPk = in("viewPk");
        String tableName;

        SqlRowSet rsView, rsFormData;
        // ------------------------------------------------
        try {
            rsView = BaseViewService.getView(jtMaster, viewPk);
            rsView.first();
            tableName = rsView.getString("table_name");

            BaseViewService.delete(jtMaster, tableName, id);

            if (idNext > 0) {
                rsFormData = BaseViewService.getFormData(jtMaster, rsView, idNext);
                ok("dtbFormData", rsFormData);
            }
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}