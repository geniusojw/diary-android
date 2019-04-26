package org.jerrioh.diary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import org.jerrioh.diary.R;
import org.jerrioh.diary.config.Information;
import org.jerrioh.diary.db.WriteDao;
import org.jerrioh.diary.dbmodel.Write;
import org.jerrioh.diary.util.DateUtil;

public class TodayWriteActivity extends AppCompatActivity {
    private Write todayDiary;
    private EditText titleText;
    private EditText contentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todaywrite);

        View backButton = findViewById(R.id.back_today_button);
        backButton.setEnabled(true);
        backButton.setClickable(true);
        backButton.setOnClickListener(v -> {
            saveDiary();
            Intent backIntent = new Intent(TodayWriteActivity.this, MainActivity.class);
            backIntent.putExtra("initNavId", R.id.nav_today);
            startActivity(backIntent);
        });

        titleText = findViewById(R.id.todaywrite_title);
        contentText = findViewById(R.id.todaywrite_content);

        // 오늘의 일기 생성
        WriteDao writeDao = new WriteDao(this);
        String today_yyyyMMdd = DateUtil.getyyyyMMdd();
        todayDiary = writeDao.getTodayDiary(today_yyyyMMdd);
        if (todayDiary == null) {
            int writeType = Write.WriteType.DIARY;
            String writeDay = today_yyyyMMdd;
            String writeUserId = Information.account.getUserId();
            String readUserId = Information.account.getUserId();
            todayDiary = new Write(writeType, writeDay, writeUserId, readUserId, "", "", 0);
            writeDao.insertWrite(todayDiary);
        }

        titleText.setText(todayDiary.getTitle());
        contentText.setText(todayDiary.getContent());

        if (contentText.getText().length() > 0) {
            contentText.requestFocus();
        }
    }

    @Override
    public void onBackPressed() {
        saveDiary();
        super.onBackPressed();
    }

    private void saveDiary() {
        todayDiary.setTitle(titleText.getText().toString());
        todayDiary.setContent(contentText.getText().toString());

        WriteDao writeDao = new WriteDao(this);
        writeDao.updateTodayDiary(todayDiary);
    }
}
