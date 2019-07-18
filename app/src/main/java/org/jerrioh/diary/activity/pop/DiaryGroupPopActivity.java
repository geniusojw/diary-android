package org.jerrioh.diary.activity.pop;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
            groupNameView.setText(diaryGroupName + " (" + currentAuthorCount + "인)");

            String period = DateUtil.getDateStringSkipYear(startTime) + " ~ " + DateUtil.getDateStringSkipYear(endTime - TimeUnit.MINUTES.toMillis(1));
            TextView periodView = findViewById(R.id.text_view_diary_group_header_group_period);
            periodView.setText(period);

            TextView moreInfoView = findViewById(R.id.text_view_diary_group_header_group_keyword);
            moreInfoView.setText("[ keyword : " + keyword + " ]");

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
        String todayTitle;
        String todayContent;
        String yesterdayTitle;
        String yesterdayContent;
        try {
            authorGroupDiary = diaryGroupAuthorDiaries.getJSONObject(currentAuthorIndex);

            authorId = authorGroupDiary.getString("authorId");
            nickname = authorGroupDiary.getString("nickname");
            todayTitle = authorGroupDiary.getString("todayTitle");
            todayContent = authorGroupDiary.getString("todayContent");
            yesterdayTitle = authorGroupDiary.getString("yesterdayTitle");
            yesterdayContent = authorGroupDiary.getString("yesterdayContent");

        } catch (JSONException e) {
            Log.e(TAG, "JSONException. " + e.toString());
            finish();
            return;
        }

        TextView currentAuthorIndexView = findViewById(R.id.text_view_diary_group_body_author_index);
        TextView currentAuthorView = findViewById(R.id.text_view_diary_group_body_author);
        currentAuthorIndexView.setText((currentAuthorIndex + 1)+ " 번째 사람");
        currentAuthorView.setText(nickname);

        TextView todayTextView = findViewById(R.id.text_view_diary_group_body_today);
        ImageView todayImageView = findViewById(R.id.image_view_diary_group_body_today);

        TextView yesterdayTextView = findViewById(R.id.text_view_diary_group_body_yesterday);
        ImageView yesterdayImageView = findViewById(R.id.image_view_diary_group_body_yesterday);

        TextView letterTextView = findViewById(R.id.text_view_diary_group_body_letter);
        ImageView letterImageView = findViewById(R.id.image_view_diary_group_body_letter);

        if ((TextUtils.isEmpty(todayTitle) || "null".equals(todayTitle))
                && (TextUtils.isEmpty(todayContent) || "null".equals(todayContent))) {
            todayTextView.setText(CommonUtil.randomString("그는 자고 있습니다.",
                    "그는 쿨쿨 자고 있습니다.",
                    "일어나지 못하고 있습니다."));
            todayImageView.setImageResource(R.drawable.ic_hotel_black_24dp);
            todayImageView.setOnClickListener(null);

        } else {
            todayTextView.setText(nickname + "님의 일기 보기");
            todayImageView.setImageResource(R.drawable.ic_chat_black_24dp);
            todayImageView.setOnClickListener(v -> {
                Intent intent = new Intent(this, DiaryGroupReadActivity.class);
                intent.putExtra("nickname", nickname);
                intent.putExtra("title", todayTitle);
                intent.putExtra("content", todayContent);
                startActivity(intent);
            });
        }

        if ((TextUtils.isEmpty(yesterdayTitle) || "null".equals(yesterdayTitle))
                && (TextUtils.isEmpty(yesterdayContent) || "null".equals(yesterdayContent))) {
            yesterdayTextView.setText(CommonUtil.randomString("어제의 일기"));
            yesterdayImageView.setImageResource(R.drawable.ic_hotel_black_24dp);
            yesterdayImageView.setOnClickListener(null);

        } else {
            yesterdayTextView.setText("어제의 일기");
            yesterdayImageView.setImageResource(R.drawable.ic_import_contacts_black_24dp);
            yesterdayImageView.setOnClickListener(v -> {
                Intent intent = new Intent(this, DiaryGroupReadActivity.class);
                intent.putExtra("nickname", nickname);
                intent.putExtra("title", yesterdayTitle);
                intent.putExtra("content", yesterdayContent);
                startActivity(intent);
            });
        }

        letterImageView.setOnClickListener(v -> {
//            Intent intent = new Intent(this, LetterWriteActivity.class);
//            startActivity(intent);
        });

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
}
