/*************************************************
 * Copyright (C), 2012-2014, doys-next.com
 * @author Volant Lee.
 * @version 2.0
 * @date 2012-07-20
 * Sort方法支持Int、Numeric、Decimal三种数据类型升序排序, 其它的数据类型一律按照String(支持中文排序)处理 ,
 * 建议只对Int及String两种数据类型排序，Find同上.
 * 用法参考.net的DataTable, 已提供的方法基本相同, 用完后一定要调用.close()方法回收内存.
 */
/******************************************************************************
 * Copyright (C), 2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2021-12-23
 * @modify_date 2021-12-23
 * 增加判断字段是否存在
 *****************************************************************************/
package doys.framework.database.dtb;
import doys.framework.a0.Const;
import doys.framework.core.ex.CommonException;
import doys.framework.database.DBFactory;
import doys.framework.util.UtilRowSet;
import doys.framework.util.UtilString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class DataTable {
    public SqlRowSet rs = null;
    public String SQL = "";
    public boolean UniqueSort = true;
    public boolean SortByChinese = true;
    private boolean NullIsMaxValue = true;

    private boolean hasAuto = false;                // -- 是否有自增列 --
    private int _nRowCount = -1;
    private int _nRowCount0 = -1;
    private int _nColCount = -1;
    private int _nInsertCount = 0;
    private int _nUpdateCount = 0;
    private int _nDeleteCount = 0;

    private String _SortStr = "";
    private boolean _Sorted = false;

    private String[][] _arrRows = null;
    private String[][] _arrRowsDeleted = null;
    private String[][] _arrCols = null;             // --Columns Property Collection--
    private String[][] _arrSortCols = null;

    private SqlRowSetMetaData rsmd = null;
    private SimpleDateFormat sdfTime = new SimpleDateFormat(Const.datetimeFormat);
    private Logger logger = LoggerFactory.getLogger("DataTable");
    // -------------------------------------------------------------------------
    public DataTable(DBFactory dbFactory, String sql) throws Exception {
        _DataTable(dbFactory, sql, new Object[] {});
    }
    public DataTable(DBFactory dbFactory, String sql, Object... args) throws Exception {
        _DataTable(dbFactory, sql, args);
    }
    private void _DataTable(DBFactory dbFactory, String sql, Object... args) throws Exception {
        try {
            this.close();
            this.SQL = sql;

            long t1 = new Date().getTime();
            ///rs = dbFactory.openResultset(sql, parameters, SqlRowSet.TYPE_SCROLL_INSENSITIVE);
            rs = dbFactory.getRowSet(sql, args);

            long t2 = new Date().getTime();
            if (rs != null) {
                rsmd = rs.getMetaData();
                InitDataTable();
            }
            else {
                logger.info("DataTable 打开错误，未能正确执行SQL语句，请检查SQL语句是否正确以及连接是否正确。");
                throw new SQLException();
            }

            long t3 = new Date().getTime();
            if (t3 - t1 > 200) {
                logger.debug("DataTable耗时:" + (t3 - t1) + ", InitDataTable转换到数组:" + (t3 - t2) + ", sql执行耗时:" + (t2 - t1) + ".");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("初始化DataTable遇到错误. " + e.getMessage());
            throw e;
        } finally {
        }
    }
    public DataTable(String strDataTable, DBFactory dbFactory) throws Exception {
        /*
         * 反序列化, 将序列化的字符串转换为DataTable类
         */
        int nIndex = 0;

        String sql = "";
        String key = "", value = "", fieldName = "", columnType = "", fieldValue = "";
        String fieldString = "", dataString = "";

        String[] arrFields = null, arrField = null;
        String[] arrRows = null, arrRow = null;

        // -- 预处理 -----------------------------------------
        nIndex = strDataTable.indexOf(Const.CHAR7);
        if (nIndex > 0) {
            fieldString = strDataTable.substring(0, nIndex);
            dataString = strDataTable.substring(nIndex + 1);
        }
        else {
            fieldString = strDataTable;
        }

        // -- 1. 分析column属性 -------------------------------
        arrFields = fieldString.split(Const.CHAR1);
        for (int i = 0; i < arrFields.length; i++) {
            arrField = arrFields[i].split(Const.CHAR2);
            for (int j = 0; j < arrField.length; j++) {
                nIndex = arrField[j].indexOf(Const.CHAR3);
                key = arrField[j].substring(0, nIndex);
                value = arrField[j].substring(nIndex + 1);
                if (key.equalsIgnoreCase("name")) {
                    fieldName = value;
                }
                else if (key.equalsIgnoreCase("columnType")) {
                    columnType = value;
                    if (columnType.equalsIgnoreCase("number")) {
                        fieldValue = "0";
                    }
                    else {
                        fieldValue = "''";
                    }
                }
            }
            sql += ", " + fieldValue + " AS " + fieldName;
        }
        sql = "SELECT " + sql.substring(1) + " WHERE 1 = 0";

        // -- 2. 模拟类初始化 -----------------------------------
        _DataTable(dbFactory, sql, new Object[] {});

        // -- 3. 读入记录集数据 ----------------------------------
        if (dataString.length() > 0) {
            arrRows = dataString.split(Const.CHAR1);
            this._nRowCount = arrRows.length;
            this._arrRows = new String[this._nRowCount][1 + this._nColCount];
            for (int i = 0; i < this._nRowCount; i++) {
                arrRow = arrRows[i].split(Const.CHAR2);
                for (int j = 0; j <= this._nColCount; j++) {
                    this._arrRows[i][j] = arrRow[j];
                }
                if (arrRow[0].equals("N"))
                    this._nInsertCount++;
                if (arrRow[0].equals("U"))
                    this._nUpdateCount++;
            }
        }
    }
    public void close() {
        _arrRows = null;
        _arrRowsDeleted = null;
        _arrCols = null;
        _arrSortCols = null;
    }

    private void InitDataTable() {
        int nRow = 0;
        Date dtValue = null;
        try {
            _nRowCount = getRowCount();
            _nRowCount0 = _nRowCount;
            _nColCount = getColCount();
            _arrRows = new String[_nRowCount][_nColCount + 1 + 1];
            _arrCols = new String[_nColCount + 1][3];
            // --初始化列集合--------------------------------------------------------
            for (int iCol = 1; iCol <= _nColCount; iCol++) {
                if (!this.hasAuto && rsmd.getColumnLabel(iCol).equalsIgnoreCase("id")) {
                    this.hasAuto = true;
                }

                _arrCols[iCol][1] = Integer.toString(rsmd.getColumnType(iCol));
                _arrCols[iCol][2] = rsmd.getColumnTypeName(iCol);
                if (_arrCols[iCol][2].equalsIgnoreCase("int") || _arrCols[iCol][2].equalsIgnoreCase("INTEGER")
                    || _arrCols[iCol][2].equalsIgnoreCase("bigint") || _arrCols[iCol][2].equalsIgnoreCase("tinyint") || _arrCols[iCol][2].equalsIgnoreCase("smallint")
                    || _arrCols[iCol][2].equalsIgnoreCase("numeric") || _arrCols[iCol][2].equalsIgnoreCase("decimal") || _arrCols[iCol][2].equalsIgnoreCase("NUMBER")) {
                    _arrCols[iCol][0] = "number";
                }
                else if (_arrCols[iCol][2].equalsIgnoreCase("varchar") || _arrCols[iCol][2].equalsIgnoreCase("nvarchar")
                    || _arrCols[iCol][2].equalsIgnoreCase("char") || _arrCols[iCol][2].equalsIgnoreCase("nchar") || _arrCols[iCol][2].equalsIgnoreCase("VARCHAR2")
                    || _arrCols[iCol][2].equalsIgnoreCase("NVARCHAR2") || _arrCols[iCol][2].equalsIgnoreCase("text")
                    || _arrCols[iCol][2].equalsIgnoreCase("ntext")) {
                    _arrCols[iCol][0] = "string";
                }
                else if (_arrCols[iCol][2].equalsIgnoreCase("datetime") || _arrCols[iCol][2].equalsIgnoreCase("TIMESTAMP")
                    || _arrCols[iCol][2].equalsIgnoreCase("DATE") || _arrCols[iCol][2].equalsIgnoreCase("TIME")) {
                    _arrCols[iCol][0] = "datetime";
                }
                else if (_arrCols[iCol][2].equalsIgnoreCase("bit")) {
                    _arrCols[iCol][0] = "bit";
                }
                else {
                    System.err.println("DataTable.InitDataTable, " + _arrCols[iCol][2]);
                    _arrCols[iCol][0] = "";
                }
            }
            // --将数据转换到数组中-----------------------------------------------------
            rs.beforeFirst();
            while (rs.next()) {
                _arrRows[nRow][0] = "";
                for (int iCol = 1; iCol <= _nColCount; iCol++) {
                    if (_arrCols[iCol][0].compareTo("datetime") == 0) {
                        dtValue = rs.getTimestamp(iCol);
                        if (dtValue != null) {
                            _arrRows[nRow][iCol] = sdfTime.format(dtValue);
                        }
                    }
                    if (_arrCols[iCol][0].compareTo("number") == 0) {
                        if (_arrCols[iCol][2].equalsIgnoreCase("numeric") || _arrCols[iCol][2].equalsIgnoreCase("decimal")) {
                            _arrRows[nRow][iCol] = String.valueOf(rs.getBigDecimal(iCol));
                        }
                        else {
                            _arrRows[nRow][iCol] = rs.getString(iCol);
                        }
                    }
                    else {
                        _arrRows[nRow][iCol] = rs.getString(iCol);
                    }
                }
                nRow++;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public String[][] Data() {
        return _arrRows;
    }

    private String _DataCell(int iRow, int iCol, boolean fixNullSpace) {
        String sReturn = _arrRows[iRow][iCol];
        if (fixNullSpace) {
            sReturn = UtilString.trimSpace(sReturn);
        }
        return sReturn;
    }
    public String DataCell(int iRow, int iCol) {
        return _DataCell(iRow, iCol, false);
    }
    public String DataCell(int iRow, int iCol, boolean fixNullSpace) {
        return _DataCell(iRow, iCol, fixNullSpace);
    }
    public String DataCell(int iRow, String ColumnName) throws Exception {
        int iCol = getColIndex(ColumnName);
        return _DataCell(iRow, iCol, false);
    }
    public String DataCell(int iRow, String ColumnName, boolean fixNullSpace) throws Exception {
        int iCol = getColIndex(ColumnName);
        return _DataCell(iRow, iCol, fixNullSpace);
    }
    public String getRowTag(int iRow) {
        if (_arrRows[iRow][_nColCount + 1] == null) {
            return "";
        }
        return _arrRows[iRow][_nColCount + 1];
    }

    public void setDataCell(int iRow, int iCol, Object objValue) {
        _arrRows[iRow][iCol] = (objValue == null ? null : objValue.toString());
        if (_arrRows[iRow][0].compareTo("N") != 0) {
            if (_arrRows[iRow][0].compareTo("U") != 0) {
                _arrRows[iRow][0] = "U";
                _nUpdateCount++;
            }
        }
    }
    public void setDataCell(int iRow, String ColumnName, Object objValue) throws Exception {
        int iCol = getColIndex(ColumnName);
        setDataCell(iRow, iCol, objValue);
    }
    public void setRowTag(int iRow, String strValue) {
        _arrRows[iRow][_nColCount + 1] = strValue;
    }

    public DataRow Row(int iRow) {
        DataRow dr = new DataRow();
        dr._arrRow = _arrRows[iRow];
        dr._DataRowIndex = iRow;
        return dr;
    }
    public DataColumn Column(int iCol) {
        DataColumn dc = new DataColumn(iCol);
        return dc;
    }
    public DataColumn Column(String ColumnName) {
        DataColumn dc = new DataColumn(ColumnName);
        return dc;
    }

    /**
     * 返回记录集记录条数
     */
    public int getRowCount() {
        if (_nRowCount == -1) {
            try {
                if (rs != null) {
                    rs.last();
                    _nRowCount = rs.getRow();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return _nRowCount;
    }
    public int getColCount() {
        if (_nColCount == -1) {
            try {
                _nColCount = rsmd.getColumnCount();
            } catch (Exception e) {
                throw e;
            }
        }
        return _nColCount;
    }
    public int getColIndex(String ColumnName) throws Exception {
        int nColIndex;
        try {
            nColIndex = rs.findColumn(ColumnName);
        } catch (InvalidResultSetAccessException e) {
            throw new CommonException("列名：" + ColumnName + " 无效。");
        }
        return nColIndex;
    }
    public String getSortStr() {
        return _SortStr;
    }

    public void Sort(String sSortCols) throws Exception {
        _Sort(sSortCols, false);
    }
    public void Sort(String sSortCols, boolean bPreSorted) throws Exception {
        _Sort(sSortCols, bPreSorted);
    }
    private void _Sort(String sSortCols, boolean bPreSorted) throws Exception {
        int nColIndex;
        String colTypeName;
        String[] arrSort;
        // ---------------------------------------------------------------------
        sSortCols = sSortCols.replaceAll(" ", "");
        if (sSortCols.equals("")) {
            logger.error("未设置排序字段, 不能执行Find方法.");
            return;
        }
        _SortStr = sSortCols;
        arrSort = _SortStr.split(",");
        _arrSortCols = new String[arrSort.length][3];
        // ---------------------------------------------------------------------
        for (int i = 0; i < arrSort.length; i++) {
            _arrSortCols[i][2] = arrSort[i];
            nColIndex = rs.findColumn(arrSort[i]);
            _arrSortCols[i][0] = Integer.toString(nColIndex);
            colTypeName = rsmd.getColumnTypeName(nColIndex);
            if (colTypeName.compareToIgnoreCase("int") == 0 || colTypeName.equalsIgnoreCase("LONG")) {
                _arrSortCols[i][1] = "Int";
            }
            else if (colTypeName.equalsIgnoreCase("numeric") || colTypeName.equalsIgnoreCase("decimal") || colTypeName.equalsIgnoreCase("NUMBER")
                || colTypeName.equalsIgnoreCase("FLOAT") || colTypeName.equalsIgnoreCase("money") || colTypeName.equalsIgnoreCase("real")) {
                _arrSortCols[i][1] = "Numeric";
            }
            else if (colTypeName.equalsIgnoreCase("VARCHAR") || colTypeName.equalsIgnoreCase("VARCHAR2") || colTypeName.equalsIgnoreCase("NVARCHAR")
                || colTypeName.equalsIgnoreCase("NVARCHAR2") || colTypeName.equalsIgnoreCase("CHAR") || colTypeName.equalsIgnoreCase("NCHAR")) {
                _arrSortCols[i][1] = "String";
            }
            else if (colTypeName.equalsIgnoreCase("datetime")) {
                _arrSortCols[i][1] = "DateTime";
            }
            else {
                _arrSortCols[i][1] = "String";
                logger.error("未知的数据类型【" + colTypeName + "】，请检查。");
            }
        }
        // ---------------------------------------------------------------------
        if (!bPreSorted) {
            Arrays.sort(_arrRows, new MultiArraySort(_arrSortCols, SortByChinese, NullIsMaxValue));
        }
        _Sorted = true;
    }

    public int Find(String[] arrFind) throws Exception {
        int nReturn, iRow;
        boolean blBreak = false;
        // --------------------------------------------------------------------
        if (_Sorted == false) {
            this._Sort(_SortStr, false);
        }
        // --------------------------------------------------------------------
        nReturn = Arrays.binarySearch(_arrRows, arrFind, new MultiArrayFind(_arrSortCols, SortByChinese, NullIsMaxValue));
        // --------------------------------------------------------------------
        if (nReturn >= 0) {
            if (!UniqueSort) {
                for (iRow = nReturn - 1; iRow >= 0; iRow--) {
                    for (int j = 0; j < _arrSortCols.length; j++) {
                        int colIndex = Integer.parseInt(_arrSortCols[j][0]);
                        String aValue = _arrRows[iRow][colIndex];
                        String bValue = arrFind[j];
                        if (aValue == null && bValue == null) {
                            continue;
                        }
                        if ((aValue == null && bValue != null) || (aValue != null && bValue == null) || (!aValue.equalsIgnoreCase(bValue))) {
                            blBreak = true;
                            break;
                        }
                    }
                    if (blBreak) {
                        break;
                    }
                }
                nReturn = iRow + 1;
            }
        }
        // --------------------------------------------------------------------
        return nReturn;
    }

    /**
     * 返回Column数据类型(简化版)
     */
    public String getColumnType(String ColumnName) throws Exception {
        return _getColumnType(getColIndex(ColumnName));
    }
    /**
     * 返回Column数据类型(简化版)
     */
    public String getColumnType(int iCol) {
        return _getColumnType(iCol);
    }
    private String _getColumnType(int iCol) {
        return _arrCols[iCol][0];
    }

    public boolean containsColumn(String columnName) {
        for (int i = rsmd.getColumnCount(); i > 0; i--) {
            if (rsmd.getColumnLabel(i).equalsIgnoreCase(columnName)) {
                return true;
            }
        }
        return false;
    }

    // --DataRow insert, update , delete and commit save-----------------------
    public DataRow NewRow() {
        if (_nColCount <= 0) {
            return null;
        }
        DataRow drNew = new DataRow();
        return drNew;
    }
    public void AddRow(DataRow row) throws Exception {
        int nFind = 0, nInsertPos = _nRowCount;
        // ------------------------------------------------
        if (_Sorted) { // -- 如果已排序, 需要先查找新纪录的正确插入位置 --
            String[] arrFind = _SortStr.split(",");
            for (int i = 0; i < arrFind.length; i++) {
                arrFind[i] = row.DataCell(arrFind[i]);
            }
            nFind = this.Find(arrFind);
            if (nFind >= 0) {
                nInsertPos = nFind;
            }
            else {
                nInsertPos = -nFind - 1;
            }
        }
        // -- 动态扩充数组_arrRows ------------------------------
        int oldCapacity = _arrRows.length;
        if (_nRowCount >= oldCapacity || nInsertPos < _nRowCount) { // -- 容量不足或插入当中位置 --
            int newCapacity = oldCapacity;
            String[][] arrOld = _arrRows;
            if (_Sorted) {
                newCapacity++;
            }
            else {// -- 非排序情况下, 新纪录插入尾部, 扩容时适当留有余量 --
                newCapacity = (oldCapacity * 3) / 2 + 10;
            }
            _arrRows = new String[newCapacity][_nColCount + 1 + 1];

            if (nInsertPos > 0) {
                System.arraycopy(arrOld, 0, _arrRows, 0, nInsertPos);
            }
            if (nInsertPos < _nRowCount) {
                System.arraycopy(arrOld, nInsertPos, _arrRows, nInsertPos + 1, _nRowCount - nInsertPos);
            }
            arrOld = null;
        }
        // -- 将新记录添加到数组_arrRows中 --------------------------
        for (int iCol = 1; iCol <= _nColCount + 1; iCol++) {
            _arrRows[nInsertPos][iCol] = row.DataCell(iCol);
        }
        _arrRows[nInsertPos][0] = "N";
        row._arrRow = _arrRows[nInsertPos];
        // --------------------------------------------------------------------
        _nInsertCount++;
        _nRowCount++;
    }
    public void RemoveAt(int rowIndex) {
        if (_nRowCount - 1 < rowIndex || rowIndex < 0) {
            _arrRows[rowIndex][0] = "";
            try {
                throw new CommonException("下标越界.");
            } catch (Exception e) {
                return;
            }
        }
        // --记录删除的数据行--------------------------------------
        if (_arrRows[rowIndex][0].compareTo("N") != 0) {
            if (_nDeleteCount == 0) {
                _arrRowsDeleted = new String[_nRowCount0 / 3 + 1][_nColCount + 1];
            }
            else if (_nDeleteCount == _arrRowsDeleted.length) {
                String[][] newArrRows = new String[_nRowCount0][_nColCount + 1];
                System.arraycopy(_arrRowsDeleted, 0, newArrRows, 0, _nDeleteCount);
                _arrRowsDeleted = newArrRows;
            }
            else if (_nDeleteCount > _arrRowsDeleted.length) {
                int i = 1 / 1 - 1;
                logger.error("error:" + i);
            }
            // -- System.arraycopy(_arrRows, rowIndex, _arrRowsDeleted, _nDeleteCount, 1);
            for (int iCol = 0; iCol <= _nColCount; iCol++) {
                _arrRowsDeleted[_nDeleteCount][iCol] = _arrRows[rowIndex][iCol];
            }
            _nDeleteCount++;
        }
        // ------------------------------------------------
        if (_nRowCount - 1 == rowIndex) {// --移除行恰好是最后一行
            for (int iCol = 0; iCol <= _nColCount + 1; iCol++) {
                _arrRows[rowIndex][iCol] = null;
            }
        }
        else {
            //int newCapacity = _arrRows.length - 1         // -- 旧 --
            int newCapacity = _nRowCount - 1;               // -- 新 --
            String[][] arrOld = _arrRows;
            _arrRows = new String[newCapacity][_nColCount + 1 + 1];
            if (rowIndex > 0) {
                System.arraycopy(arrOld, 0, _arrRows, 0, rowIndex);
            }
            System.arraycopy(arrOld, rowIndex + 1, _arrRows, rowIndex, _nRowCount - rowIndex - 1);
            arrOld = null;
        }
        // --------------------------------------------------------------------
        _nRowCount--;
    }
    public int Update(DBFactory dbFactory, String tableName, String strPKeyCols) throws Exception {
        // -- 主键顺序须与查询顺序一致，即strPKeyCols中的字段顺序要与SELECT中的字段顺序一致，否则保存出错 --
        String sqlInsertCmd = "", sqlUpdateCmd = "", sqlDeleteCmd = "";
        String strFields = "", strValues = "", strWherePKey = "";

        int[] arrPKeyIndex = null;
        int[] arrResult = null;
        int nResult = 0, nIdx = 0;

        ArrayList<Object[]> listInsert = null, listUpdate = null, listDelete = null;
        Object[] paraInsert, paraUpdate, paraDelete;
        // --1、生成预处理SQL语句------------------------------------------------------
        strPKeyCols = strPKeyCols.replaceAll(" ", "");
        for (int iCol = 1; iCol <= _nColCount; iCol++) {
            DataColumn dc = Column(iCol);
            if (!dc.isAutoIncrement) {
                strFields += "," + dc.columnName;
                strValues += ",?";
                sqlUpdateCmd += "," + dc.columnName + " = ?";
            }
            if ((strPKeyCols.toUpperCase() + ",").indexOf(dc.columnName.toUpperCase()) >= 0) {
                strWherePKey += " AND " + dc.columnName + " = ?";
            }
        }
        strFields = strFields.substring(1);
        strValues = strValues.substring(1);
        sqlUpdateCmd = sqlUpdateCmd.substring(1);
        if (strWherePKey.length() > 5) {
            strWherePKey = strWherePKey.substring(5);
        }

        sqlInsertCmd = "INSERT INTO " + tableName + "(" + strFields + ") VALUES (" + strValues + ")";
        sqlUpdateCmd = "UPDATE " + tableName + " SET " + sqlUpdateCmd + " WHERE " + strWherePKey;
        sqlDeleteCmd = "DELETE FROM " + tableName + " WHERE " + strWherePKey;

        String[] arrPKey = strPKeyCols.split(",");
        arrPKeyIndex = new int[arrPKey.length];
        for (int i = 0; i < arrPKey.length; i++) {
            arrPKeyIndex[i] = getColIndex(arrPKey[i]);
        }
        // --2、填充预处理SQL参数------------------------------------------------------
        // --A、Create prepareStatement---------------------
        if (_nInsertCount > 0) {
            listInsert = new ArrayList<>();
        }
        if (_nUpdateCount > 0) {
            listUpdate = new ArrayList<>();
        }
        if (_nDeleteCount > 0) {
            listDelete = new ArrayList<>();
        }
        // --B、Insert and Update---------------------------
        for (int iRow = 0; iRow < _nRowCount; iRow++) {
            String strNUD = _arrRows[iRow][0];
            if (strNUD == null || strNUD == "") {
                continue;
            }
            if (strNUD.compareTo("N") == 0) {
                nIdx = 0;
                paraInsert = new Object[_nColCount - (this.hasAuto ? 1 : 0)];
                for (int iCol = 1; iCol <= _nColCount; iCol++) {
                    DataColumn dc = Column(iCol);
                    if (!dc.isAutoIncrement) {
                        if (null == _arrRows[iRow][iCol]) {
                            paraInsert[nIdx++] = null;
                        }
                        else {
                            if (dc.columnType.equalsIgnoreCase("datetime")) {
                                paraInsert[nIdx++] = getTimestamp(_arrRows[iRow][iCol]);
                            }
                            else {
                                paraInsert[nIdx++] = _arrRows[iRow][iCol];
                            }
                        }
                    }
                }
                listInsert.add(paraInsert);
            }
            else if (strNUD.compareTo("U") == 0) {
                nIdx = 0;
                paraUpdate = new Object[_nColCount + arrPKeyIndex.length - (this.hasAuto ? 1 : 0)];
                for (int iCol = 1; iCol <= _nColCount; iCol++) {
                    DataColumn dc = Column(iCol);
                    if (!dc.isAutoIncrement) {
                        if (null == _arrRows[iRow][iCol]) {
                            paraUpdate[nIdx++] = null;
                        }
                        else {
                            if (dc.columnType.equalsIgnoreCase("datetime")) {
                                paraUpdate[nIdx++] = getTimestamp(_arrRows[iRow][iCol]);
                            }
                            else {
                                paraUpdate[nIdx++] = _arrRows[iRow][iCol];
                            }
                        }
                    }
                }
                for (int iCol = 0; iCol < arrPKeyIndex.length; iCol++) {
                    int colIndex = arrPKeyIndex[iCol];
                    paraUpdate[nIdx++] = _arrRows[iRow][colIndex];
                }
                listUpdate.add(paraUpdate);
            }
        }
        // --C、Delete--------------------------------------
        for (int iRow = 0; iRow < _nDeleteCount; iRow++) {
            nIdx = 0;
            paraDelete = new Object[arrPKeyIndex.length];
            for (int iCol = 0; iCol < arrPKeyIndex.length; iCol++) {
                int colIndex = arrPKeyIndex[iCol];
                paraDelete[nIdx++] = _arrRowsDeleted[iRow][colIndex];
            }
            listDelete.add(paraDelete);
        }
        // --D、提交更新------------------------------------
        if (_nDeleteCount > 0) {
            arrResult = dbFactory.batchUpdate(sqlDeleteCmd, listDelete);
            nResult += arrResult.length;
        }
        if (_nUpdateCount > 0) {
            arrResult = dbFactory.batchUpdate(sqlUpdateCmd, listUpdate);
            nResult += arrResult.length;
        }
        if (_nInsertCount > 0) {
            arrResult = dbFactory.batchUpdate(sqlInsertCmd, listInsert);
            nResult += arrResult.length;
        }

        nResult = _nInsertCount + _nUpdateCount + _nDeleteCount;
        _nInsertCount = 0;
        _nUpdateCount = 0;
        _nDeleteCount = 0;

        // --3、catch, finally and return ------------------
        return nResult;
    }

    // -- inner class area ----------------------------------------------------


    /**
     * DataRow
     */
    public class DataRow {
        private int _DataRowIndex = -1;
        private String[] _arrRow = null;
        public DataRow() {
            _arrRow = new String[getColCount() + 1 + 1];
        }

        public void setDataCell(int iCol, Object objValue) {
            if (_DataRowIndex == -1) { // --新加行--
                _arrRow[iCol] = (objValue == null ? null : objValue.toString());
            }
            else {// --修改行--
                DataTable.this.setDataCell(_DataRowIndex, iCol, objValue);
            }
        }
        public void setDataCell(String ColumnName, Object objValue) throws Exception {
            int iCol = getColIndex(ColumnName);
            setDataCell(iCol, objValue);
        }
        public void setRowTag(String strValue) {
            _arrRow[_nColCount + 1] = strValue;
        }

        private String _DataCell(int iCol, boolean fixNullSpace) {
            String sReturn = _arrRow[iCol];
            if (fixNullSpace) {
                sReturn = UtilString.trimSpace(sReturn);
            }
            return sReturn;
        }
        public String DataCell(int iCol) {
            return _arrRow[iCol];
        }
        public String DataCell(String ColumnName) throws Exception {
            int iCol = getColIndex(ColumnName);
            return _DataCell(iCol, false);
        }
        public String DataCell(String ColumnName, boolean fixNullSpace) throws Exception {
            int iCol = getColIndex(ColumnName);
            return _DataCell(iCol, fixNullSpace);
        }
        public String getRowTag() {
            return _arrRow[_nColCount + 1];
        }
    }


    /**
     * DataColumn
     */
    public class DataColumn {
        public int DataTypeID = 0;

        public String tableName = "";
        public String columnName = "";
        public String dataType = "";        // -- 原始数据类型 --
        public String columnType = "";        // -- 简化数据类型--

        public boolean isAutoIncrement = false;
        // ---------------------------------------------------------------------
        public DataColumn(int iCol) {
            _DataColumn(iCol);
        }
        public DataColumn(String ColName) {
            try {
                int iCol = getColIndex(ColName);
                _DataColumn(iCol);
            } catch (Exception e) {
            }
        }
        private void _DataColumn(int iCol) {
            try {
                this.tableName = rsmd.getTableName(iCol);
                this.columnName = rsmd.getColumnLabel(iCol);
                this.DataTypeID = rsmd.getColumnType(iCol);
                this.dataType = rsmd.getColumnTypeName(iCol);
                this.columnType = UtilRowSet.getFieldType(this.dataType);

                ///if (rsmd.isAutoIncrement(iCol)) {
                if (this.columnName.equalsIgnoreCase("id")) {
                    // TODO: 暂时这样判断
                    isAutoIncrement = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // --------------------------------------------------------------------
    }

    // -- JSON ----------------------------------------------------------------
    public String toJSON() {
        return toJSON(true);
    }
    /**
     * DataTable转JSON格式字符串
     *
     * @param blArray 默认返回数组格式, 仅针对记录数 = 1时有效
     */
    public String toJSON(boolean blArray) {
        int nRowCount = this.getRowCount();
        String columnType = "", columnName = "";

        StringBuilder sbJSON = new StringBuilder();
        // --------------------------------------------
        if (nRowCount == 0) {
            return "";
        }
        else if (nRowCount > 1 || blArray) {
            sbJSON.append("[");
        }
        // --------------------------------------------
        for (int i = 0; i < nRowCount; i++) {
            sbJSON.append((i > 0 ? ", " : "") + "{");
            for (int j = 1; j <= this.getColCount(); j++) {
                columnType = this.Column(j).columnType;
                columnName = this.Column(j).columnName;
                sbJSON.append((j > 1 ? "," : "") + "\"" + columnName + "\":");
                if (this.DataCell(i, j) == null) {
                    sbJSON.append("null");
                }
                else {
                    if (columnType.equals("number")) {
                        sbJSON.append(this.DataCell(i, j));
                    }
                    else {
                        sbJSON.append("\"" + this.DataCell(i, j) + "\"");
                    }
                }
            }
            sbJSON.append("}");
        }
        // --------------------------------------------
        if (nRowCount > 1 || blArray) {
            sbJSON.append("]");
        }
        return sbJSON.toString();
    }

    /// -- temp --
    public java.sql.Timestamp getTimestamp(String strDateTime) {
        Date date = null;
        java.sql.Timestamp timeStamp = null;
        try {
            if (strDateTime == null || strDateTime == "") {
                return null;
            }
            else if (strDateTime.length() == 10) {
                date = new SimpleDateFormat(Const.dateFormat).parse(strDateTime);
            }
            else if (strDateTime.length() == 19) {
                date = new SimpleDateFormat(Const.datetimeFormat).parse(strDateTime);
            }
            else if (strDateTime.length() > 19) {
                date = new SimpleDateFormat(Const.longDtFormat).parse(strDateTime);
            }
            else if (strDateTime.length() >= 13 && strDateTime.length() <= 16) {
                date = new SimpleDateFormat(Const.datetimeFormat.substring(0, strDateTime.length())).parse(strDateTime);
            }
            else {
                return null;
            }

            timeStamp = new java.sql.Timestamp(date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return timeStamp;
    }
}