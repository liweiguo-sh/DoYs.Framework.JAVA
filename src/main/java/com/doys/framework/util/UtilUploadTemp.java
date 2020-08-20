/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-08-18
 * 临时文件上传类
 *****************************************************************************/
package com.doys.framework.util;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class UtilUploadTemp {
    /**
     * 保存上传文件（单一的、临时的）
     *
     * @param multipartFile
     * @return 返回文件磁盘路径
     */
    public static String saveSingleFile(MultipartFile multipartFile) throws Exception {
        String[] ret = _saveSingleFile(multipartFile);

        return ret[2];
    }
    private static String[] _saveSingleFile(MultipartFile multipartFile) throws Exception {
        String[] ret = new String[3];

        String filepath, filename, pathname;

        File filePath, fileSave;
        // -- path and name -------------------------------
        filepath = UtilYml.getTempPath() + "/aprint/";
        filename = UtilFile.getNewName(multipartFile.getOriginalFilename(), UtilFile.getFileSN());

        filePath = new File(filepath);
        if (!filePath.exists()) {
            if (!filePath.mkdirs()) {
                throw new Exception("创建文件目录失败，请检查。");
            }
        }
        pathname = filepath + filename;

        // -- save upload file ----------------------------
        fileSave = new File(pathname);
        multipartFile.transferTo(fileSave);

        // ------------------------------------------------
        ret[0] = filepath;
        ret[1] = filename;
        ret[2] = pathname;
        return ret;
    }
}