package com.doys.framework.util;
import com.doys.framework.config.Const;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UtilDate {
    public static String getDateStr() {
        return getDateStr(null, null);
    }
    public static String getDateStr(LocalDate date, String dtFormat) {
        if (date == null) {
            date = LocalDate.now();
        }
        if (dtFormat == null || dtFormat.equals("")) {
            dtFormat = Const.dateFormat;
        }
        return date.format(DateTimeFormatter.ofPattern(dtFormat));
    }

    public static String getDateTimeStr() {
        return getDateTimeStr(null, null);
    }
    public static String getDateTimeStr(LocalDateTime dateTime, String dtFormat) {
        if (dateTime == null) {
            dateTime = LocalDateTime.now();
        }
        if (dtFormat == null || dtFormat.equals("")) {
            dtFormat = Const.dateFormat;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(dtFormat));
    }
    public static String getDateTimeString() {
        return getDateTimeStr(null, Const.datetimeFormat);
    }

    public static long getDateTimeDiff(LocalDateTime startTime) {
        return getDateTimeDiff(startTime, LocalDateTime.now());
    }
    public static long getDateTimeDiff(LocalDateTime startTime, LocalDateTime endTime) {
        return java.time.Duration.between(startTime, endTime).toMillis();
    }
}