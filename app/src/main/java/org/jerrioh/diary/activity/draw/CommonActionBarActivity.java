package org.jerrioh.diary.activity.draw;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.jerrioh.diary.R;

public class CommonActionBarActivity extends AppCompatActivity {

    protected void setCommonToolBar(String title) {
        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(v -> {
           finish();
        });
    }
}
