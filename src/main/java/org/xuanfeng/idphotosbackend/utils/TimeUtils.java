package org.xuanfeng.idphotosbackend.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    public static final String YMD_PATTERN = "yyyy-MM-dd";

    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取时间字符串-默认格式
     *
     * @param date    日期
     * @return 时间字符串
     */
    public static String dateToStr(Date date) {
       return dateToStr(date, DEFAULT_PATTERN);
    }

    /**
     * 获取时间字符串
     *
     * @param date    日期
     * @param pattern 格式
     * @return 时间字符串
     */
    public static String dateToStr(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    /**
     * 获取指定偏移天数的日期
     *
     * @param days 负数为过去，正数为未来
     * @return 日期
     */
    public static Date getOffsetDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    /**
     * 获取今日的时间，如：2026-01-01
     * @return 日期
     */
    public static String getTodayStr() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YMD_PATTERN);
        return simpleDateFormat.format(new Date());
    }
}
