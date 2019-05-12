package org.jerrioh.diary.activity.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.model.db.DiaryDao;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.util.DateUtil;

import java.util.Locale;

public class TodayWriteActivity extends AppCompatActivity {
    private Diary todayDiary;

    private TextView diaryDate;
    private EditText titleText;
    private EditText contentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_detail);

        View backButton = findViewById(R.id.floating_back_button);
        backButton.setEnabled(true);
        backButton.setClickable(true);
        backButton.setOnClickListener(v -> {
            saveDiary();
            finish();
        });

        diaryDate = findViewById(R.id.detail_date);
        titleText = findViewById(R.id.detail_title);
        titleText.setFocusableInTouchMode(true);

        contentText = findViewById(R.id.detail_content);
        contentText.setFocusableInTouchMode(true);

        // 오늘의 일기 생성
        DiaryDao diaryDao = new DiaryDao(this);
        String today_yyyyMMdd = DateUtil.getyyyyMMdd();
        todayDiary = diaryDao.getDiary(today_yyyyMMdd);
        if (todayDiary == null) {
            todayDiary = new Diary();
            todayDiary.setDiaryDate(today_yyyyMMdd);
            todayDiary.setTitle("");
            todayDiary.setContent("");
            todayDiary.setAuthorDiaryStatus(Diary.DiaryStatus.UNSAVED);
            todayDiary.setAccountDiaryStatus(Diary.DiaryStatus.UNSAVED);
            diaryDao.insertDiary(todayDiary);
        }
        String diaryDateString = DateUtil.getDayString(System.currentTimeMillis(), Locale.getDefault());

        diaryDate.setText(diaryDateString);
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
        diaryDao.updateDiaryContent(todayDiary.getDiaryDate(), todayDiary.getTitle(), todayDiary.getContent());
    }
}
