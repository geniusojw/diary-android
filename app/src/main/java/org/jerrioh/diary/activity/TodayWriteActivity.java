package org.jerrioh.diary.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.util.CurrentAccountUtil;
import org.jerrioh.diary.db.DiaryDao;
import org.jerrioh.diary.dbmodel.Diary;
import org.jerrioh.diary.util.DateUtil;

import java.util.Locale;

public class TodayWriteActivity extends AppCompatActivity {
    private Diary todayDiary;

    private TextView diaryDay;
    private EditText titleText;
    private EditText contentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        View backButton = findViewById(R.id.floating_back_button);
        backButton.setEnabled(true);
        backButton.setClickable(true);
        backButton.setOnClickListener(v -> {
            saveDiary();
            finish();
        });

        diaryDay = findViewById(R.id.detail_date);
        titleText = findViewById(R.id.detail_title);
        titleText.setFocusableInTouchMode(true);

        contentText = findViewById(R.id.detail_content);
        contentText.setFocusableInTouchMode(true);

        // 오늘의 일기 생성
        DiaryDao diaryDao = new DiaryDao(this);
        String today_yyyyMMdd = DateUtil.getyyyyMMdd();
        todayDiary = diaryDao.getTodayDiary(today_yyyyMMdd);
        if (todayDiary == null) {
            String writeDay = today_yyyyMMdd;
            String writeUserId = CurrentAccountUtil.getAccount(this).getUserId();
            todayDiary = new Diary(writeDay, writeUserId,  "", "", 0);
            diaryDao.insertWrite(todayDiary);
        }
        String dayString = DateUtil.getDayString(System.currentTimeMillis(), Locale.getDefault());

        diaryDay.setText(dayString);
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

        DiaryDao diaryDao = new DiaryDao(this);
        diaryDao.updateTodayDiary(todayDiary);
    }
}
