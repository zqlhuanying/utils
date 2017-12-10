package com.example.utils;

import com.example.utils.model.TimeUnitEnum;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.DurationFieldType;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author qianliao.zhuang
 */
public final class DateUtils {

    private static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static Map<TimeUnitEnum, DurationFieldType> timeMap = new HashMap<>();
    static {
        timeMap.put(TimeUnitEnum.MILLI, DurationFieldType.millis());
        timeMap.put(TimeUnitEnum.SECOND, DurationFieldType.seconds());
        timeMap.put(TimeUnitEnum.MINUTE, DurationFieldType.minutes());
        timeMap.put(TimeUnitEnum.HOUR, DurationFieldType.hours());
        timeMap.put(TimeUnitEnum.DAY, DurationFieldType.days());
        timeMap.put(TimeUnitEnum.WEEK, DurationFieldType.weeks());
        timeMap.put(TimeUnitEnum.MONTH, DurationFieldType.months());
        timeMap.put(TimeUnitEnum.YEAR, DurationFieldType.years());
    }

    private DateUtils(){}

    public static Date now(){
        return DateTime.now().toDate();
    }

    public static Date plusDays(Date date, int interval) {
        return plus(date, interval, TimeUnitEnum.DAY);
    }

    public static Date plusWeeks(Date date, int interval) {
        return plus(date, interval, TimeUnitEnum.WEEK);
    }

    public static Date plusMonths(Date date, int interval) {
        return plus(date, interval, TimeUnitEnum.MONTH);
    }

    public static Date plusYears(Date date, int interval) {
        return plus(date, interval, TimeUnitEnum.YEAR);
    }

    public static Date minus(Date date, int interval, TimeUnitEnum unit){
        return plus(date, interval * -1, unit);
    }

    public static Date minusDays(Date date, int interval) {
        return plusDays(date, interval * -1);
    }

    public static Date minusWeeks(Date date, int interval) {
        return plusWeeks(date, interval * -1);
    }

    public static Date minusMonths(Date date, int interval) {
        return plusMonths(date, interval * -1);
    }

    public static Date minusYears(Date date, int interval) {
        return plusYears(date, interval * -1);
    }

    public static Date plus(Date date, int interval, TimeUnitEnum unit){
        return toDateTime(date)
                .withFieldAdded(timeMap.get(unit), interval)
                .toDate();
    }

    public static Date max(Date lDate, Date rDate){
        return compare(lDate, rDate) > 0 ? lDate : rDate;
    }

    public static Date min(Date lDate, Date rDate){
        return compare(lDate, rDate) < 0 ? lDate : rDate;
    }

    public static int compare(Date lDate, Date rDate){
        return DateTimeComparator.getInstance().compare(lDate, rDate);
    }

    public static long getMills(){
        return DateTime.now().getMillis();
    }

    public static long getMills(Date date){
        return toDateTime(date).getMillis();
    }

    /**
     * 将 1天，1周之类的转换成毫秒数
     * 1小时 = 3600000
     * 1天 = 86400000
     * @param interval
     * @param unit
     * @return
     */
    public static long getMills(int interval, TimeUnitEnum unit){
        Date now = now();
        return getMills(plus(now, interval, unit)) - getMills(now);
    }

    public static int getHourOfDay(Date date){
        return toDateTime(date).getHourOfDay();
    }

    /**
     * 获取当天零点
     * @param date
     * @return
     */
    public static Date getBeginOfTheDay(Date date){
        return toDateTime(date)
                .dayOfWeek()
                .roundFloorCopy()
                .toDate();
    }

    /**
     * 获取第二天的零点
     * @param date
     * @return
     */
    public static Date getNextBeginOfTheDay(Date date){
        return toDateTime(date)
                .dayOfWeek()
                .roundCeilingCopy()
                .toDate();
    }

    public static String parse(Date date){
        return parse(date, STANDARD_FORMAT);
    }

    public static String parse(Date date, String format){
        return toDateTime(date).toString(format);
    }

    public static Date parse(String dateStr){
        return parse(dateStr, STANDARD_FORMAT);
    }

    public static Date parse(String dateStr, String format){
        return DateTimeFormat
                .forPattern(format)
                .withLocale(Locale.CHINESE)
                .parseDateTime(dateStr)
                .toDate();
    }

    public static Date parse(long timestamp){
        return new Instant(timestamp)
                .toDateTime()
                .toDate();
    }

    public static DateTime toDateTime(Date date){
        return new DateTime(date);
    }

}
