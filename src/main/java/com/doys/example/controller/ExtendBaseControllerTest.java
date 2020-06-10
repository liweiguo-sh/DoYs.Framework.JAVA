package com.doys.example.controller;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import com.doys.framework.util.UtilDate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

@RestController
@RequestMapping("/temp")
public class ExtendBaseControllerTest extends BaseController {
    private static ThreadLocal<ArrayList<String>> listLocal = new ThreadLocal<>();
    //private int nCount = 0;

    @RequestMapping("/abc")
    private RestResult abc(HttpSession ss) throws InterruptedException {
        Thread.sleep(200);
        String strNow = UtilDate.getDateTimeString();

        int i = 0;
        err("错误-" + i++);
        err("错误-" + i++);

        return ResultErr();
    }
}