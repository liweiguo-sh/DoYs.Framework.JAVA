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
    public void getResRunPath() {
        resRunPath = _mResRunPath;
    }
    // ------------------------------------------------------------------------
    /**
     * @param dbBus
     * @param multipartFile
     * @param labelId
     * @param labelType     标签类型，Maso、BarTender、NiceLabel
     * @param fileType      文件类型，label：标签模板文件，data：标签数据源模板文件
     * @return [0]：文件名称（6.bwt），[1]：磁盘文件全路径（c:\res\...\6.cvs）
     * @throws Exception
     */
    public static String[] SaveLabelFile(DBFactory dbBus, MultipartFile multipartFile, int labelId, String labelType, String fileType) throws Exception {
        String[] ret = new String[2];

        String sql;
        String filepath, filename, pathname;

        File filePath, fileLabel;
        // -- pre-processing ------------------------------
        if (!labelType.equalsIgnoreCase("BarTender")) {
            throw new Exception("unsupport label type: " + labelType);
        }
        // -- name and path -------------------------------
        filename = UtilFile.getNewName(multipartFile.getOriginalFilename(), String.valueOf(labelId));
        filepath = resRunPath + "/aprint/label_file/";
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
        if (fileType.equalsIgnoreCase("label")) {
            sql = "UPDATE base_label SET label_file_name = ? WHERE id = ?";
            dbBus.exec(sql, filename, labelId);
        }
        else {
            sql = "UPDATE base_label SET data_file_name = ? WHERE id = ?";
            dbBus.exec(sql, filename, labelId);
        }
        // ------------------------------------------------
        ret[0] = filename;
        ret[1] = pathname;

        return ret;
    }
}