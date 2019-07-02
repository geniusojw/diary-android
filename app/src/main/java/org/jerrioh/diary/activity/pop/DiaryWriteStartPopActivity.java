package org.jerrioh.diary.activity.pop;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.main.DiaryWriteActivity;
import org.jerrioh.diary.util.DateUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DiaryWriteStartPopActivity extends CustomPopActivity {

    private TextView todayView;
    private TextView tipTextView;

    private EditText titleEditView;
    private TextView nextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_diary_start);

        super.setWindowAttribute(0.95f, 0.4f);

        todayView = findViewById(R.id.text_view_diary_start_today);
        tipTextView = findViewById(R.id.text_view_diary_detail_start_tip);

        titleEditView = findViewById(R.id.edit_text_diary_detail_start_title);
        nextView = findViewById(R.id.text_view_diary_detail_start_next);

        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.HOUR_OF_DAY, 0);

        Date date = now.getTime();
        long timeLeft = TimeUnit.DAYS.toMillis(1) + date.getTime() - System.currentTimeMillis();

        todayView.setText(DateUtil.getDateStringSkipTime());
        tipTextView.setText("오늘의 일기를 작성할 수 있는 시간이\n" + DateUtil.getTimeString(timeLeft) + " 남았습니다.");

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
