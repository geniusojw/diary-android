package org.jerrioh.diary.noti;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.main.MainActivity;
import org.jerrioh.diary.util.CommonUtil;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmBroadcastReceiver";
    private static final String CHANNEL_ID = "channel_id01";
    private static final String CHANNEL_NAME = "Notification Channel";

    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        String[] alarmTips = new String[] {
                context.getResources().getString(R.string.alarm_tip1),
                context.getResources().getString(R.string.alarm_tip2),
                context.getResources().getString(R.string.alarm_tip3),
                context.getResources().getString(R.string.alarm_tip4),
                context.getResources().getString(R.string.alarm_tip5),
                context.getResources().getString(R.string.alarm_tip6),
                context.getResources().getString(R.string.alarm_tip7),
                context.getResources().getString(R.string.alarm_tip8),
                context.getResources().getString(R.string.alarm_tip9),
                context.getResources().getString(R.string.alarm_tip10),
                context.getResources().getString(R.string.alarm_tip11),
                context.getResources().getString(R.string.alarm_tip12),
                context.getResources().getString(R.string.alarm_tip13),
                context.getResources().getString(R.string.alarm_tip14),
                context.getResources().getString(R.string.alarm_tip15),
                context.getResources().getString(R.string.alarm_tip16),
                context.getResources().getString(R.string.alarm_tip17),
                context.getResources().getString(R.string.alarm_tip18),
                context.getResources().getString(R.string.alarm_tip19),
                context.getResources().getString(R.string.alarm_tip20),
                context.getResources().getString(R.string.alarm_tip21),
                context.getResources().getString(R.string.alarm_tip22),
                context.getResources().getString(R.string.alarm_tip23),
                context.getResources().getString(R.string.alarm_tip24),
                context.getResources().getString(R.string.alarm_tip25)
        };

        //Toast.makeText(context, "[DEBUG] onReceive - AlarmBroadcastReceiver", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onReceive - AlarmBroadcastReceiver");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setVibrationPattern(new long[] {500, 500, 500, 500});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE); // VISIBILITY_SECRET

            NotificationManager notificationManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
            notificationManager.createNotificationChannel(notificationChannel);
        }


        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        //mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(context.getResources().getString(R.string.alarm_title))
                .setContentText(CommonUtil.randomString(alarmTips))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)) // Notification.DEFAULT_SOUND
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        Notification notification = builder.build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID, notification);
    }
}