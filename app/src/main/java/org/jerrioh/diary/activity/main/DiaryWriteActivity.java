package org.jerrioh.diary.activity.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.LinearGradient;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.AuthorDiaryApis;
import org.jerrioh.diary.model.DiaryGroup;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.model.db.DiaryDao;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.model.db.DiaryGroupDao;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.PropertyUtil;
import org.jerrioh.diary.util.ReceiverUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class DiaryWriteActivity extends AbstractDetailActivity {
    private static final String TAG = "DiaryWriteActivity";

    private Diary todayDiary;

    private LinearLayout diaryDetail;
    private TextView diaryDate;
    private EditText titleText;
    private EditText contentText;
    private TextView shareText;

    private String originalTitle;
    private String originalContent;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks whether a hardware keyboard is available
//        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
//            Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();
//        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
//            Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_diary);

        diaryDetail = findViewById(R.id.linear_layout_detail_diary);
        diaryDate = findViewById(R.id.text_view_detail_date);
        titleText = findViewById(R.id.edit_text_detail_title);
        titleText.setFocusableInTouchMode(true);

        diaryDetail.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        diaryDate.setTextColor(getResources().getColor(R.color.colorWhite));
        titleText.setTextColor(getResources().getColor(R.color.colorWhite));
        titleText.setHintTextColor(getResources().getColor(R.color.colorWhite));

        ImageView moreImage = findViewById(R.id.image_view_more);
        moreImage.setImageResource(R.drawable.ic_more_vert_white_24dp);

        contentText = findViewById(R.id.edit_text_detail_content);
        contentText.setFocusableInTouchMode(true);
        contentText.setHint(getResources().getString(R.string.diary_write_here));

        DiaryGroupDao diaryGroupDao = new DiaryGroupDao(this);
        DiaryGroup diaryGroup = diaryGroupDao.getDiaryGroup();
        if (diaryGroup != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime > diaryGroup.getStartTime() && currentTime < diaryGroup.getEndTime()) {
                shareText = findViewById(R.id.text_view_detail_share_diary_group);
                shareText.setVisibility(View.VISIBLE);
            }
        };

        TextView emptySpaceView = findViewById(R.id.text_view_detail_empty_space);
        emptySpaceView.setOnClickListener(v -> {
            contentText.requestFocus();
        });

        FloatingActionButton backButton = findViewById(R.id.floating_back_button);
        backButton.setEnabled(true);
        backButton.setClickable(true);
        backButton.setOnClickListener(v -> {
//            softKeyboard.openSoftKeyboard();
            onBackPressed();
        });

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

            originalTitle = "";
            originalContent = "";
        } else {
            originalTitle = todayDiary.getTitle();
            originalContent = todayDiary.getContent();
        }
        String diaryDateString = DateUtil.getDateStringSkipTime() + DateUtil.getTodayMark();

        diaryDate.setText(diaryDateString);
        titleText.setText(todayDiary.getTitle());
        contentText.setText(todayDiary.getContent());

        super.setUpMoreOptionsPost(contentText, true, true, diaryDate.getText().toString());
        super.setUpTransparentFloatingButton(contentText, Arrays.asList(backButton));

        contentText.requestFocus();
        contentText.setSelection(contentText.getText().length());

        // super.setUpSoftKeyboard(R.id.relative_layout_detail_diary_main, Arrays.asList(backButton));
        // softKeyboard thread가 완전히 준비가 되기전에 키보드를 오픈하면 정상적으로 동작하지 않는듯하다. 0.5초 딜레이
        // new Handler().postDelayed(new Runnable() { @Override public void run() { softKeyboard.openSoftKeyboard(); } }, 500);
    }

    @Override
    public void onBackPressed() {
        if (!titleText.getText().toString().equals(originalTitle) || !contentText.getText().toString().equals(originalContent)) {
            Toast.makeText(this, getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
            saveDiary();
        }

        // receiver 등록
        String value = PropertyUtil.getProperty(Property.Key.YESTERDAY_RECEIVER_ON, this);
        if (Integer.parseInt(value) == 0) {
            ReceiverUtil.setDiaryReceiverOn(this);
            PropertyUtil.setProperty(Property.Key.YESTERDAY_RECEIVER_ON, "1", this);
        }

        super.onBackPressed();
    }

    private void saveDiary() {
        todayDiary.setTitle(titleText.getText().toString());
        todayDiary.setContent(contentText.getText().toString());

        DiaryDao diaryDao = new DiaryDao(this);
        diaryDao.updateDiaryContent(todayDiary.getDiaryDate(), todayDiary.getTitle(), todayDiary.getContent());

        // 오늘의 일기 업로드(일기 상태를 변경하지 않으므로 저장할 때마다 업로드 가능)
        DiaryGroupDao diaryGroupDao = new DiaryGroupDao(this);
        DiaryGroup diaryGroup = diaryGroupDao.getDiaryGroup();
        if (diaryGroup != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime > diaryGroup.getStartTime() && currentTime < diaryGroup.getEndTime()) {
                AuthorDiaryApis authorDiaryApis = new AuthorDiaryApis(this);
                authorDiaryApis.write(todayDiary.getDiaryDate(), todayDiary.getTitle(), todayDiary.getContent(), new ApiCallback() {
                    @Override
                    protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                        Log.d(TAG, "diary write result (group) = " + httpStatus);
                    }
                });
            }
        }
    }


}
