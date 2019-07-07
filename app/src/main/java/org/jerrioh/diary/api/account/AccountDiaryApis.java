package org.jerrioh.diary.api.account;

import android.content.Context;
import android.util.Log;

import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.ApiCaller;
import org.jerrioh.diary.model.Diary;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import javax.security.auth.callback.Callback;

public class AccountDiaryApis extends ApiCaller {
    private static final String TAG = "AccountDiaryApis";

    public AccountDiaryApis(Context context) {
        super(context);
    }

    public void synchronize(List<Diary> diaries, ApiCallback callback) {
        Map<String, String> headers = accountHeaders();

        try {
            JSONArray jsonArray = new JSONArray();
            for (Diary diary : diaries) {
                JSONObject json = new JSONObject();
                json.put("diaryDate", diary.getDiaryDate());
                json.put("title", diary.getTitle());
                json.put("content", diary.getContent());
                jsonArray.put(json);
            }
            super.post("/account/diaries/synchronize", headers, jsonArray.toString(), callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }

    public void write(Diary diary, ApiCallback callback) {
        // 어제의 일기 저장
        //DiaryDao diaryDao = new DiaryDao(context);
        //String yesterday_yyyyMMdd = DateUtil.getyyyyMMdd(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24));

        Map<String, String> headers = accountHeaders();

        try {
            JSONObject json = new JSONObject();
            json.put("diaryDate", diary.getDiaryDate());
            json.put("title", diary.getTitle());
            json.put("content", diary.getContent());
            super.post("/account/diaries", headers, json.toString(), callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }

    public void deleteDiary(String diaryDate, ApiCallback callback) {
        Map<String, String> headers = accountHeaders();
        JSONObject json = new JSONObject();
        super.delete("/account/diaries/" + diaryDate, headers, json.toString(), callback);
    }
}
