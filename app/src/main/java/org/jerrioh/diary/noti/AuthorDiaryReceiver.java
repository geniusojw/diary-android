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

        AuthorDiaryApis authorDiaryApis = new AuthorDiaryApis(context);

        DiaryDao diaryDao = new DiaryDao(context);
        String today_yyyyMMdd = DateUtil.getyyyyMMdd(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1));
        Diary diary = diaryDao.getDiary(today_yyyyMMdd);

        if (diary == null) {
            Toast.makeText(context, "[DEBUG] AuthorDiaryReceiver.onReceive 일기를 업로드하려고 햇으나 일기를 쓰지 않았다.", Toast.LENGTH_LONG).show();
        }

        authorDiaryApis.write(diary, new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                Log.d(TAG, "result = " + httpStatus);
                Toast.makeText(context, "[DEBUG] httpStatus = " + httpStatus, Toast.LENGTH_LONG).show();
            }
        });
    }
}
