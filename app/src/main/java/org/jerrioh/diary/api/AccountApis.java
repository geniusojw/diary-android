package org.jerrioh.diary.api;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.jerrioh.diary.db.AccountDao;
import org.jerrioh.diary.dbmodel.Account;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.StringUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class AccountApis {

    private static final String TAG = "AccountApis";

    public static boolean expired(String token) {
        if (StringUtil.isEmpty(token)) {
            Log.d(TAG, "token is empty");
            return true;
        }

        int start = token.indexOf(".") + 1;
        int end = token.indexOf(".", start);

        byte[] decoded = Base64.decode(token.substring(start, end), 0);
        String jwtBody = new String(decoded);
        if (StringUtil.isEmpty(jwtBody)) {
            Log.d(TAG, "jwt body is null");
            return true;
        }

        try {
            long expire = new JSONObject(jwtBody).getLong("exp") * 1000;
            long current = System.currentTimeMillis();
            if (DateUtil.timePassed(current, expire, 0)) {
                Log.d(TAG, "cur: " + current + ", exp: " + expire);
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void signup(Context context, String userId, String password) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("userId", userId);
        json.put("password", password);

        ApiUtil.Callback callback = (jsonObject) -> {
            int code = (int) jsonObject.get("code");
            if (code == 200000) {
                JSONObject data = (JSONObject) jsonObject.get("data");
                new AccountDao(context).updateToken(data.getString("token"));
            }
        };
        ApiUtil.post(context, "/account/signup", json.toString(), null, callback);
    }

    public static void signin(Context context, String userId, String password) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("userId", userId);
        json.put("password", password);

        ApiUtil.Callback callback = (jsonObject) -> {
            int code = (int) jsonObject.get("code");
            if (code == 200000) {
                JSONObject data = (JSONObject) jsonObject.get("data");
                new AccountDao(context).updateToken(data.getString("token"));
            }
        };
        ApiUtil.post(context, "/account/signin", json.toString(), null, callback);
    }

    public static void signinCallback(Context context, String userId, String password, ApiUtil.Callback callback) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("userId", userId);
        json.put("password", password);

        ApiUtil.post(context, "/account/signin", json.toString(), null, callback);
    }

    public static void refresh(Context context, String token) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("jwt", token);

        ApiUtil.Callback callback = (jsonObject) -> {
            int code = (int) jsonObject.get("code");
            if (code == 200000) {
                JSONObject data = (JSONObject) jsonObject.get("data");
                new AccountDao(context).updateToken(data.getString("token"));
            }
        };
        ApiUtil.post(context, "/account/refresh-token", json.toString(), null, callback);
    }
}
