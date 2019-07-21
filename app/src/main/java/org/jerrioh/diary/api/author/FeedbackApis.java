package org.jerrioh.diary.api.author;

import android.content.Context;
import android.util.Log;

import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.ApiCaller;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class FeedbackApis extends ApiCaller {
    private static final String TAG = "FeedbackApis";

    public FeedbackApis(Context context) {
        super(context);
    }

    public void feedbackDiary(String toAuthorId, String diaryDate, int feedbackDiaryType, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("toAuthorId", toAuthorId);
            json.put("diaryDate", diaryDate);
            json.put("feedbackDiaryType", feedbackDiaryType);

            super.post("/author/feedback/diary", headers, json.toString(), callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }

    public void feedbackAuthor(String toAuthorId, int feedbackAuthorType, String feedbackWrite, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("toAuthorId", toAuthorId);
            json.put("feedbackAuthorType", feedbackAuthorType);
            json.put("feedbackWrite", feedbackWrite);

            super.post("/author/feedback/author", headers, json.toString(), callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }
}
