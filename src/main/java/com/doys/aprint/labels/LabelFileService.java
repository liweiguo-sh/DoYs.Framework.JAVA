/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-08-08
 * 标签文件服务类
 *****************************************************************************/
package com.doys.aprint.labels;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.database.DBFactory;
import com.doys.framework.database.ds.UtilDDS;
import com.doys.framework.util.UtilFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;

@Component
public class LabelFileService extends BaseService {
    @Value("${global.resRunPath}")
    private String _mResRunPath;

    private static String resRunPath;
    @PostConstruct
    private void getResRunPath() {
        resRunPath = _mResRunPath;
    }
    // ------------------------------------------------------------------------
    public static String[] saveLabelFile(DBFactory dbBus, MultipartFile multipartFile, int labelId) throws Exception {
        String[] ret = new String[2];

        String sql;
        String filepath, filename, pathname;

        File filePath, fileLabel;
        // -- name and path -------------------------------
        filename = UtilFile.getNewName(multipartFile.getOriginalFilename(), String.valueOf(labelId));
        filepath = resRunPath + "/" + UtilDDS.getTenantId() + "/aprint/label_file/";
        filePath = new File(filepath);
        if (!filePath.exists()) {
            if (!filePath.mkdirs()) {
                throw new Exception("创建标签文件目录失败，请检查。");
            }
        }
        pathname = filepath + filename;

        // -- save label file -----------------------------
        fileLabel = new File(pathname);
        multipartFile.transferTo(fileLabel);

        // -- save filename -------------------------------
        sql = "UPDATE base_label SET label_file_name = ? WHERE id = ?";
        dbBus.exec(sql, filename, labelId);
        // ------------------------------------------------
        ret[0] = filename;
        ret[1] = pathname;

        return ret;
    }
    public static String[] saveDataFile(DBFactory dbBus, MultipartFile multipartFile, int labelId) throws Exception {
        String[] ret = new String[2];

        String sql;
        String filepath, filename, pathname;

        File filePath, fileLabel;
        // -- name and path -------------------------------
        filename = UtilFile.getNewName(multipartFile.getOriginalFilename(), String.valueOf(labelId));
        filepath = resRunPath + "/" + UtilDDS.getTenantId() + "/aprint/label_file/";
        filePath = new File(filepath);
        if (!filePath.exists()) {
            if (!filePath.mkdirs()) {
                throw new Exception("创建标签文件目录失败，请检查。");
            }
        }
        pathname = filepath + filename;

        // -- save label file -----------------------------
        fileLabel = new File(pathname);
        multipartFile.transferTo(fileLabel);

        // -- save filename -------------------------------
        sql = "UPDATE base_label SET data_file_name = ? WHERE id = ?";
        dbBus.exec(sql, filename, labelId);
        // ------------------------------------------------
        ret[0] = filename;
        ret[1] = pathname;

        return ret;
    }
}