package com.doys.aprint.base;
import com.doys.framework.core.view.BaseViewController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprint/base/label_variable_view")
public class LabelVariableView extends BaseViewController {

    @Override
    protected boolean AfterSave(boolean addnew, long id) throws Exception {
        int labelId = inInt("label_id");

        LabelService.generateLabelXTable(dbSys, labelId);
        return true;
    }
}