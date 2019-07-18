package org.jerrioh.diary.api.author;

import android.content.Context;
import android.util.Log;

import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.ApiCaller;
import org.jerrioh.diary.model.Diary;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class AuthorDiaryApis extends ApiCaller {
    private static final String TAG = "AuthorDiaryApis";

    public AuthorDiaryApis(Context context) {
        super(context);
    }

    public void write(String diaryDate, String title, String content, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("diaryDate", diaryDate);
            json.put("title", title);
            json.put("content", content);

            super.post("/author/diaries", headers, json.toString(), callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }

    public void deleteDiary(String diaryDate, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        JSONObject json = new JSONObject();
        super.delete("/author/diaries/" + diaryDate, headers, json.toString(), callback);
    }
}
