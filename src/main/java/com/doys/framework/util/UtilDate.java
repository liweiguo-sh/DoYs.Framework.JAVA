package com.doys.framework.util;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UtilDate {
    public static String dateFormat = "yyyy-MM-dd";                         // -- 默认日期格式 --
    public static String timeFormat = "HH:mm:ss";                           // -- 默认时间格式 --
    public static String datetimeFormat = "yyyy-MM-dd HH:mm:ss";            // -- 默认日期时间格式 --

    public static String getDateStr() {
        return getDateStr(null, null);
    }
    public static String getDateStr(LocalDate date, String dtFormat) {
        if (date == null) {
            date = LocalDate.now();
        }
        if (dtFormat == null || dtFormat.equals("")) {
            dtFormat = dateFormat;
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
            dtFormat = datetimeFormat;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(dtFormat));
    }
    public static String getDateTimeString() {
        return getDateTimeStr(null, "yyyy-MM-dd HH:mm:ss.SSS");
    }

    public static long getDateTimeDiff(LocalDateTime startTime) {
        return getDateTimeDiff(startTime, LocalDateTime.now());
    }
    public static long getDateTimeDiff(LocalDateTime startTime, LocalDateTime endTime) {
        return java.time.Duration.between(startTime, endTime).toMillis();
    }
}