package org.jerrioh.diary.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.jerrioh.diary.R;

public class CommonActionBarActivity extends AppCompatActivity {

    protected void setCommonToolBar(String title) {
        Toolbar toolbar = findViewById(R.id.x_toolbar);
        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(v -> {
           finish();
        });
    }
}
