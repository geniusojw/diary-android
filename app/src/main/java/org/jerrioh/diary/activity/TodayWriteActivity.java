package org.jerrioh.diary.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import org.jerrioh.diary.R;
import org.jerrioh.diary.config.Information;
import org.jerrioh.diary.db.DbHelper;
import org.jerrioh.diary.db.WritingDao;
import org.jerrioh.diary.dbmodel.Writing;

public class TodayWriteActivity extends AppCompatActivity {
    private Writing todayDiary;
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

        WritingDao writingDao = new WritingDao(this);
        todayDiary = writingDao.getTodayDiary();
        if (todayDiary == null) {
            int writingType = Writing.WritingType.DIARY;
            String writingDate = Information.ClientInformation.TODAY;
            String writer = Information.ClientInformation.USER_ID;
            String reader = Information.ClientInformation.USER_ID;
            todayDiary = new Writing(writingType, writingDate, writer, reader, "", "");
            writingDao.insertWriting(todayDiary);
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

        WritingDao writingDao = new WritingDao(this);
        writingDao.updateTodayDiary(todayDiary);
    }
}
