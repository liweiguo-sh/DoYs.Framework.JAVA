/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-08-19
 * Excel工具类
 *****************************************************************************/
package com.doys.framework.util;
import com.doys.framework.core.ex.CommonException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;

public class UtilExcel {
    public static String[][] excelToArray(String fileExcel) throws Exception {
        int rowCount, columnCount;

        String extName, cellValue;
        String[][] data;

        FileInputStream fis;
        Workbook workbook;
        Sheet sheet;
        Row row, rowHeader;
        Cell cell;
        CellType cellType;
        // -- 1. open workbook ----------------------------
        fis = new FileInputStream(fileExcel);

        extName = UtilFile.getExtName(fileExcel);
        if (extName.equalsIgnoreCase("xls")) {
            workbook = new HSSFWorkbook(fis);
        }
        else if (extName.equalsIgnoreCase("xlsx")) {
            workbook = new XSSFWorkbook(fis);
        }
        else {
            throw new CommonException("非法的Excle文件后缀名 (" + extName + "), 请检查。");
        }
        // -- 2. open sheet -------------------------------
        sheet = workbook.getSheetAt(0);
        rowCount = sheet.getLastRowNum();

        rowHeader = sheet.getRow(0);
        columnCount = rowHeader.getLastCellNum();

        data = new String[rowCount + 1][columnCount];

        // -- 3. fill data --------------------------------
        for (int iRow = 0; iRow <= rowCount; iRow++) {
            row = sheet.getRow(iRow);
            for (int iCol = 0; iCol < columnCount; iCol++) {
                cell = row.getCell(iCol);
                if (cell == null) {
                    data[iRow][iCol] = "";
                    continue;
                }
                // ----------------------------------------
                cellType = cell.getCellType();
                if (cellType == CellType.NUMERIC) {
                    cellValue = String.valueOf(cell.getNumericCellValue());
                    if (cellValue.endsWith(".0")) {
                        cellValue = cellValue.substring(0, cellValue.length() - 2);
                    }
                }
                else if (cellType == CellType.STRING) {
                    cellValue = cell.getStringCellValue();
                }
                else if (cellType == CellType.BLANK) {
                    cellValue = "";
                }
                else {
                    cellValue = "???" + cellType + "???";
                }
                data[iRow][iCol] = cellValue;
            }
        }

        // -- 9. return -----------------------------------
        fis.close();
        return data;
    }
}