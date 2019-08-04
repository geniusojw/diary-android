package org.jerrioh.diary.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import org.jerrioh.diary.activity.lock.UnlockActivity;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.Property;

public class LockUtil {
    private static final String TAG = "LockSettingActivity";

    public static void setLastUseTime(Context context) {
        String lockUse = PropertyUtil.getProperty(Property.Key.SCREEN_LOCK_USE, context);
        if (Integer.parseInt(lockUse) == 1) {
            Author author = AuthorUtil.getAuthor(context);
            if (TextUtils.isEmpty(author.getAccountEmail())) {
                PropertyUtil.setProperty(Property.Key.SCREEN_LOCK_USE, "0", context);
                PropertyUtil.setProperty(Property.Key.SCREEN_LOCK_4DIGIT, "", context);
                PropertyUtil.setProperty(Property.Key.SCREEN_LOCK_LAST_USE_TIME, "0", context);
                return;
            }
            PropertyUtil.setProperty(Property.Key.SCREEN_LOCK_LAST_USE_TIME, String.valueOf(System.currentTimeMillis()), context);
        }
    }

    public static void lockIfTimeTo(Context context) {
        String lockUse = PropertyUtil.getProperty(Property.Key.SCREEN_LOCK_USE, context);
        if (Integer.parseInt(lockUse) == 1) {
            Author author = AuthorUtil.getAuthor(context);
            if (TextUtils.isEmpty(author.getAccountEmail())) {
                PropertyUtil.setProperty(Property.Key.SCREEN_LOCK_USE, "0", context);
                PropertyUtil.setProperty(Property.Key.SCREEN_LOCK_4DIGIT, "", context);
                PropertyUtil.setProperty(Property.Key.SCREEN_LOCK_LAST_USE_TIME, "0", context);
                return;
            }

            Long unlockTime = Long.parseLong(PropertyUtil.getProperty(Property.Key.SCREEN_LOCK_LAST_USE_TIME, context));
            if (System.currentTimeMillis() < unlockTime + Property.Config.SCREEN_LOCK_LOCK_MILLIS) {
                PropertyUtil.setProperty(Property.Key.SCREEN_LOCK_LAST_USE_TIME, String.valueOf(System.currentTimeMillis()), context);
                return;
            }

            String accountEmail = author.getAccountEmail();

            Intent lockIntent = new Intent(context, UnlockActivity.class);
            lockIntent.putExtra("accountEmail", accountEmail);
            context.startActivity(lockIntent);
        }
    }
}
