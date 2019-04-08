package org.jerrioh.diary.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {
    public static final String TIMEZONE_GMT_M10 = "Pacific/Honolulu";
    public static final String TIMEZONE_GMT_M8 = "America/Los_Angeles";
    public static final String TIMEZONE_GMT_M5 = "America/New_York";
    public static final String TIMEZONE_GMT_00 = "Greenwich";
    public static final String TIMEZONE_GMT_P8 = "Asia/Shanghai";
    public static final String TIMEZONE_GMT_P9 = "Asia/Seoul";

    private static final String DATE_PATTERN_SIMPLE = "yyyyMMdd";
    private static final String DATE_PATTERN_TEST = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String DATE_PATTERN_KOREAN = "M월 d일 (E)";
    private static final String DATE_PATTERN_ENGLISH = "EEE, d, MMM";

    private static final String LANGUAGE_TEST = "tst";
    private static final String LANGUAGE_KOREAN = "kor";
    private static final String LANGUAGE_ENGLISH = "eng";

    public static String getSimpleDateString(Date date, Locale locale) {
        return getDateString(date, TimeZone.getDefault(), DATE_PATTERN_SIMPLE, Locale.ENGLISH);
    }

    public static String getTodayDateString(Date date, Locale locale) {
        String iso3Language = locale.getISO3Language();
        if (LANGUAGE_KOREAN.equals(iso3Language)) {
            return getDateString(date, TimeZone.getDefault(), DATE_PATTERN_KOREAN, Locale.KOREAN);
        } else if (LANGUAGE_ENGLISH.equals(iso3Language)) {
            return getDateString(date, TimeZone.getDefault(), DATE_PATTERN_ENGLISH, Locale.ENGLISH);
        } else {
            return getDateString(date, TimeZone.getDefault(), DATE_PATTERN_TEST, Locale.ENGLISH);
        }
    }

    public static String getDateString(Date date, TimeZone timeZone, String pattern, Locale locale) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(date);
    }
}
