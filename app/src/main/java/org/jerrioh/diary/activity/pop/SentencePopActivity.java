package org.jerrioh.diary.activity.pop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.DiaryGroupApis;
import org.jerrioh.diary.model.DiaryGroup;
import org.jerrioh.diary.model.db.DiaryGroupDao;
import org.json.JSONException;
import org.json.JSONObject;

public class SentencePopActivity extends CustomPopActivity {

    public static final int TYPE_DIARY_GROUP_KEYWORD = 1;
    public static final int TYPE_DIARY_GROUP_AUTHOR_FEEDBACK = 2;

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

            titleView.setText("일기모임 주제");
            editView.setHint(diaryGroup.getKeyword());
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

            // TODO

        } else {
            finish();
        }


    }
}
