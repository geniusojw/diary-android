package org.jerrioh.diary.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.pop.CustomPopActivity;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.FeedbackApis;
import org.jerrioh.diary.config.Constants;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.CommonUtil;
import org.jerrioh.diary.util.DateUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class DiaryGroupReadActivity extends CustomPopActivity {

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
        nicknameTextView.setText(nickname + "님의 어제 일기");

        EditText titleEditText = findViewById(R.id.edit_text_group_diary_title);
        titleEditText.setText(CommonUtil.defaultIfEmpty(title, Constants.DEFAULT_TITLE));

        EditText contentEditText = findViewById(R.id.edit_text_group_diary_content);
        contentEditText.setText(content);

        LinearLayout goodButton = findViewById(R.id.linear_layout_group_diary_detail_good_button);

        if (!authorId.equals(AuthorUtil.getAuthor(this).getAuthorId())) {
            ImageView goodButtonImage = findViewById(R.id.image_view_group_diary_detail_good_button);
            int feedbackDiaryType;
            int imageResourceId;
            if (today) {
                feedbackDiaryType = LIKE;
                imageResourceId = R.drawable.ic_favorite_black_24dp;
            } else {
                feedbackDiaryType = GOOD;
                imageResourceId = R.drawable.ic_favorite_black_24dp;
            }

            goodButtonImage.setImageResource(imageResourceId);
            goodButton.setVisibility(View.VISIBLE);
            goodButton.setOnClickListener(v -> {
                FeedbackApis feedbackApis = new FeedbackApis(this);
                feedbackApis.feedbackDiary(authorId, diaryDate, feedbackDiaryType, new ApiCallback() {
                    @Override
                    protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                        if (httpStatus == 200) {
                            Toast.makeText(DiaryGroupReadActivity.this, "성공", Toast.LENGTH_SHORT).show();
                        } else if (httpStatus == 409) {
                            Toast.makeText(DiaryGroupReadActivity.this, "좋아요", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DiaryGroupReadActivity.this, "실패", Toast.LENGTH_SHORT).show();
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
