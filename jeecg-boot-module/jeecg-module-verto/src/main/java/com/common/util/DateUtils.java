package com.verto.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具类
 * 
 * @author Verto Team
 * @since 2024-01-01
 */
public class DateUtils {

    /**
     * 默认日期时间格式
     */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 默认时间格式
     */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    /**
     * 获取当前时间
     * 
     * @return 当前时间
     */
    public static Date now() {
        return new Date();
    }

    /**
     * 获取当前LocalDateTime
     * 
     * @return 当前LocalDateTime
     */
    public static LocalDateTime nowLocalDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 格式化当前时间
     * 
     * @return 格式化后的时间字符串
     */
    public static String formatNow() {
        return formatNow(DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * 按指定格式格式化当前时间
     * 
     * @param pattern 时间格式
     * @return 格式化后的时间字符串
     */
    public static String formatNow(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 格式化LocalDateTime
     * 
     * @param dateTime LocalDateTime对象
     * @return 格式化后的时间字符串
     */
    public static String format(LocalDateTime dateTime) {
        return format(dateTime, DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * 按指定格式格式化LocalDateTime
     * 
     * @param dateTime LocalDateTime对象
     * @param pattern 时间格式
     * @return 格式化后的时间字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 解析时间字符串为LocalDateTime
     * 
     * @param dateTimeStr 时间字符串
     * @return LocalDateTime对象
     */
    public static LocalDateTime parse(String dateTimeStr) {
        return parse(dateTimeStr, DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * 按指定格式解析时间字符串为LocalDateTime
     * 
     * @param dateTimeStr 时间字符串
     * @param pattern 时间格式
     * @return LocalDateTime对象
     */
    public static LocalDateTime parse(String dateTimeStr, String pattern) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }
}