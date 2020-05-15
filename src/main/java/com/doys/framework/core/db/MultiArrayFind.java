package com.doys.framework.core.db;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.Comparator;
import java.util.Locale;

public class MultiArrayFind implements Comparator<Object> {
    private String[][] _arrCols;
    private boolean _SortByChinese = true;
    private boolean _NullIsMaxValue = false;
    private RuleBasedCollator _collator;

    public MultiArrayFind(String[][] arrCols) {
        this._arrCols = arrCols;
    }
    public MultiArrayFind(String[][] arrCols, boolean SortByChinese) {
        this._arrCols = arrCols;
        _collator = (RuleBasedCollator) Collator.getInstance(Locale.CHINA);
        _SortByChinese = SortByChinese;
    }
    public MultiArrayFind(String[][] arrCols, boolean SortByChinese, boolean NullIsMaxValue) {
        this._arrCols = arrCols;
        _collator = (RuleBasedCollator) Collator.getInstance(Locale.CHINA);
        _SortByChinese = SortByChinese;
        _NullIsMaxValue = NullIsMaxValue;
    }

    public int compare(Object o1, Object o2) {
        int nReturn = 0;
        for (int i = 0; i < _arrCols.length; i++) {
            int colIndex = Integer.parseInt(_arrCols[i][0]);
            String DataType = _arrCols[i][1];
            String aValue = ((String[]) o1)[colIndex];
            String bValue = ((String[]) o2)[i];
            // --null值特殊处理-----------------------------------------------------
            if (aValue == null && bValue == null) {
                continue;
            }
            else if (aValue == null) {
                return _NullIsMaxValue ? 1 : -1;
            }
            else if (bValue == null) {
                return _NullIsMaxValue ? -1 : 1;
            }
            // ----------------------------------------------------------------
            if (DataType.compareToIgnoreCase("String") == 0) {
                if (_SortByChinese) {
                    // -- 忽略大小写 --
                    nReturn = _collator.compare(aValue.toLowerCase(), bValue.toLowerCase());
                }
                else {
                    nReturn = aValue.compareTo(bValue);
                }
            }
            else if (DataType.compareToIgnoreCase("Int") == 0) {
                nReturn = Integer.parseInt(aValue) - Integer.parseInt(bValue);
            }
            else if (DataType.compareToIgnoreCase("Numeric") == 0) {
                if (Double.parseDouble(aValue) - Double.parseDouble(bValue) > 0) {
                    nReturn = 1;
                }
                else if (Double.parseDouble(aValue) - Double.parseDouble(bValue) < 0) {
                    nReturn = -1;
                }
                else {
                    nReturn = 0;
                }
            }
            else if (DataType.compareToIgnoreCase("DateTime") == 0) {
                nReturn = aValue.substring(0, 10).compareTo(bValue.substring(0, 10));
            }
            if (nReturn != 0) {
                break;
            }
        }
        // --------------------------------------------------------------------
        return nReturn;
    }
}