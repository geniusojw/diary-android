package org.jerrioh.diary.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ApiCallback {
    private static final String TAG = "ApiCallback";

    protected abstract void execute(int httpStatus, JSONObject jsonObject) throws JSONException;
}
