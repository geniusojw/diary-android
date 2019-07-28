package org.jerrioh.diary.activity.pop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.pop.CustomPopActivity;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.FeedbackApis;
import org.jerrioh.diary.config.Constants;
import org.jerrioh.diary.util.CommonUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class DiaryGroupFeedbackActivity extends CustomPopActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_diary_feedback);
        super.setWindowAttribute(.95f, .5f);

        Intent intent = getIntent();
        String authorId = intent.getStringExtra("authorId");
        String nickname = intent.getStringExtra("nickname");

        ProgressBar progressBar = findViewById(R.id.progress_bar_diary_group_feedback_pop);
        LinearLayout linearLayout = findViewById(R.id.linear_layout_diary_group_feedback_pop);

        TextView description = findViewById(R.id.text_view_diary_group_feedback_description);
        TextView type0View = findViewById(R.id.text_view_diary_group_feedback_type0);
        TextView type1View = findViewById(R.id.text_view_diary_group_feedback_type1);
        TextView type2View = findViewById(R.id.text_view_diary_group_feedback_type2);
        TextView typeWrite = findViewById(R.id.text_view_diary_group_feedback_type_write);

        FeedbackApis feedbackApis = new FeedbackApis(this);
        feedbackApis.feedbackAuthorTypes(authorId, new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                if (httpStatus == 200) {
                    progressBar.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);

                    JSONObject data = jsonObject.getJSONObject("data");

                    int type0 = data.getInt("authorType0");
                    int type1 = data.getInt("authorType1");
                    int type2 = data.getInt("authorType2");
                    String description0 = data.getString("authorTypeDescription0");
                    String description1 = data.getString("authorTypeDescription1");
                    String description2 = data.getString("authorTypeDescription2");

                    description.setText(getResources().getString(R.string.group_feed_back_author_description, nickname));
                    type0View.setText(description0);
                    type1View.setText(description1);
                    type2View.setText(description2);

                    type0View.setOnClickListener(v -> { callFeedbackAuthor(authorId, type0); });
                    type1View.setOnClickListener(v -> { callFeedbackAuthor(authorId, type1); });
                    type2View.setOnClickListener(v -> { callFeedbackAuthor(authorId, type2); });

                    typeWrite.setOnClickListener(v -> {
                        Intent intent = new Intent(DiaryGroupFeedbackActivity.this, SentencePopActivity.class);
                        intent.putExtra("type", SentencePopActivity.TYPE_DIARY_GROUP_AUTHOR_FEEDBACK);
                        intent.putExtra("authorId", authorId);
                        intent.putExtra("nickname", nickname);
                        startActivity(intent);
                        finish();
                    });

                } else if (httpStatus == 409) {
                    Toast.makeText(DiaryGroupFeedbackActivity.this, "이미 피드백했습니다.", Toast.LENGTH_SHORT).show();
                    finish();

                } else {
                    Toast.makeText(DiaryGroupFeedbackActivity.this, getResources().getString(R.string.network_fail), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void callFeedbackAuthor(String toAuthorId, int feedbackAuthorType) {
        FeedbackApis feedbackApis = new FeedbackApis(this);
        feedbackApis.feedbackAuthor(toAuthorId, feedbackAuthorType, null, new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                if (httpStatus == 200) {
                    Toast.makeText(DiaryGroupFeedbackActivity.this, "성공했습니다. 굳. 시간도 받았습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (httpStatus == 409) {
                    Toast.makeText(DiaryGroupFeedbackActivity.this, "이미 피드백 하였습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}
