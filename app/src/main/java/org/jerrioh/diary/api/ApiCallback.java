package org.jerrioh.diary.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ApiCallback {
    private static final String TAG = "ApiCallback";

    protected abstract void execute(int httpStatus, JSONObject jsonObject) throws JSONException;

    public void execute(int httpStatus, String response) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            execute(httpStatus, jsonObject);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException! response = " + response);
        }
    }
}
