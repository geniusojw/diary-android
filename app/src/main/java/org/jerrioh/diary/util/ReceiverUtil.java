package org.jerrioh.diary.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.jerrioh.diary.noti.AlarmBroadcastReceiver;
import org.jerrioh.diary.noti.AuthorDiaryReceiver;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class ReceiverUtil {

    private static final int REQUEST_CODE_ALARM = 101;
    private static final int REQUEST_CODE_YESTERDAY = 102;

    // https://androidclarified.com/android-example-alarm-manager-complete-working/
    public static void setAlarmReceiverOn(Context context, int hour, int minute) {
        Intent receiverIntent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static void setAlarmReceiverOff(Context context) {
        Intent receiverIntent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM, receiverIntent, PendingIntent.FLAG_NO_CREATE);

        if (pendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
    }

    public static void setDiaryReceiverOn(Context context) {
        Intent receiverIntent = new Intent(context, AuthorDiaryReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_YESTERDAY, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 15);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
