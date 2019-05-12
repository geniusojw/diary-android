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

public class AuthorDiaryApis extends ApiCaller {
    private static final String TAG = "AuthorDiaryApis";

    protected AuthorDiaryApis(Context context) {
        super(context);
    }

    public void write(Diary diary, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("diaryDate", diary.getDiaryDate());
            json.put("title", diary.getTitle());
            json.put("content", diary.getContent());
            json.put("country", Locale.getDefault().getISO3Country());
            json.put("language", Locale.getDefault().getISO3Language());

            super.post("/author/diary", headers, json.toString(), callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }
}
