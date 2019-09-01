package org.jerrioh.diary.activity.main;

import androidx.appcompat.app.AppCompatActivity;

import org.jerrioh.diary.util.LockUtil;

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
