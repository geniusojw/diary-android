package org.jerrioh.diary.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.model.db.DiaryDao;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.util.DateUtil;

public class DiaryWriteActivity extends AppCompatActivity {
    private Diary todayDiary;

    private TextView diaryDate;
    private EditText titleText;
    private EditText contentText;

    private TextView musicText;
    private ImageView musicImage;

    private boolean musicOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_diary);

        diaryDate = findViewById(R.id.text_view_detail_date);
        titleText = findViewById(R.id.edit_text_detail_title);
        titleText.setFocusableInTouchMode(true);

        contentText = findViewById(R.id.edit_text_detail_content);
        contentText.setFocusableInTouchMode(true);

        musicText = findViewById(R.id.text_view_detail_music);
        musicImage = findViewById(R.id.image_view_detail_music);

        FloatingActionButton backButton = findViewById(R.id.floating_back_button);
        backButton.setEnabled(true);
        backButton.setClickable(true);
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        TextView emptySpaceView = findViewById(R.id.text_view_detail_empty_space);
        emptySpaceView.setOnClickListener(v -> {
            contentText.requestFocus();
        });

        musicText.setVisibility(View.VISIBLE);
        musicImage.setVisibility(View.VISIBLE);
        View.OnClickListener musicClick = v -> {
            if (musicOn) {
                musicOn = false;
                musicText.setText("MUSIC ON");
                musicText.setTextColor(getResources().getColor(R.color.colorPrimary));

            } else {
                musicOn = true;
                musicText.setText("MUSIC OFF");
                musicText.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        };
        musicText.setOnClickListener(musicClick);
        musicImage.setOnClickListener(musicClick);

        // 오늘의 일기 생성
        DiaryDao diaryDao = new DiaryDao(this);
        String today_yyyyMMdd = DateUtil.getyyyyMMdd();
        todayDiary = diaryDao.getDiary(today_yyyyMMdd);
        if (todayDiary == null) {
            Intent intent = getIntent();
            String title = intent.getStringExtra("title");

            todayDiary = new Diary();
            todayDiary.setDiaryDate(today_yyyyMMdd);
            todayDiary.setTitle(!TextUtils.isEmpty(title) ? title : "");
            todayDiary.setContent("");
            todayDiary.setAuthorDiaryStatus(Diary.DiaryStatus.UNSAVED);
            todayDiary.setAccountDiaryStatus(Diary.DiaryStatus.UNSAVED);
            diaryDao.insertDiary(todayDiary);
        }
        String diaryDateString = DateUtil.getDateStringSkipTime();

        diaryDate.setText(diaryDateString);
        titleText.setText(todayDiary.getTitle());
        contentText.setText(todayDiary.getContent());

        contentText.requestFocus();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
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
