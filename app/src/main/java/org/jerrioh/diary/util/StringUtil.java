package org.jerrioh.diary.util;

public class StringUtil {
    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static String defaultIfEmpty(String string, String defaultString) {
        return isEmpty(string) ? defaultString : string;
    }
}
