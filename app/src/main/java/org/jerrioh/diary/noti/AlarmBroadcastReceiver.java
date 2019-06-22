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

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmBroadcastReceiver";
    private static final String CHANNEL_ID = "channel_id01";
    private static final String CHANNEL_NAME = "Notification Channel";

    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "[DEBUG] onReceive - AlarmBroadcastReceiver", Toast.LENGTH_LONG).show();
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
                .setContentTitle("알람시작")
                .setContentText("알람에 대한 설명.. " + System.currentTimeMillis())
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
