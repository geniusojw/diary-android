package org.jerrioh.diary.api.author;

import android.content.Context;
import android.util.Log;

import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.ApiCaller;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.model.Letter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;

public class AuthorLetterApis extends ApiCaller {
    private static final String TAG = "AuthorLetterApis";

    public AuthorLetterApis(Context context) {
        super(context);
    }

    public void receive(ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        super.get("/author/letters", headers, callback);
    }

    public void send(Letter letter, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("letterId", letter.getLetterId());
            json.put("toAuthorId", letter.getToAuthorId());
            json.put("title", letter.getTitle());
            json.put("content", letter.getContent());

            super.post("/author/letters", headers, json.toString(), callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }
}
