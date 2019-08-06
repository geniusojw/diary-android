package org.jerrioh.diary.activity.pop;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.main.DiaryWriteActivity;
import org.jerrioh.diary.model.DiaryGroup;
import org.jerrioh.diary.model.db.DiaryGroupDao;
import org.jerrioh.diary.util.DateUtil;

public class DiaryWriteStartPopActivity extends AbstractDiaryPopActivity {

    private TextView todayView;
    private TextView topicView;
    private TextView tipTextView;

    private EditText titleEditView;
    private TextView nextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_diary_start);

        super.setWindowAttribute(0.95f, 0.4f, 0, -120);

        todayView = findViewById(R.id.text_view_diary_start_today);
        topicView = findViewById(R.id.text_view_diary_start_topic);
        tipTextView = findViewById(R.id.text_view_diary_detail_start_tip);

        titleEditView = findViewById(R.id.edit_text_diary_detail_start_title);
        nextView = findViewById(R.id.text_view_diary_detail_start_next);

        titleEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkTitleText();
            }
        });

        DiaryGroupDao diaryGroupDao = new DiaryGroupDao(this);
        DiaryGroup diaryGroup = diaryGroupDao.getDiaryGroup();

        String topicText;
        long currentTime = System.currentTimeMillis();
        if (diaryGroup != null
                && currentTime > diaryGroup.getStartTime()
                && currentTime < diaryGroup.getEndTime()
                && diaryGroup.getCurrentAuthorCount() > 1) {
            topicText = getResources().getString(R.string.diary_start_topic_group, diaryGroup.getCurrentAuthorCount() - 1);
        } else {
            topicText = getResources().getString(R.string.diary_start_topic);
        }


        long timeLeft = DateUtil.getTimeLeft();
        todayView.setText(DateUtil.getDateStringSkipTime());
        topicView.setText(topicText);
        tipTextView.setText(getResources().getString(R.string.diary_today_remain_time_new_line, DateUtil.getTimeString(timeLeft)));

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

    private void checkTitleText() {
        String text;
        int colorId;
        if (TextUtils.isEmpty(titleEditView.getText())) {
            text = "SKIP";
            colorId = R.color.colorIndicatorText;
        } else {
            text = "START DIARY";
            colorId = R.color.colorAccent;
        }

        nextView.setText(text);
        nextView.setTextColor(getResources().getColor(colorId));
    }
}
