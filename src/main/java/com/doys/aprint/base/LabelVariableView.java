package com.doys.aprint.base;
import com.doys.aprint.labels.LabelTableService;
import com.doys.framework.core.ex.UnexpectedException;
import com.doys.framework.core.view.BaseViewController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprint/base/label_variable_view")
public class LabelVariableView extends BaseViewController {
    @Override
    protected boolean BeforeSave(boolean addnew, long id) throws Exception {
        int result;
        int labelId = inInt("label_id");

        String sql;
        String name = in("name");
        // ------------------------------------------------

        // ------------------------------------------------
        return true;
    }
    @Override
    protected boolean AfterSave(boolean addnew, long id) throws Exception {
        int labelId = inInt("label_id");

        // -- 同步标签变量定义 to 标签数据表表结构 ------------------------
        LabelTableService.labelVariableToLabelColumn(dbBus, labelId);

        // ------------------------------------------------
        return true;
    }

    @Override
    protected boolean BeforeDelete(long id) throws Exception {
        throw new UnexpectedException();
    }
}