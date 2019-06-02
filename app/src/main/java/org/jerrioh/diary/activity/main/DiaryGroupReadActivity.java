package org.jerrioh.diary.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.config.Constants;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.util.CommonUtil;
import org.jerrioh.diary.util.DateUtil;

public class DiaryGroupReadActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_diary_detail);

        Intent intent = getIntent();
        String nickname = intent.getStringExtra("nickname");
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");

        TextView nicknameTextView = findViewById(R.id.text_view_group_diary_nickname);
        nicknameTextView.setText(nickname);

        EditText titleEditText = findViewById(R.id.edit_text_group_diary_title);
        titleEditText.setText(CommonUtil.defaultIfEmpty(title, Constants.DEFAULT_TITLE));

        EditText contentEditText = findViewById(R.id.edit_text_group_diary_content);
        contentEditText.setText(content);
    }
}
