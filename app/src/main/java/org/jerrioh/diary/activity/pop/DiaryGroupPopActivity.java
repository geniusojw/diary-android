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
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.DiaryGroupApis;
import org.jerrioh.diary.model.DiaryGroup;
import org.jerrioh.diary.model.db.DiaryGroupDao;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.DateUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class DiaryGroupPopActivity extends CustomPopActivity {
    private static final String TAG = "DiaryGroupPopActivity";

    private JSONObject diaryGroupJsonObject;
    private JSONArray diaryGroupAuthorJsonArray;
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
                                diaryGroupAuthorJsonArray = jsonObject.getJSONArray("data");
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
        if (diaryGroupJsonObject == null || diaryGroupAuthorJsonArray == null) {
            Log.e(TAG, "no result. diaryGroupJsonObject=" + diaryGroupJsonObject + ", diaryGroupAuthorJsonArray=" + diaryGroupAuthorJsonArray);
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
            groupNameView.setText(diaryGroupName + " (" + diaryGroupAuthorJsonArray.length() + "인)");

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
        TextView currentAuthorIndexView = findViewById(R.id.text_view_diary_group_body_part1_current_author_index);
        currentAuthorIndexView.setText((currentAuthorIndex + 1)+ " 번째 사람");

        String authorId;
        String nickname;
        String title;
        String content;
        try {
            JSONObject authorJson = diaryGroupAuthorJsonArray.getJSONObject(currentAuthorIndex);
            authorId = authorJson.getString("authorId");
            nickname = authorJson.getString("nickname");
            title = authorJson.getString("title");
            content = authorJson.getString("content");

        } catch (JSONException e) {
            Log.e(TAG, "JSONException. " + e.toString());
            finish();
            return;
        }

        TextView currentAuthorView = findViewById(R.id.text_view_diary_group_body_part1_current_author);
        currentAuthorView.setText(nickname);

        ImageView readDiaryImageView = findViewById(R.id.image_view_diary_group_body_part2);
        TextView readDiaryTextView = findViewById(R.id.text_view_diary_group_body_part2);

        int drawableId;
        String readText;
        boolean readClickable;
        View.OnClickListener setOnClickListener;

        if (TextUtils.isEmpty(content) || "null".equals(content)) {
            drawableId = R.drawable.ic_hotel_black_24dp;
            readText = nickname + "님은 어제 일기를 쓰지 않았다."; // TODO 안쓴 것과 아직 업로드 안한 것을 구분
            readClickable = false;
            setOnClickListener = null;
        } else {
            drawableId = R.drawable.ic_chat_black_24dp;
            readText = nickname + "님의 일기 보기";
            readClickable = true;
            setOnClickListener = v -> {
                Intent intent = new Intent(this, DiaryGroupReadActivity.class);
                intent.putExtra("nickname", nickname);
                intent.putExtra("title", title);
                intent.putExtra("content", content);
                startActivity(intent);
            };
        }
        readDiaryImageView.setImageResource(drawableId);
        readDiaryTextView.setText(readText);
        readDiaryTextView.setClickable(readClickable);
        readDiaryTextView.setOnClickListener(setOnClickListener);
        readDiaryImageView.setOnClickListener(setOnClickListener);

        int previousAuthorIndex = currentAuthorIndex - 1 >= 0 ? currentAuthorIndex - 1 : diaryGroupAuthorJsonArray.length() - 1;
        int nextAuthorIndex = currentAuthorIndex + 1 < diaryGroupAuthorJsonArray.length() ? currentAuthorIndex + 1 : 0;

        try {
            JSONObject previousAuthorJson = diaryGroupAuthorJsonArray.getJSONObject(previousAuthorIndex);
            TextView previousAuthorView = findViewById(R.id.text_view_diary_group_previous_author);
            String previousNickname = previousAuthorJson.getString("nickname");
            String previousNicknameVertical = "";
            for (char ch : previousNickname.toCharArray()) {
                if (ch != ' ') previousNicknameVertical += ("\n" + ch);
            }
            previousAuthorView.setText(previousNicknameVertical);

            JSONObject nextAuthorJson = diaryGroupAuthorJsonArray.getJSONObject(nextAuthorIndex);
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
