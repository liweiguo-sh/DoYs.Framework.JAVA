package com.doys.aprint.base;
import com.doys.framework.core.view.BaseViewController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprint/base/category_view")
public class CategoryView extends BaseViewController {
    @Override
    protected boolean AfterSave(boolean addnew, long id) throws Exception {


        return true;
    }
    @Override
    protected boolean AfterDelete(long id) throws Exception {


        return true;
    }
}