package org.jerrioh.diary.activity.draw;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Switch;

import org.jerrioh.diary.R;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.Setting;
import org.jerrioh.diary.model.db.SettingDao;
import org.jerrioh.diary.util.AuthorUtil;

public class AuthorActivity extends CommonActionBarActivity {
    private static final String TAG = "AuthorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
    }
}
