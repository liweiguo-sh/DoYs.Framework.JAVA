package doys.framework.util;
import doys.framework.a0.Const;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class UtilDate {
    public static long getMilliSecond() {
        return getMilliSecond(LocalDateTime.now());
    }
    public static long getMilliSecond(LocalDateTime ldt) {
        // -- 取毫秒数 --
        return ldt.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    // -- get string from date/time/datetime ----------------------------------
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
    public static String getDateTimeStr(LocalDateTime dateTime) {
        return getDateTimeStr(dateTime, null);
    }
    public static String getDateTimeStr(LocalDateTime dateTime, String dtFormat) {
        if (dateTime == null) {
            dateTime = LocalDateTime.now();
        }
        if (dtFormat == null || dtFormat.equals("")) {
            dtFormat = Const.datetimeFormat;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(dtFormat));
    }
    public static String getDateTimeString() {
        return getDateTimeStr(null, Const.datetimeFormat);
    }

    // -- get date/time/datetime from string ----------------------------------
    public static LocalDateTime getDateTime(String strDateTime) {
        String format = Const.datetimeFormat;
        if (strDateTime.length() > format.length()) {
            strDateTime = strDateTime.substring(0, format.length());
        }

        return LocalDateTime.parse(strDateTime, DateTimeFormatter.ofPattern(format));
    }
    public static LocalDate getDate(String strDate) {
        return LocalDate.parse(strDate, DateTimeFormatter.ofPattern(Const.dateFormat));
    }

    // -- get diff between date/time/datetime ---------------------------------
    public static long getDateTimeDiff(LocalDateTime startTime) {
        return getDateTimeDiff(startTime, LocalDateTime.now());
    }
    public static long getDateTimeDiff(LocalDateTime startTime, LocalDateTime endTime) {
        // -- 返回 endTime - startTime 的时间差，单位：毫秒 --
        return java.time.Duration.between(startTime, endTime).toMillis();
    }
}