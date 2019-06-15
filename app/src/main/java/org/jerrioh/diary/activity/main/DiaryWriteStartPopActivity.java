package org.jerrioh.diary.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.model.db.DiaryDao;
import org.jerrioh.diary.util.DateUtil;

public class DiaryWriteStartPopActivity extends CustomPopActivity {

    private EditText titleEditView;
    private TextView tipTextView;
    private LinearLayout nextLinearLayout;
    private TextView nextView;

    private Diary todayDiary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_diary_start);

        titleEditView = findViewById(R.id.edit_text_diary_detail_start_title);
        tipTextView = findViewById(R.id.text_view_diary_detail_start_tip);
        nextLinearLayout = findViewById(R.id.linear_layout_diary_detail_start_next);
        nextView = findViewById(R.id.text_view_diary_detail_start_next);

        DiaryDao diaryDao = new DiaryDao(this);
        String today_yyyyMMdd = DateUtil.getyyyyMMdd();
        todayDiary = diaryDao.getDiary(today_yyyyMMdd);
        if (todayDiary != null) {
            startDiaryWriteActivity();
        }

        super.setWindowAttribute(0.95f, 0.4f);

        nextView.setOnClickListener(v -> {
            startDiaryWriteActivity();
        });
    }

    private void startDiaryWriteActivity() {
        Intent intent = new Intent(this, DiaryWriteActivity.class);
        intent.putExtra("title", titleEditView.getText().toString());
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        String text;
        int colorId;
        if (TextUtils.isEmpty(titleEditView.getText())) {
            text = "SKIP";
            colorId = R.color.colorPrimary;
        } else {
            text = "START DIARY";
            colorId = R.color.colorAccent;
        }

        nextView.setText(text);
        nextView.setTextColor(getResources().getColor(colorId));

        return super.onKeyUp(keyCode, event);
    }
}