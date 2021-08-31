/******************************************************************************
 * Copyright (C), 2020-2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-08-19
 * @create_date 2021-02-17
 * Excel工具类
 *****************************************************************************/
package doys.framework.util;
import doys.framework.core.ex.CommonException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.text.DecimalFormat;

public class UtilExcel {
    public static String[][] excelToArray(String fileExcel) throws Exception {
        int rowMax, columnCount;

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
        rowMax = sheet.getLastRowNum();
        if (rowMax < 0) {
            return new String[0][0];
        }
        rowHeader = sheet.getRow(0);
        columnCount = rowHeader.getLastCellNum();

        data = new String[rowMax + 1][columnCount];

        // -- 3. fill data --------------------------------
        DecimalFormat df = new DecimalFormat("0");
        for (int iRow = 0; iRow <= rowMax; iRow++) {
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
                    cellValue = df.format(cell.getNumericCellValue());
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