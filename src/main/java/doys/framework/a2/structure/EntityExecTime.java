/******************************************************************************
 * Copyright (C), 2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2021-09-07
 * 执行过程记录类，用于测试过程执行时间
 *****************************************************************************/
package doys.framework.a2.structure;
import doys.framework.util.UtilString;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;

public class EntityExecTime {
    private LocalTime tStart, tLast;
    private ArrayList<String[]> list;
    private DecimalFormat format = new DecimalFormat("#.#");
    // ------------------------------------------------------------------------
    public EntityExecTime() {
        list = new ArrayList<>();

        restart();
    }

    public void logProcess(String processName) {
        LocalTime tNow = LocalTime.now();

        float durationLast = Duration.between(tLast, tNow).toMillis();
        float durationStart = Duration.between(tStart, tNow).toMillis();

        String unit;
        String[] arrLog = new String[] { processName, "", "" };

        unit = "毫秒";
        if (durationLast >= 1000) {
            durationLast /= 1000f;
            unit = "秒";
            if (durationLast >= 1000) {
                durationLast /= 1000f;
                unit = "分";
            }
        }
        arrLog[1] = format.format(durationLast) + unit;

        unit = "毫秒";
        if (durationStart >= 1000) {
            durationStart /= 1000f;
            unit = "秒";
            if (durationStart >= 1000) {
                durationStart /= 1000f;
                unit = "分";
            }
        }
        arrLog[2] = format.format(durationStart) + unit;

        list.add(arrLog);
        tLast = LocalTime.now();
    }
    public void restart() {
        tStart = LocalTime.now();
        tLast = tStart;

        list.add(new String[] { "开始计时 ...", "", "" });
    }

    public String getTotal() {
        float durationStart = Duration.between(tStart, LocalTime.now()).toMillis();
        String unit = "毫秒";

        if (durationStart >= 1000) {
            durationStart /= 1000f;
            unit = "秒";
            if (durationStart >= 1000) {
                durationStart /= 1000f;
                unit = "分";
            }
        }
        return format.format(durationStart) + unit;
    }
    public ArrayList<String[]> getResult() {
        list.add(new String[] { "计时结束", "", "" });
        printLog();

        return list;
    }

    private void printLog() {
        int maxWidth = 0;
        String text1, text2, text3;
        for (String[] arrLog : list) {
            maxWidth = Math.max(arrLog[0].length(), maxWidth);
        }

        for (String[] arrLog : list) {
            text1 = UtilString.padLeft(arrLog[0], maxWidth, ' ');
            text2 = UtilString.padRight(arrLog[1], 8, ' ');
            text3 = UtilString.padRight(arrLog[2], 8, ' ');
            System.err.println(text1 + "        " + text2 + "    " + text3);
        }
    }
}
