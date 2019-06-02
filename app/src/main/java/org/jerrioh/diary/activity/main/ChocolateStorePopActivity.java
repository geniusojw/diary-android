package org.jerrioh.diary.activity.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.draw.ChocolateStoreActivity;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.AuthorStoreApis;
import org.jerrioh.diary.api.author.DiaryGroupApis;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.model.DiaryGroup;
import org.jerrioh.diary.model.db.DiaryGroupDao;
import org.jerrioh.diary.util.DateUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChocolateStorePopActivity extends Activity {
    private static final String TAG = "ChocolateStorePopActivity";

    private ApiCallback callback = new ApiCallback() {
        @Override
        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
            if (httpStatus == 200) {
                Toast.makeText(ChocolateStorePopActivity.this, "success", Toast.LENGTH_LONG).show();
            } else if (httpStatus == 402) {
                Toast.makeText(ChocolateStorePopActivity.this, "not enough chocolates", Toast.LENGTH_LONG).show();
            }
            finish();
        }
    };

    private ApiCallback inviteCallback = new ApiCallback() {
        @Override
        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
            if (httpStatus == 200) {
                JSONObject data = jsonObject.getJSONObject("data");
                saveDiaryGroup(data);

            } else if (httpStatus == 402) {
                Toast.makeText(ChocolateStorePopActivity.this, "not enough chocolates", Toast.LENGTH_LONG).show();
            } else if (httpStatus == 409) {
                Toast.makeText(ChocolateStorePopActivity.this, "이미 그룹이 있다.", Toast.LENGTH_LONG).show();
                DiaryGroupApis diaryGroupApis = new DiaryGroupApis(ChocolateStorePopActivity.this);
                diaryGroupApis.getDiaryGroup(new ApiCallback() {
                    @Override
                    protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                        if (httpStatus == 200) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            saveDiaryGroup(data);
                        }
                    }
                });
            }
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chocolate_store_pop);

        setWindowAttribute();

        AuthorStoreApis authorStoreApis = new AuthorStoreApis(this);

        Intent intent = getIntent();
        int item = intent.getIntExtra("item", -1);

        String descriptionText = "";
        View.OnClickListener okClickListener = null;
        if (item == ChocolateStoreActivity.CHOCOLATE_STORE_ITEM_CHANGE_DESCRIPTION) {
            descriptionText = "1초코릿 내시겟어요?";
            okClickListener = v -> {
                authorStoreApis.changeDescription(callback);
            };
        } else if (item == ChocolateStoreActivity.CHOCOLATE_STORE_ITEM_CHANGE_NICKNAME) {
            descriptionText = "닉네임 돌려줘?";
            okClickListener = v -> {
                authorStoreApis.changeNickname(callback);
            };
        } else if (item == ChocolateStoreActivity.CHOCOLATE_STORE_ITEM_ALIAS_FEATURE_UNLIMITED_USE) {
            descriptionText = "살겁니까?";
            okClickListener = v -> {
                authorStoreApis.aliasFeatureUnlimitedUse(callback);
            };
        } else if (item == ChocolateStoreActivity.CHOCOLATE_STORE_ITEM_INVITE1) {
            descriptionText = "초대1를 하시겠습니까?";
            okClickListener = v -> {
                authorStoreApis.inviteTicket1(inviteCallback);
            };
        } else if (item == ChocolateStoreActivity.CHOCOLATE_STORE_ITEM_INVITE2) {
            descriptionText = "초대2를 하시겠습니까?";
            okClickListener = v -> {
                authorStoreApis.inviteTicket2(inviteCallback);
            };
        } else if (item == ChocolateStoreActivity.CHOCOLATE_STORE_ITEM_DONATE) {
            descriptionText = "쪼꼬렛 기부를 하시겠습니까?";
            okClickListener = v -> {
                //authorStoreApis.donate(callback);
                finish();
            };
        }

        TextView popDescriptionView = findViewById(R.id.text_view_chocolate_pop_description);
        popDescriptionView.setText(descriptionText);

        Button okButton = findViewById(R.id.button_chocolate_pop_ok);
        Button noButton = findViewById(R.id.button_chocolate_pop_no);

        okButton.setOnClickListener(okClickListener);
        noButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void setWindowAttribute() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        getWindow().setLayout((int)(width*.95), (int)(height*.5));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -50;

        getWindow().setAttributes(params);

    }

    private void saveDiaryGroup(JSONObject data) throws JSONException {
        DiaryGroup diaryGroup = new DiaryGroup();
        diaryGroup.setDiaryGroupId(data.getLong("diaryGroupId"));
        diaryGroup.setDiaryGroupName(data.getString("diaryGroupName"));
        diaryGroup.setKeyword(data.getString("keyword"));
        diaryGroup.setCountry(data.getString("country"));
        diaryGroup.setLanguage(data.getString("language"));
        diaryGroup.setTimeZoneId(data.getString("timeZoneId"));
        diaryGroup.setStartTime(data.getLong("startTime"));
        diaryGroup.setEndTime(data.getLong("endTime"));

        DiaryGroupDao diaryGroupDao = new DiaryGroupDao(this);
        if (diaryGroupDao.getDiaryGroup() == null) {
            diaryGroupDao.insertDiaryGroup(diaryGroup);
        } else {
            diaryGroupDao.updateDiaryGroup(diaryGroup);
        }
    }
}
