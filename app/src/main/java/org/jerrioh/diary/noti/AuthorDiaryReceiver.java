package org.jerrioh.diary.noti;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.jerrioh.diary.util.AuthorUtil;

public class AuthorDiaryReceiver extends BroadcastReceiver {
    private static final String TAG = "AuthorDiaryReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "[DEBUG] onReceive - AuthorDiaryReceive", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onReceive - AuthorDiaryReceiver");

        AuthorUtil.uploadAuthorDiary(context);
    }
}
