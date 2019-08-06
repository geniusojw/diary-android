package org.jerrioh.diary.activity.pop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.FeedbackApis;
import org.jerrioh.diary.config.Constants;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.CommonUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class DiaryGroupReadActivity extends AbstractDiaryPopActivity {

    public static final int GOOD = 0;
    public static final int LIKE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_diary_detail);
        super.setWindowAttribute(.95f, .9f);

        Intent intent = getIntent();
        boolean today = intent.getBooleanExtra("today", true);
        String authorId = intent.getStringExtra("authorId");
        String nickname = intent.getStringExtra("nickname");
        String diaryDate = intent.getStringExtra("diaryDate");
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");

        TextView nicknameTextView = findViewById(R.id.text_view_group_diary_nickname);

        String toNicknameText;
        if (today) {
            toNicknameText = getResources().getString(R.string.group_today_diary_of_who, nickname);
        } else {
            toNicknameText = getResources().getString(R.string.group_yesterday_diary_of_who, nickname);
        }
        nicknameTextView.setText(toNicknameText);

        EditText titleEditText = findViewById(R.id.edit_text_group_diary_title);
        titleEditText.setText(CommonUtil.defaultIfEmpty(title, Constants.DEFAULT_TITLE));

        EditText contentEditText = findViewById(R.id.edit_text_group_diary_content);
        contentEditText.setText(content);

        LinearLayout goodButton = findViewById(R.id.linear_layout_group_diary_detail_good_button);

        if (!authorId.equals(AuthorUtil.getAuthor(this).getAuthorId())) {
            ImageView goodButtonImage = findViewById(R.id.image_view_group_diary_detail_good_button);
            TextView goodButtonText = findViewById(R.id.text_view_group_diary_detail_good_button);

            int feedbackDiaryType;
            int imageResourceId;
            String goodText;
            String successMessage;
            String alreadyDoneMessage;
            if (today) {
                feedbackDiaryType = LIKE;
                imageResourceId = R.drawable.ic_favorite_black_24dp;
                goodText = getResources().getString(R.string.group_cheer_up_button);
                successMessage = getResources().getString(R.string.group_cheer_up, nickname);
                alreadyDoneMessage = getResources().getString(R.string.group_cheer_up_already);
            } else {
                feedbackDiaryType = GOOD;
                imageResourceId = R.drawable.ic_thumb_up_black_24dp;
                goodText = getResources().getString(R.string.group_good_diary_button);
                successMessage = getResources().getString(R.string.group_good_diary, nickname);
                alreadyDoneMessage = getResources().getString(R.string.group_good_diary_already);
            }

            goodButtonImage.setImageResource(imageResourceId);
            goodButtonText.setText(goodText);
            goodButton.setVisibility(View.VISIBLE);
            goodButton.setOnClickListener(v -> {
                goodButton.setClickable(false);
                FeedbackApis feedbackApis = new FeedbackApis(this);
                feedbackApis.feedbackDiary(authorId, diaryDate, feedbackDiaryType, new ApiCallback() {
                    @Override
                    protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                        if (httpStatus == 200) {
                            Toast.makeText(DiaryGroupReadActivity.this, successMessage, Toast.LENGTH_SHORT).show();
                        } else if (httpStatus == 409) {
                            Toast.makeText(DiaryGroupReadActivity.this, alreadyDoneMessage, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DiaryGroupReadActivity.this, getResources().getString(R.string.network_fail), Toast.LENGTH_SHORT).show();
                            goodButton.setClickable(true);
                        }
                    }
                });
            });
        }

        LinearLayout backButton = findViewById(R.id.linear_layout_group_diary_detail_back_button);
        backButton.setOnClickListener(v -> {
            finish();
        });

    }
}
