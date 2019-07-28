package org.jerrioh.diary.activity.pop;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.main.DiaryGroupReadActivity;
import org.jerrioh.diary.activity.main.LetterWriteActivity;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.DiaryGroupApis;
import org.jerrioh.diary.model.DiaryGroup;
import org.jerrioh.diary.model.db.DiaryGroupDao;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.CommonUtil;
import org.jerrioh.diary.util.DateUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class DiaryGroupPopActivity extends CustomPopActivity {
    private static final String TAG = "DiaryGroupPopActivity";

    private JSONObject diaryGroupJsonObject;
    private JSONArray diaryGroupAuthorDiaries;
    private int currentAuthorIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_group_pop);

        super.setWindowAttribute(.95f, .9f);
        setDiaryGroupContent();
    }

    private void setDiaryGroupContent() {
        // 유효한 다이어리 그룹이 없을 경우 팝업 종료
        DiaryGroupDao diaryGroupDao = new DiaryGroupDao(this);
        DiaryGroup diaryGroup = diaryGroupDao.getDiaryGroup();
        if (diaryGroup == null || System.currentTimeMillis() > diaryGroup.getEndTime()) {
            finish();
            return;
        }

        // 돌아가기 버튼 세팅
        LinearLayout closeLayout = findViewById(R.id.linear_layout_diary_group_close);
        closeLayout.setOnClickListener(v -> {
            finish();
        });

        // api 호출하여 그룹일기 정보 갱신 및 세팅
        DiaryGroupApis diaryGroupApis = new DiaryGroupApis(this);
        diaryGroupApis.getDiaryGroup(new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                if (httpStatus == 200) {
                    diaryGroupJsonObject = jsonObject.getJSONObject("data");
                    AuthorUtil.saveDiaryGroup(diaryGroupJsonObject, DiaryGroupPopActivity.this);

                    diaryGroupApis.readYesterdayDiaries(new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                diaryGroupAuthorDiaries = jsonObject.getJSONArray("data");
                                setDiaryGroupContentByApiResult();

                            } else if (httpStatus == 404) {
                                diaryGroupDao.deleteDiaryGroup();
                                finish();
                            } else {
                                finish();
                            }
                        }
                    });

                } else if (httpStatus == 404) {
                    diaryGroupDao.deleteDiaryGroup();
                    finish();
                } else {
                    Toast.makeText(DiaryGroupPopActivity.this, DiaryGroupPopActivity.this.getResources().getString(R.string.network_fail), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void setDiaryGroupContentByApiResult() {
        if (diaryGroupJsonObject == null || diaryGroupAuthorDiaries == null) {
            Log.e(TAG, "no result. diaryGroupJsonObject=" + diaryGroupJsonObject + ", diaryGroupAuthorDiaries=" + diaryGroupAuthorDiaries);
            finish();
            return;
        }

        LinearLayout optionView1 = findViewById(R.id.linear_layout_diary_group_body_options1);
        LinearLayout optionView2 = findViewById(R.id.linear_layout_diary_group_body_options2);
        ProgressBar progressBar = findViewById(R.id.progress_bar_diary_group_pop);

        optionView1.setVisibility(View.VISIBLE);
        optionView2.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        try {
            String diaryGroupName = diaryGroupJsonObject.getString("diaryGroupName");
            String hostAuthorId = diaryGroupJsonObject.getString("hostAuthorId");
            String keyword = diaryGroupJsonObject.getString("keyword");
            int currentAuthorCount = diaryGroupJsonObject.getInt("currentAuthorCount");
            int maxAuthorCount = diaryGroupJsonObject.getInt("maxAuthorCount");
            String country = diaryGroupJsonObject.getString("country");
            String language = diaryGroupJsonObject.getString("language");
            String timeZoneId = diaryGroupJsonObject.getString("timeZoneId");
            long startTime = diaryGroupJsonObject.getLong("startTime");
            long endTime = diaryGroupJsonObject.getLong("endTime");

            // group 정보
            TextView groupNameView = findViewById(R.id.text_view_diary_group_header_group_name);
            if ("null".equals(diaryGroupName) || TextUtils.isEmpty(diaryGroupName)) {
                diaryGroupName = getResources().getString(R.string.group_started_no_group_name);
            }
            groupNameView.setText(diaryGroupName);

            String period = DateUtil.getDateStringSkipYear(startTime) + " ~ " + DateUtil.getDateStringSkipYear(endTime - TimeUnit.MINUTES.toMillis(1));
            TextView periodView = findViewById(R.id.text_view_diary_group_header_group_period);
            periodView.setText(period);

            TextView groupCountView = findViewById(R.id.text_view_diary_group_header_group_poeple_count);
            groupCountView.setText("(" + getResources().getString(R.string.group_people_count, currentAuthorCount) + ")");

            TextView moreInfoView = findViewById(R.id.text_view_diary_group_header_group_keyword);

            String keywordText = "";
            if (!TextUtils.isEmpty(keyword) && !"null".equals(keyword)) {
                keywordText = "[ " + keyword + " ]";
            }
            moreInfoView.setText(keywordText);

            // author 참석자 일기 정보
            setAuthorInformation();

        } catch (JSONException e) {
            Log.e(TAG, "JSONException. " + e.toString());
            finish();
            return;
        }
    }

    private void setAuthorInformation() {

        JSONObject authorGroupDiary;

        String authorId;
        String nickname;
        boolean isFirstDay;
        String todayDate;
        String todayTitle;
        String todayContent;
        String yesterdayDate;
        String yesterdayTitle;
        String yesterdayContent;
        try {
            authorGroupDiary = diaryGroupAuthorDiaries.getJSONObject(currentAuthorIndex);

            authorId = authorGroupDiary.getString("authorId");
            nickname = authorGroupDiary.getString("nickname");
            isFirstDay = authorGroupDiary.getBoolean("firstDay");
            todayDate = authorGroupDiary.getString("todayDate");
            todayTitle = authorGroupDiary.getString("todayTitle");
            todayContent = authorGroupDiary.getString("todayContent");
            yesterdayDate = authorGroupDiary.getString("yesterdayDate");
            yesterdayTitle = authorGroupDiary.getString("yesterdayTitle");
            yesterdayContent = authorGroupDiary.getString("yesterdayContent");

        } catch (JSONException e) {
            Log.e(TAG, "JSONException. " + e.toString());
            finish();
            return;
        }

        TextView currentAuthorIndexView = findViewById(R.id.text_view_diary_group_body_author_index);
        TextView currentAuthorView = findViewById(R.id.text_view_diary_group_body_author);
        currentAuthorIndexView.setText(CommonUtil.ordinalPeople(currentAuthorIndex + 1));
        currentAuthorView.setText(nickname);

        ImageView yesterdayImageView = findViewById(R.id.image_view_diary_group_body_yesterday);
        ImageView todayImageView = findViewById(R.id.image_view_diary_group_body_today);

        ImageView feedbackImageView = findViewById(R.id.image_view_diary_group_body_feedback);
        ImageView letterImageView = findViewById(R.id.image_view_diary_group_body_letter);

        // 옵션 메뉴 (오늘, 어제 일기, 피드백, 편지)

        LinearLayout yesterdayLinearLayout = findViewById(R.id.linear_layout_diary_group_body_yesterday);
        LinearLayout feedbackLinearLayout = findViewById(R.id.linear_layout_diary_group_body_feedback);
        LinearLayout letterLinearLayout = findViewById(R.id.linear_layout_diary_group_body_letter);

        this.setDiaryReadIntent(true, authorId, nickname, todayDate, todayTitle, todayContent, todayImageView);

        if (isFirstDay) {
            yesterdayLinearLayout.setVisibility(View.GONE);
        } else {
            yesterdayLinearLayout.setVisibility(View.VISIBLE);
            this.setDiaryReadIntent(false, authorId, nickname, yesterdayDate, yesterdayTitle, yesterdayContent, yesterdayImageView);
        }

        String myAuthorId = AuthorUtil.getAuthor(this).getAuthorId();
        boolean myDiary = authorId.equals(myAuthorId);
        if (myDiary) {
            letterLinearLayout.setVisibility(View.GONE);
        } else {
            letterLinearLayout.setVisibility(View.VISIBLE);
            this.setLetterIntent(authorId, nickname, letterImageView);
        }

        if (isFirstDay || myDiary) {
            feedbackLinearLayout.setVisibility(View.GONE);
        } else {
            feedbackLinearLayout.setVisibility(View.VISIBLE);
            this.setFeedbackIntent(authorId, nickname, feedbackImageView);
        }

        int previousAuthorIndex = currentAuthorIndex - 1 >= 0 ? currentAuthorIndex - 1 : diaryGroupAuthorDiaries.length() - 1;
        int nextAuthorIndex = currentAuthorIndex + 1 < diaryGroupAuthorDiaries.length() ? currentAuthorIndex + 1 : 0;

        try {
            JSONObject previousAuthorJson = diaryGroupAuthorDiaries.getJSONObject(previousAuthorIndex);
            TextView previousAuthorView = findViewById(R.id.text_view_diary_group_previous_author);
            String previousNickname = previousAuthorJson.getString("nickname");
            String previousNicknameVertical = "";
            for (char ch : previousNickname.toCharArray()) {
                if (ch != ' ') previousNicknameVertical += ("\n" + ch);
            }
            previousAuthorView.setText(previousNicknameVertical);

            JSONObject nextAuthorJson = diaryGroupAuthorDiaries.getJSONObject(nextAuthorIndex);
            TextView nextAuthorView = findViewById(R.id.text_view_diary_group_next_author);
            String nextNickname = nextAuthorJson.getString("nickname");
            String nextNicknameVertical = "";
            for (char ch : nextNickname.toCharArray()) {
                if (ch != ' ') nextNicknameVertical += ("\n" + ch);
            }
            nextAuthorView.setText(nextNicknameVertical);

        } catch (JSONException e) {
            Log.e(TAG, "JSONException. " + e.toString());
            finish();
            return;
        }

        // 좌측과 우측 AUTHOR
        LinearLayout previousLayout = findViewById(R.id.linear_layout_diary_group_previous_author);
        previousLayout.setClickable(true);
        previousLayout.setOnClickListener(v -> {
            currentAuthorIndex = previousAuthorIndex;
            setAuthorInformation();
        });

        LinearLayout nextLayout = findViewById(R.id.linear_layout_diary_group_next_author);
        nextLayout.setClickable(true);
        nextLayout.setOnClickListener(v -> {
            currentAuthorIndex = nextAuthorIndex;
            setAuthorInformation();
        });
    }

    private void setDiaryReadIntent(boolean today, String authorId, String nickname, String diaryDate, String title, String content, ImageView imageButtonView) {
        if ((TextUtils.isEmpty(diaryDate) || "null".equals(diaryDate))) {
            imageButtonView.setImageResource(R.drawable.ic_chat_bubble_black_24dp);
            imageButtonView.setOnClickListener(v -> {
                Toast.makeText(this, getResources().getString(R.string.group_diary_not_written), Toast.LENGTH_SHORT).show();
            });

        } else {
            imageButtonView.setImageResource(R.drawable.ic_chat_black_24dp);
            imageButtonView.setOnClickListener(v -> {
                Intent intent = new Intent(this, DiaryGroupReadActivity.class);
                intent.putExtra("today", today);
                intent.putExtra("authorId", authorId);
                intent.putExtra("nickname", nickname);
                intent.putExtra("diaryDate", diaryDate);
                intent.putExtra("title", title);
                intent.putExtra("content", content);
                startActivity(intent);
            });
        }
    }

    private void setFeedbackIntent(String authorId, String nickname, ImageView feedbackImageView) {
        feedbackImageView.setOnClickListener(v -> {
            Intent intent = new Intent(this, DiaryGroupFeedbackActivity.class);
            intent.putExtra("authorId", authorId);
            intent.putExtra("nickname", nickname);
            startActivity(intent);
        });
    }

    private void setLetterIntent(String authorId, String nickname, ImageView letterImageView) {
        letterImageView.setOnClickListener(v -> {
            Intent intent = new Intent(this, LetterWriteActivity.class);
            intent.putExtra("authorId", authorId);
            intent.putExtra("nickname", nickname);
            startActivity(intent);
        });
    }
}
