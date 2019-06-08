package org.jerrioh.diary.util;

import android.text.TextUtils;

import java.text.ParseException;
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

    private static final String DATE_PATTERN_yyyyMMdd = "yyyyMMdd";
    private static final String DATE_PATTERN_HHmmss = "HHmmss";

    private static final String DATE_PATTERN_FULL_TEST = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String DATE_PATTERN_FULL_KOREAN = "yyyy년 M월 d일 (E) HH:mm";
    private static final String DATE_PATTERN_FULL_ENGLISH = "EEE, d, MMM, yyyy HH:mm";

    private static final String DATE_PATTERN_SKIP_TIME_TEST = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String DATE_PATTERN_SKIP_TIME_KOREAN = "yyyy년 M월 d일 (E)";
    private static final String DATE_PATTERN_SKIP_TIME_ENGLISH = "EEE, d, MMM, yyyy";

    private static final String DATE_PATTERN_SKIP_YEAR_TEST = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String DATE_PATTERN_SKIP_YEAR_KOREAN = "M월 d일 (E) HH:mm";
    private static final String DATE_PATTERN_SKIP_YEAR_ENGLISH = "EEE, d, MMM HH:mm";

    private static final String DATE_PATTERN_YEAR_MONTH_TEST = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String DATE_PATTERN_YEAR_MONTH_KOREAN = "yyyy년 M월";
    private static final String DATE_PATTERN_YEAR_MONTH_ENGLISH = "MMM, yyyy";

    private static final String DATE_PATTERN_DAY_OF_WEEK = "EEEE";

    private static final String LANGUAGE_TEST = "tst";
    private static final String LANGUAGE_KOREAN = "kor";
    private static final String LANGUAGE_ENGLISH = "eng";

    public static String getHHmmss() {
        return getDateString(System.currentTimeMillis(), TimeZone.getDefault(), DATE_PATTERN_HHmmss, Locale.ENGLISH);
    }

    public static String getyyyyMMdd() {
        return getDateString(System.currentTimeMillis(), TimeZone.getDefault(), DATE_PATTERN_yyyyMMdd, Locale.ENGLISH);
    }

    public static String getyyyyMMdd(long timeMillis) {
        return getDateString(timeMillis, TimeZone.getDefault(), DATE_PATTERN_yyyyMMdd, Locale.ENGLISH);
    }

    public static String getDateStringFull(long timeMillis) {
        String iso3Language = Locale.getDefault().getISO3Language();
        if (LANGUAGE_KOREAN.equals(iso3Language)) {
            return getDateString(timeMillis, TimeZone.getDefault(), DATE_PATTERN_FULL_KOREAN, Locale.KOREAN);
        } else if (LANGUAGE_ENGLISH.equals(iso3Language)) {
            return getDateString(timeMillis, TimeZone.getDefault(), DATE_PATTERN_FULL_ENGLISH, Locale.ENGLISH);
        } else {
            return getDateString(timeMillis, TimeZone.getDefault(), DATE_PATTERN_FULL_TEST, Locale.ENGLISH);
        }
    }

    // 년 월 일 표시
    public static String getDateStringSkipTime() {
        long timeMillis = System.currentTimeMillis();
        String iso3Language = Locale.getDefault().getISO3Language();
        if (LANGUAGE_KOREAN.equals(iso3Language)) {
            return getDateString(timeMillis, TimeZone.getDefault(), DATE_PATTERN_SKIP_TIME_KOREAN, Locale.KOREAN);
        } else if (LANGUAGE_ENGLISH.equals(iso3Language)) {
            return getDateString(timeMillis, TimeZone.getDefault(), DATE_PATTERN_SKIP_TIME_ENGLISH, Locale.ENGLISH);
        } else {
            return getDateString(timeMillis, TimeZone.getDefault(), DATE_PATTERN_SKIP_TIME_TEST, Locale.ENGLISH);
        }
    }

    public static String getDateStringSkipYear(long timeMillis) {
        String iso3Language = Locale.getDefault().getISO3Language();
        if (LANGUAGE_KOREAN.equals(iso3Language)) {
            return getDateString(timeMillis, TimeZone.getDefault(), DATE_PATTERN_SKIP_YEAR_TEST, Locale.KOREAN);
        } else if (LANGUAGE_ENGLISH.equals(iso3Language)) {
            return getDateString(timeMillis, TimeZone.getDefault(), DATE_PATTERN_SKIP_YEAR_KOREAN, Locale.ENGLISH);
        } else {
            return getDateString(timeMillis, TimeZone.getDefault(), DATE_PATTERN_SKIP_YEAR_ENGLISH, Locale.ENGLISH);
        }
    }

    private static String getDateString(long timeMillis, TimeZone timeZone, String pattern, Locale locale) {
        Date date = new Date(timeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(date);
    }

    // 년 월 표시
    public static String getDateString_yyyyMMdd(String yyyyMMdd) {
        if (TextUtils.isEmpty(yyyyMMdd) || yyyyMMdd.length() != 8) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            String iso3Language = Locale.getDefault().getISO3Language();
            if (LANGUAGE_KOREAN.equals(iso3Language)) {
                return new SimpleDateFormat(DATE_PATTERN_SKIP_TIME_KOREAN, Locale.KOREAN).format(sdf.parse(yyyyMMdd));
            } else if (LANGUAGE_ENGLISH.equals(iso3Language)) {
                return new SimpleDateFormat(DATE_PATTERN_SKIP_TIME_ENGLISH, Locale.ENGLISH).format(sdf.parse(yyyyMMdd));
            } else {
                return new SimpleDateFormat(DATE_PATTERN_SKIP_TIME_TEST, Locale.ENGLISH).format(sdf.parse(yyyyMMdd));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 년 월 표시
    public static String getDateStringYearMonth(String yyyyMM) {
        if (TextUtils.isEmpty(yyyyMM) || yyyyMM.length() != 6) {
            return null;
        }

        String yyyyMMdd = yyyyMM + "15";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String iso3Language = Locale.getDefault().getISO3Language();
            if (LANGUAGE_KOREAN.equals(iso3Language)) {
                return new SimpleDateFormat(DATE_PATTERN_YEAR_MONTH_KOREAN, Locale.KOREAN).format(sdf.parse(yyyyMMdd));
            } else if (LANGUAGE_ENGLISH.equals(iso3Language)) {
                return new SimpleDateFormat(DATE_PATTERN_YEAR_MONTH_ENGLISH, Locale.ENGLISH).format(sdf.parse(yyyyMMdd));
            } else {
                return new SimpleDateFormat(DATE_PATTERN_YEAR_MONTH_TEST, Locale.ENGLISH).format(sdf.parse(yyyyMMdd));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String dayOfWeek(String yyyyMMdd) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            String iso3Language = Locale.getDefault().getISO3Language();
            if (LANGUAGE_KOREAN.equals(iso3Language)) {
                return new SimpleDateFormat(DATE_PATTERN_DAY_OF_WEEK).format(sdf.parse(yyyyMMdd));
            } else if (LANGUAGE_ENGLISH.equals(iso3Language)) {
                return new SimpleDateFormat(DATE_PATTERN_DAY_OF_WEEK).format(sdf.parse(yyyyMMdd));
            } else {
                return new SimpleDateFormat(DATE_PATTERN_DAY_OF_WEEK).format(sdf.parse(yyyyMMdd));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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
