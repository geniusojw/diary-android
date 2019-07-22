package org.jerrioh.diary.util;

import android.content.Context;
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

    public static String ordinalPeople(int i) {
        String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th people";
            default:
                return i + suffixes[i % 10] + " people";
        }
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

    public static String resourceString(Context context, int resourceId, Object... args) {
        if (args.length == 0) {
            return context.getString(resourceId);
        } else {
            return context.getString(resourceId, args);

        }
    }
}
