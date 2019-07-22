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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.main.MainActivity;
import org.jerrioh.diary.util.CommonUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                context.getResources().getString(R.string.alarm_tip8)
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
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)) // Notification.DEFAULT_SOUND
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        Notification notification = builder.build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID, notification);
    }
}

//    String[] wiseSayings = new String[]{
//            context.getResources().getString(R.string.alarm_wise_saying1),
//            context.getResources().getString(R.string.alarm_wise_saying2),
//            context.getResources().getString(R.string.alarm_wise_saying3),
//            context.getResources().getString(R.string.alarm_wise_saying4),
//            context.getResources().getString(R.string.alarm_wise_saying5),
//            context.getResources().getString(R.string.alarm_wise_saying6),
//            context.getResources().getString(R.string.alarm_wise_saying7),
//            context.getResources().getString(R.string.alarm_wise_saying8),
//            context.getResources().getString(R.string.alarm_wise_saying9),
//            context.getResources().getString(R.string.alarm_wise_saying10),
//            context.getResources().getString(R.string.alarm_wise_saying11),
//            context.getResources().getString(R.string.alarm_wise_saying12),
//            context.getResources().getString(R.string.alarm_wise_saying13),
//            context.getResources().getString(R.string.alarm_wise_saying14),
//            context.getResources().getString(R.string.alarm_wise_saying15),
//            context.getResources().getString(R.string.alarm_wise_saying16),
//            context.getResources().getString(R.string.alarm_wise_saying17),
//            context.getResources().getString(R.string.alarm_wise_saying18),
//            context.getResources().getString(R.string.alarm_wise_saying19),
//            context.getResources().getString(R.string.alarm_wise_saying20),
//            context.getResources().getString(R.string.alarm_wise_saying21),
//            context.getResources().getString(R.string.alarm_wise_saying22),
//            context.getResources().getString(R.string.alarm_wise_saying23),
//            context.getResources().getString(R.string.alarm_wise_saying24),
//            context.getResources().getString(R.string.alarm_wise_saying25),
//            context.getResources().getString(R.string.alarm_wise_saying26),
//            context.getResources().getString(R.string.alarm_wise_saying27),
//            context.getResources().getString(R.string.alarm_wise_saying28),
//            context.getResources().getString(R.string.alarm_wise_saying29),
//            context.getResources().getString(R.string.alarm_wise_saying30),
//            context.getResources().getString(R.string.alarm_wise_saying31),
//            context.getResources().getString(R.string.alarm_wise_saying32),
//            context.getResources().getString(R.string.alarm_wise_saying33),
//            context.getResources().getString(R.string.alarm_wise_saying34),
//            context.getResources().getString(R.string.alarm_wise_saying35),
//            context.getResources().getString(R.string.alarm_wise_saying36),
//            context.getResources().getString(R.string.alarm_wise_saying37),
//            context.getResources().getString(R.string.alarm_wise_saying38),
//            context.getResources().getString(R.string.alarm_wise_saying39),
//            context.getResources().getString(R.string.alarm_wise_saying40),
//            context.getResources().getString(R.string.alarm_wise_saying41),
//            context.getResources().getString(R.string.alarm_wise_saying42),
//            context.getResources().getString(R.string.alarm_wise_saying43),
//            context.getResources().getString(R.string.alarm_wise_saying44),
//            context.getResources().getString(R.string.alarm_wise_saying45),
//            context.getResources().getString(R.string.alarm_wise_saying46),
//            context.getResources().getString(R.string.alarm_wise_saying47),
//            context.getResources().getString(R.string.alarm_wise_saying48),
//            context.getResources().getString(R.string.alarm_wise_saying49),
//            context.getResources().getString(R.string.alarm_wise_saying50)
//    };