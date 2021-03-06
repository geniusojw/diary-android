package org.jerrioh.diary.activity.pop;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.AuthorStoreApis;
import org.jerrioh.diary.api.author.DiaryGroupApis;
import org.jerrioh.diary.api.author.FeedbackApis;
import org.jerrioh.diary.model.DiaryGroup;
import org.jerrioh.diary.model.db.DiaryGroupDao;
import org.json.JSONException;
import org.json.JSONObject;

public class SentencePopActivity extends AbstractDiaryPopActivity {

    public static final int TYPE_DIARY_GROUP_KEYWORD = 1;
    public static final int TYPE_DIARY_GROUP_AUTHOR_FEEDBACK = 2;
    public static final int TYPE_STORE_CREATE_WISE_SAYING = 3;

    private static final String TAG = "SentencePopActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence_write_pop);

        super.setWindowAttribute(.95f, .3f, 0, -200);

        Intent intent = getIntent();
        int type = intent.getIntExtra("type", 0);

        TextView titleView = findViewById(R.id.text_view_sentence_title);
        EditText editView = findViewById(R.id.edit_text_sentence_content);
        ImageView okButton = findViewById(R.id.image_view_sentence_content_ok);

        if (type == TYPE_DIARY_GROUP_KEYWORD) {
            DiaryGroupDao diaryGroupDao = new DiaryGroupDao(this);
            DiaryGroup diaryGroup = diaryGroupDao.getDiaryGroup();

            titleView.setText(getResources().getString(R.string.group_keyword));
            editView.setText(diaryGroup.getKeyword());
            editView.setHint(getResources().getString(R.string.group_free_write));
            okButton.setOnClickListener(v -> {
                DiaryGroupApis diaryGroupApis = new DiaryGroupApis(SentencePopActivity.this);
                diaryGroupApis.updateDiaryGroup(editView.getText().toString(), new ApiCallback() {
                    @Override
                    protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                        if (httpStatus == 200) {
                            diaryGroup.setKeyword(editView.getText().toString());
                            diaryGroupDao.updateDiaryGroup(diaryGroup);
                        } else {
                            Log.e(TAG, "sentence write failed");
                        }
                    }
                });
                finish();
            });

        } else if (type == TYPE_DIARY_GROUP_AUTHOR_FEEDBACK) {
            String authorId = intent.getStringExtra("authorId");
            String nickname = intent.getStringExtra("nickname");

            titleView.setText(getResources().getString(R.string.group_who_are_you,  nickname));
            editView.setHint(getResources().getString(R.string.group_honest_write));
            okButton.setOnClickListener(v -> {
                FeedbackApis feedbackApis = new FeedbackApis(this);
                feedbackApis.feedbackAuthor(authorId, 0, editView.getText().toString(), new ApiCallback() {
                    @Override
                    protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                        if (httpStatus == 200) {
                            Toast.makeText(SentencePopActivity.this, getResources().getString(R.string.group_feed_back), Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (httpStatus == 409) {
                            Toast.makeText(SentencePopActivity.this, getResources().getString(R.string.group_feed_back_already), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
                finish();
            });

        } else if (type == TYPE_STORE_CREATE_WISE_SAYING) {
            titleView.setText(getResources().getString(R.string.store_pop_create_wise_saying_title));
            editView.setHint(getResources().getString(R.string.store_pop_create_wise_saying_empty));
            okButton.setOnClickListener(v -> {
                String wiseSaying = editView.getText().toString();

                if (TextUtils.isEmpty(wiseSaying)) {
                    Toast.makeText(SentencePopActivity.this, getResources().getString(R.string.store_pop_create_wise_saying_empty), Toast.LENGTH_SHORT).show();

                } else {
                    AuthorStoreApis authorStoreApis = new AuthorStoreApis(this);
                    authorStoreApis.createWiseSaying(wiseSaying, new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            setResult(httpStatus);
                            finish();
                        }
                    });
                }
            });

        } else {
            finish();
        }


    }
}
