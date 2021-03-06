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

    public void getWiseSaying(ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        JSONObject json = new JSONObject();
        super.post("/author/store/wise-saying", headers, json.toString(), callback);
    }

    public void createWiseSaying(String wiseSaying, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("saying", wiseSaying);
            super.post("/author/store/create-wise-saying", headers, json.toString(), callback);

        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }

    public void weather(String city, String country, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("city", city);
            json.put("country", country);
            super.post("/author/store/weather", headers, json.toString(), callback);

        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }

    public void buyPostIt(int price, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("price", price);

            super.post("/author/store/buy-post-it", headers, json.toString(), callback);

        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
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

    public void changeNicknameType(int nicknameType, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("price", nicknameType);
            super.post("/author/store/change-nickname-type", headers, json.toString(), callback);

        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }

    public void purchaseTheme(ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        JSONObject json = new JSONObject();
        super.post("/author/store/purchase-theme", headers, json.toString(), callback);
    }

    public void downloadTheme(String themeName, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        JSONObject json = new JSONObject();
        super.post("/author/store/download-theme/" + themeName, headers, json.toString(), callback);
    }

    public void purchaseMusic(ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        JSONObject json = new JSONObject();
        super.post("/author/store/purchase-music", headers, json.toString(), callback);
    }

    public void downloadMusic(String musicName, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        JSONObject json = new JSONObject();
        super.post("/author/store/download-music/" + musicName, headers, json.toString(), callback);
    }

    public void aliasFeatureUnlimitedUse(ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        JSONObject json = new JSONObject();
        super.post("/author/store/alias-feature-unlimited-use", headers, json.toString(), callback);
    }

    public void diaryGroupInvitation(String keyword, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("keyword", keyword);

            super.post("/author/store/diary-group-invitation", headers, json.toString(), callback);

        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }

    public void diaryGroupSupport(String supportType, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("supportType", supportType);
            super.post("/author/store/diary-group-support", headers, json.toString(), callback);

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
