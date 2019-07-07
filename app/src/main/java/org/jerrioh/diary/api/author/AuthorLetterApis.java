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

    public void receive(String range, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        super.get("/author/letters?range=" + range, headers, callback);
    }

    public void send(String letterId, String toAuthorId, String toAuthorNickname, String content, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("letterId", letterId);
            json.put("toAuthorId", toAuthorId);
            json.put("toAuthorNickname", toAuthorNickname);
            json.put("content", content);

            super.post("/author/letters", headers, json.toString(), callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }

    public void deleteLetter(String letterId, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        JSONObject json = new JSONObject();
        super.delete("/author/letters/" + letterId, headers, json.toString(), callback);
    }
}
