package org.jerrioh.diary.api.author;

import android.content.Context;
import android.util.Log;

import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.ApiCaller;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class AuthorStoreApis extends ApiCaller {
    private static final String TAG = "AuthorStoreApis";

    public AuthorStoreApis(Context context) {
        super(context);
    }

    public void getStoreStatus(ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        super.get("/author/store/status", headers, callback);
    }

    public void changeDescription(ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        JSONObject json = new JSONObject();
        super.post("/author/store/change-description", headers, json.toString(), callback);
    }

    public void changeNickname(ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        JSONObject json = new JSONObject();
        super.post("/author/store/change-nickname", headers, json.toString(), callback);
    }

    public void changeDiaryTheme(ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        JSONObject json = new JSONObject();
        super.post("/author/store/change-diary-theme", headers, json.toString(), callback);
    }

    public void aliasFeatureUnlimitedUse(ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        JSONObject json = new JSONObject();
        super.post("/author/store/alias-feature-unlimited-use", headers, json.toString(), callback);
    }

    public void inviteTicket1(ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("diaryGroupName", "test group");
            json.put("keyword", "test keyword");
            json.put("language", Locale.getDefault().getISO3Language());
            json.put("country", Locale.getDefault().getISO3Country());
            json.put("timeZoneId", TimeZone.getDefault().getID());

            super.post("/author/store/invite-ticket1", headers, json.toString(), callback);

        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }

    public void inviteTicket2(ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("diaryGroupName", "test group");
            json.put("keyword", "test keyword");
            json.put("language", Locale.getDefault().getISO3Language());
            json.put("country", Locale.getDefault().getISO3Country());
            json.put("timeZoneId", TimeZone.getDefault().getID());

            super.post("/author/store/invite-ticket2", headers, json.toString(), callback);

        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }

    public void donate(int chocolates, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("chocolates", chocolates);

            super.post("/author/store/chocolate-donation", headers, json.toString(), callback);

        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }
}
