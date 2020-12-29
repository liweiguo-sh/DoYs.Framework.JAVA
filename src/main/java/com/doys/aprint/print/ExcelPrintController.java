package com.doys.aprint.print;
import com.doys.aprint.task.TaskService;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import com.doys.framework.database.DBFactory;
import com.doys.framework.util.UtilExcel;
import com.doys.framework.util.UtilString;
import com.doys.framework.util.UtilUploadTemp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/aprint/print/ExcelPrint")
public class ExcelPrintController extends BaseController {
    @Autowired
    DataSourceTransactionManager dstm;
    @Autowired
    TransactionDefinition tDef;
    // ------------------------------------------------------------------------
    @RequestMapping(value = "/uploadExcel")
    private RestResult uploadExcel(@RequestParam("file") MultipartFile multipartFile, @RequestParam("labelId") int labelId) {
        int taskId, qty;

        String sql;
        String fileExcel;
        String userPk = this.ssValue("userPk");

        String[][] data, dataPreview;

        SqlRowSet rsTask;
        TransactionStatus tStatus = null;
        // ------------------------------------------------
        try {
            dstm.setDataSource(dbBus.getDataSource());
            tStatus = dstm.getTransaction(tDef);

            fileExcel = UtilUploadTemp.saveSingleFile(multipartFile);
            data = UtilExcel.excelToArray(fileExcel);
            qty = data.length - 1;
            if (qty <= 0) {
                return ResultErr("导入文件没有数据，请检查。");
            }
            taskId = TaskService.createTask(dbBus, labelId, userPk);
            TaskService.importExcelData(dbBus, labelId, taskId, data);
            dstm.commit(tStatus);

            sql = "SELECT * FROM core_task WHERE id = ?";
            rsTask = dbBus.getRowSet(sql, taskId);
            ok("dtbTask", rsTask);
            ok("qty", qty);

            // -- 返回excel预览数据 --
            dataPreview = new String[3][];
            dataPreview[0] = data[0];       // -- 表头 --
            dataPreview[1] = data[1];       // -- 首行数据 --
            dataPreview[2] = data[qty];     // -- 末行数据 --
            ok("dataPreview", dataPreview);
            ok("uploadFields", UtilString.arrayJoin(data[0], ","));     // -- 导入文件字段集合 --
        } catch (Exception e) {
            DBFactory.rollback(dstm, tStatus);
            return ResultErr(e);
        }
        return ResultOk();
    }
}