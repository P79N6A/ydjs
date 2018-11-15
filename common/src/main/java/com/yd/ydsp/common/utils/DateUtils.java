package com.yd.ydsp.common.utils;

import com.yd.ydsp.common.constants.Constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    /**
     * 时间相差天数
     * @param startDate
     * @param endDate
     * @return
     */
    public static Long subDate(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate,endDate);
    }

    /**
     * 时间相差天数
     * @param startDate
     * @param endDate
     * @return
     */
    public static Long subDate(String startDate, String endDate) {
        LocalDate sd = LocalDate.parse(startDate,DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate ed = LocalDate.parse(endDate,DateTimeFormatter.ISO_LOCAL_DATE);
        return subDate(sd,ed);
    }

    /**
     * 时间相差月数
     * @param startDate
     * @param endDate
     * @return
     */
    public static Long subMonth(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.MONTHS.between(startDate,endDate);
    }

    /**
     * 时间相差月数
     * @param startDate
     * @param endDate
     * @return
     */
    public static Long subMonth(String startDate, String endDate) {
        LocalDate sd = LocalDate.parse(startDate,DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate ed = LocalDate.parse(endDate,DateTimeFormatter.ISO_LOCAL_DATE);
        return subMonth(sd,ed);
    }

    /**
     * 计算开始日到结束日是几个月几天
     * @param startDate
     * @param endDate
     * @return
     */
    public static Map<String,Long> subMonthAndDay(LocalDate startDate, LocalDate endDate) {
        Long month= ChronoUnit.MONTHS.between(startDate,endDate);
        Long day = 0L;
        if(month<=0){
            month = 0L;
            day = subDate(startDate,endDate);
        }else{
            LocalDate today = LocalDate.now();
            /**
             * 本月最后一天
             */
            LocalDate lastDayOfThisMonth = today.with(TemporalAdjusters.lastDayOfMonth());
            day = subDate(startDate,lastDayOfThisMonth);
            if(day<=0){
                day = 0L;
            }
        }
        Map<String,Long> result = new HashMap<>();
        result.put("month",month);
        result.put("day",day);
        return result;
    }

    public static Map<String,Long> subMonthAndDay(String startDate, String endDate) {
        LocalDate sd = LocalDate.parse(startDate,DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate ed = LocalDate.parse(endDate,DateTimeFormatter.ISO_LOCAL_DATE);
        return subMonthAndDay(sd,ed);
    }


    /**
     * 在指定的日期上增加月份
     * @param date
     * @param monthNum
     * @return
     */
    public static String plusMonth(LocalDate date,Integer monthNum){
        return date.plusMonths(monthNum).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * 在指定的日期上增加月份
     * @param date
     * @param monthNum
     * @return
     */
    public static String plusMonth(String date,Integer monthNum){
        LocalDate now = LocalDate.parse(date,DateTimeFormatter.ISO_LOCAL_DATE);
        return plusMonth(now,monthNum);
    }

    /**
     * 在当前日期上增加一个月份
     * @param monthNum
     * @return
     */
    public static String plusMonthForToday(Integer monthNum){
        return LocalDate.now().plusMonths(monthNum).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * 在指定的日期上增加天
     * @param date
     * @param dayNum
     * @return
     */
    public static Date plusDay(Date date,int dayNum){
        LocalDate now = date2LocalDate(date);
        return LocalDateToUdate(now.plusDays(dayNum));
    }

    /**
     * 取指定年月的第一天
     * @param year
     * @param month
     * @return
     */
    public static Date getFirstDayByMonth(int year,int month){
        LocalDate crischristmas = LocalDate.of(year, month, 1);
        return LocalDateToUdate(crischristmas);
    }

    /**
     * 取指定年月的最后一天
     * @param year
     * @param month
     * @return
     */
    public static Date getLastDayByMonth(int year,int month){
        LocalDate crischristmas = LocalDate.of(year, month, 1);
        LocalDate lastDayOfThisMonth = crischristmas.with(TemporalAdjusters.lastDayOfMonth());
        return LocalDateToUdate(lastDayOfThisMonth);
    }

    /**
     * 取今天日期的字串
     * @return
     */
    public static String getToday(){
        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * 取指定日期的字串
     * @return
     */
    public static String getDate(LocalDate date){
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * LocalDate转为Date
     * @param localDate
     * @return
     */
    public static Date LocalDateToUdate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * LocalDate转为Date
     * @param date
     * @return
     */
    public static Date dateStrToUdate(String date) {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate localDate = LocalDate.parse(date,DateTimeFormatter.ISO_LOCAL_DATE);
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * LocalTime时间转为Date
     * @param localDate
     * @param localTime
     * @return
     */
    public static Date LocalTimeToUdate(LocalDate localDate,LocalTime localTime) {
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * 从Date转为LocalDate
     * @param date
     * @return
     */
    public static LocalDate date2LocalDate(Date date){
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();

        // atZone()方法返回在指定时区从此Instant生成的ZonedDateTime。
        return instant.atZone(zoneId).toLocalDate();
    }


    /**
     * date转为字串
     * @param date
     * @return
     */
    public static String date2String(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * date转为字串
     * @param date
     * @return
     */
    public static String date2String2(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(date);
    }

    /**
     * date转为年月日字串
     * @param date
     * @return
     */
    public static String date2StrWithYearAndMonthAndDay(Date date){
        return date2StrWithYearAndMonthAndDay(date,null);
    }

    public static String date2StrWithYearAndMonthAndDay(Date date,String format) {
        if(format==null){
            format = "yyyy-MM-dd";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String[] date2StrWithYearAndMonthAndDayArray(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date).split("-");
    }

    /**
     * date转为年月字串
     * @param date
     * @return
     */
    public static String date2StrWithYearAndMonth(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        return sdf.format(date);
    }


    /**
     * date转为年周字串
     * @param date
     * @return
     */
    public static String date2StrWithYearAndWeek(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String year = sdf.format(date);
        int week = getWeekOfYear(date);
        return year+"-"+week;
    }

    /**
     * 计算从现在开始到凌晨还有多少秒
     * @return
     */
    public static int getFragmentInSeconds(){
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now();
        LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);
        LocalDateTime tomorrowMidnight = todayMidnight.plusDays(1);
        return new Long(TimeUnit.NANOSECONDS.toSeconds(Duration.between(LocalDateTime.now(), tomorrowMidnight).toNanos())).intValue();
    }

    /**
     * 获取日期是当年的第几周
     * @param date
     * @return
     */
    public static int getWeekOfYear(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 取指定年第几周的星期一的日期
     * @param year
     * @param week
     * @return
     */
    public static Date getDateByWeek(int year,int week){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year); // 年
        cal.set(Calendar.WEEK_OF_YEAR, week); // 设置为年的第几周
        cal.set(Calendar.DAY_OF_WEEK, 2); // 1表示周日，2表示周一，7表示周六
        Date date = cal.getTime();
        return date;
    }

    /**
     * 判断两个日期是否是相同的，只看年月日
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDate(Date date1, Date date2) {
        boolean isSameDate = false;
        try {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);

            boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                    .get(Calendar.YEAR);
            boolean isSameMonth = isSameYear
                    && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
            isSameDate = isSameMonth
                    && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                    .get(Calendar.DAY_OF_MONTH);

            return isSameDate;
        } catch (Exception e) {

        }
        return isSameDate;


    }

    /**
     *
     * 取得当前时间戳（精确到秒）
     *
     * @return nowTimeStamp
     */
    public static String getNowTimeStamp() {
        long time = System.currentTimeMillis();
        String nowTimeStamp = String.valueOf(time / 1000);
        return nowTimeStamp;
    }

    /**
     * 获取精确到秒的时间戳
     * @param date
     * @return
     */
    public static int getSecondTimestamp(Date date){
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime()/1000);
        return Integer.valueOf(timestamp);
    }

    public static Date getDateBySecondTimestamp(Integer timeStamp) {
        return new Date(timeStamp*1000L);
    }

}
