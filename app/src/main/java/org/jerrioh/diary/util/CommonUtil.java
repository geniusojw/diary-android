package org.jerrioh.diary.util;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.Random;

public class CommonUtil {
    public static String defaultIfEmpty(String string, String defaultString) {
        return TextUtils.isEmpty(string) ? defaultString : string;
    }

    public static boolean isEmailPattern(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isAnyEmpty(String... texts) {
        for (String text : texts) {
            if (TextUtils.isEmpty(text)) {
                return true;
            }
        }
        return false;
    }
    public static String randomString(String... strings) {
        Random random = new Random();
        return strings[random.nextInt(strings.length)];
    }
}
