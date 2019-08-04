package org.jerrioh.diary.api.author;

import android.content.Context;
import android.util.Log;

import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.ApiCaller;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class AuthorApis extends ApiCaller {
    private static final String TAG = "AuthorApis";

    public AuthorApis(Context context) {
        super(context);
    }

    public void create(String authorId, ApiCallback callback) {
        Map<String, String> headers = defaultHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("authorId", authorId);
            json.put("nicknameType", 0);
            super.post("/author/start", headers, json.toString(), callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }

    public void authorInfo(ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        super.get("/author", headers, callback);
    }

    public void delete(ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        JSONObject json = new JSONObject();
        super.delete("/author", headers, json.toString(), callback);
    }

    public void changeNickname(String authorId, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        JSONObject json = new JSONObject();
        super.post("/author/change-nick", headers, json.toString(), callback);
    }
}
