package com.doys.aprint.base;
import com.doys.framework.core.view.BaseViewController;
import com.doys.framework.util.UtilDate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprint/base/label_view")
public class LabelView extends BaseViewController {
    @Override
    protected boolean BeforeSave(boolean addnew, long id) {
        String sql = "", strValue = "";
        String strDate = UtilDate.getDateTimeString();
        // ------------------------------------------------

        return true;
    }
    @Override
    protected boolean AfterSave(boolean addnew, long id) {
        return true;
    }
}
