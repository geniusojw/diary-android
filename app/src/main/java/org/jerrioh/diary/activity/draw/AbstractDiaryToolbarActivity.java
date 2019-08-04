package org.jerrioh.diary.activity.draw;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.main.AbstractDiaryActivity;

public abstract class AbstractDiaryToolbarActivity extends AbstractDiaryActivity {

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void setCommonToolBar(String title) {
        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(v -> {
           finish();
        });
    }
}
