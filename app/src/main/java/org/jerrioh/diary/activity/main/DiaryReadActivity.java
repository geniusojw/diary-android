package org.jerrioh.diary.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.jerrioh.diary.config.Constants;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.R;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.CommonUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DiaryReadActivity extends AbstractDetailActivity {
    private static final String TAG = "DiaryReadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_diary);

        Intent intent = getIntent();
        Diary diary = (Diary) intent.getSerializableExtra("diary");

        TextView dateTextView = findViewById(R.id.text_view_detail_date);
        String diaryDate = DateUtil.getDateString_yyyyMMdd(diary.getDiaryDate());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            Date beginDate = simpleDateFormat.parse(diary.getDiaryDate());
            Date endDate = simpleDateFormat.parse(DateUtil.getyyyyMMdd());
            long diff = endDate.getTime() - beginDate.getTime();
            long diffDays = diff / (TimeUnit.DAYS.toMillis(1));

            diaryDate += " (D+" + diffDays + ")";

        } catch (ParseException e) {
            Log.e(TAG, "date parse exception");
        }

        dateTextView.setText(diaryDate);

        EditText titleEditText = findViewById(R.id.edit_text_detail_title);
        titleEditText.setText(CommonUtil.defaultIfEmpty(diary.getTitle(), Constants.DEFAULT_TITLE));

        EditText contentEditText = findViewById(R.id.edit_text_detail_content);
        contentEditText.setText(diary.getContent());

        View backButton = findViewById(R.id.floating_back_button);
        backButton.setEnabled(true);
        backButton.setClickable(true);
        backButton.setOnClickListener(v -> {
            finish();
        });

        ImageView moreLayout = findViewById(R.id.image_view_more);
        moreLayout.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.detail_menu_diary_write, popup.getMenu());
            popup.show();
        });

        super.setUpMoreOptionsPost(contentEditText, true, false, diary.getDiaryDate());

//        TextView adjustText = findViewById(R.id.text_view_detail_diary_font_size_adjust);
//        super.setUpFontMusicButton(contentEditText, adjustText, null);
    }
}
