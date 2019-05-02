package org.jerrioh.diary.api;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EtcApis {

    private static final String TAG = "EtcApis";

    public static void getApplicationInformation(Context context, String token) throws JSONException {
        ApiUtil.Callback callback = (jsonObject) -> {
            int code = (int) jsonObject.get("code");
            if (code == 200000) {
                JSONObject data = (JSONObject) jsonObject.get("data");
                long servertime = data.getLong("servertime");
                JSONArray tips = data.getJSONArray("tips");

                Log.d(TAG, "servertime = " + servertime);
                Log.d(TAG, "clienttime = " + System.currentTimeMillis());
                Log.d(TAG, "tips = " + tips);
            }
        };
        ApiUtil.get(context, "/application-information", token, callback);
    }
}
