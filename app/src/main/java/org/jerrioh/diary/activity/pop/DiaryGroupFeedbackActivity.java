package org.jerrioh.diary.activity.pop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.pop.CustomPopActivity;
import org.jerrioh.diary.config.Constants;
import org.jerrioh.diary.util.CommonUtil;

public class DiaryGroupFeedbackActivity extends CustomPopActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_diary_feedback);
        super.setWindowAttribute(.95f, .5f);
    }
}
