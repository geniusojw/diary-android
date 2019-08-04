package org.jerrioh.diary.activity.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import org.jerrioh.diary.activity.lock.UnlockActivity;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.LockUtil;
import org.jerrioh.diary.util.PropertyUtil;

public abstract class AbstractDiaryActivity extends AppCompatActivity {

    @Override
    protected void onPause() {
        super.onPause();
        LockUtil.setLastUseTime(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LockUtil.lockIfTimeTo(this);
    }
}
