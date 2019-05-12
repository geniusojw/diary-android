package org.jerrioh.diary.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.jerrioh.diary.config.Constants;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.R;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.CommonUtil;

import java.util.Locale;

public class DiaryReadActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_detail);

        Intent intent = getIntent();
        Diary diary = (Diary) intent.getSerializableExtra("diary");

        TextView day = findViewById(R.id.detail_date);
        day.setText(DateUtil.getDayString_yyyyMMdd(diary.getDiaryDate(), Locale.getDefault()));

        EditText title = findViewById(R.id.detail_title);
        title.setText(CommonUtil.defaultIfEmpty(diary.getTitle(), Constants.DEFAULT_TITLE));

        EditText content = findViewById(R.id.detail_content);
        content.setText(diary.getContent());

        View backButton = findViewById(R.id.floating_back_button);
        backButton.setEnabled(true);
        backButton.setClickable(true);
        backButton.setOnClickListener(v -> {
            finish();
        });
    }
}
