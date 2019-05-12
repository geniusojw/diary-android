package org.jerrioh.diary.api.account;

import android.content.Context;
import android.util.Log;

import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.ApiCaller;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class AccountApis extends ApiCaller {
    private static final String TAG = "AccountApis";

    public AccountApis(Context context) {
        super(context);
    }

    public void signUp(String accountEmail, String password, String authorId, ApiCallback callback) {
        Map<String, String> headers = defaultHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("accountEmail", accountEmail);
            json.put("password", password);
            json.put("authorId", authorId);
            super.post("/account/sign-up", headers, json.toString(), callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }

    public void signIn(String accountEmail, String password, String authorId, ApiCallback callback) {
        Map<String, String> headers = defaultHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("accountEmail", accountEmail);
            json.put("password", password);
            json.put("authorId", authorId);
            super.post("/account/sign-in", headers, json.toString(), callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }

    public void findPassword(String accountEmail, ApiCallback callback) {
        Map<String, String> headers = defaultHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("accountEmail", accountEmail);
            super.post("/account/find-password", headers, json.toString(), callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }

    public void refreshToken(ApiCallback callback) {
        Map<String, String> headers = accountHeaders();
        JSONObject json = new JSONObject();
        super.post("/account/refresh-token", headers, json.toString(), callback);
    }

    public void deleteAccount(String accountEmail, String password, String authorId, ApiCallback callback) {
        Map<String, String> headers = accountHeaders();
        try {
            JSONObject json = new JSONObject();
            json.put("accountEmail", accountEmail);
            json.put("password", password);
            json.put("authorId", authorId);

            super.post("/account/delete", headers, json.toString(), callback);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException, " + e.toString());
        }
    }
}
