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
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.AuthorDiaryApis;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.model.db.DiaryDao;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.DateUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import static org.jerrioh.diary.noti.AlarmBroadcastReceiver.NOTIFICATION_ID;

public class AuthorDiaryReceiver extends BroadcastReceiver {
    private static final String TAG = "AuthorDiaryReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "[DEBUG] onReceive - AuthorDiaryReceive", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onReceive - AuthorDiaryReceiver");

        AuthorUtil.uploadAuthorDiary(context);
    }
}
