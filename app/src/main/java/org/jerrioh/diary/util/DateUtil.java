package org.jerrioh.diary.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtil {
    public static final String TIMEZONE_GMT_M10 = "Pacific/Honolulu";
    public static final String TIMEZONE_GMT_M8 = "America/Los_Angeles";
    public static final String TIMEZONE_GMT_M5 = "America/New_York";
    public static final String TIMEZONE_GMT_00 = "Greenwich";
    public static final String TIMEZONE_GMT_P8 = "Asia/Shanghai";
    public static final String TIMEZONE_GMT_P9 = "Asia/Seoul";

    private static final String DATE_PATTERN_yyyyMMdd = "yyyyMMdd";

    private static final String DATE_PATTERN_TODAY_TEST = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String DATE_PATTERN_TODAY_KOREAN = "M월 d일 (E)";
    private static final String DATE_PATTERN_TODAY_ENGLISH = "EEE, d, MMM";

    private static final String DATE_PATTERN_DIARY_TEST = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String DATE_PATTERN_DIARY_KOREAN = "yyyy년 M월";
    private static final String DATE_PATTERN_DIARY_ENGLISH = "MMM, yyyy";

    private static final String LANGUAGE_TEST = "tst";
    private static final String LANGUAGE_KOREAN = "kor";
    private static final String LANGUAGE_ENGLISH = "eng";

    public static String getyyyyMMdd() {
        return getyyyyMMdd(System.currentTimeMillis());
    }
    public static String getyyyyMMdd(long timeMillis) {
        return getDateString(timeMillis, TimeZone.getDefault(), DATE_PATTERN_yyyyMMdd, Locale.ENGLISH);
    }

    public static String getTodayDateString(long timeMillis, Locale locale) {
        String iso3Language = locale.getISO3Language();
        if (LANGUAGE_KOREAN.equals(iso3Language)) {
            return getDateString(timeMillis, TimeZone.getDefault(), DATE_PATTERN_TODAY_KOREAN, Locale.KOREAN);
        } else if (LANGUAGE_ENGLISH.equals(iso3Language)) {
            return getDateString(timeMillis, TimeZone.getDefault(), DATE_PATTERN_TODAY_ENGLISH, Locale.ENGLISH);
        } else {
            return getDateString(timeMillis, TimeZone.getDefault(), DATE_PATTERN_TODAY_TEST, Locale.ENGLISH);
        }
    }
    public static String getDiaryDateString(long timeMillis, Locale locale) {
        String iso3Language = locale.getISO3Language();
        if (LANGUAGE_KOREAN.equals(iso3Language)) {
            return getDateString(timeMillis, TimeZone.getDefault(), DATE_PATTERN_DIARY_KOREAN, Locale.KOREAN);
        } else if (LANGUAGE_ENGLISH.equals(iso3Language)) {
            return getDateString(timeMillis, TimeZone.getDefault(), DATE_PATTERN_DIARY_ENGLISH, Locale.ENGLISH);
        } else {
            return getDateString(timeMillis, TimeZone.getDefault(), DATE_PATTERN_DIARY_TEST, Locale.ENGLISH);
        }
    }

    public static String getDiaryDateString(String yyyyMM, Locale locale) {
        try {
            Date date = new SimpleDateFormat("yyyyMMdd").parse(yyyyMM + "15");
            return getDiaryDateString(date.getTime(), locale);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDateString(long timeMillis, TimeZone timeZone, String pattern, Locale locale) {
        Date date = new Date(timeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(date);
    }

    public static boolean timePassed(long current, long updated, int seconds) {
        return current > updated + TimeUnit.SECONDS.toMillis(seconds);
    }

    public static String diffMonth(String yyyyMM, int diffMonth) {
        if (yyyyMM.length() < 6) {
            return yyyyMM;
        }
        int yyyyInteger = Integer.parseInt(yyyyMM.substring(0, 4));
        int monthInteger = Integer.parseInt(yyyyMM.substring(4, 6));


        int addYear = diffMonth / 12;
        int addMonth = diffMonth % 12;

        yyyyInteger += addYear;
        monthInteger += addMonth;

        if (monthInteger > 12) {
            yyyyInteger +=1;
            monthInteger -= 12;
        } else if (monthInteger <= 0) {
            yyyyInteger -=1;
            monthInteger += 12;
        }

        return String.format("%04d", yyyyInteger) + String.format("%02d", monthInteger);
    }
}