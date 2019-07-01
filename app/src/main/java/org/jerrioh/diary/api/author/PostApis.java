package org.jerrioh.diary.api.author;

import android.content.Context;
import android.util.Log;

import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.ApiCaller;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class PostApis extends ApiCaller {
    private static final String TAG = "PostApis";

    public PostApis(Context context) {
        super(context);
    }

    public void get(ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        super.get("/author/posts", headers, callback);
    }

    public void hasPost(ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        super.get("/author/posts/has-post", headers, callback);
    }

    public void post(String content, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("content", content);

            super.post("/author/posts", headers, json.toString(), callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }
}
