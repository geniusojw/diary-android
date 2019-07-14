package org.jerrioh.diary.util;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {
    public static String getString(String key, JSONObject data) throws JSONException {
        if (data.isNull(key)) {
            return null;
        }
        return data.getString(key);
    }

    public static Long getLong(String key, JSONObject data) throws JSONException {
        if (data.isNull(key)) {
            return null;
        }
        return data.getLong(key);
    }

    public static Integer getInt(String key, JSONObject data) throws JSONException {
        if (data.isNull(key)) {
            return null;
        }
        return data.getInt(key);
    }

    public static JSONObject getJsonObject(String key, JSONObject data) throws JSONException {
        if (data.isNull(key)) {
            return null;
        }
        return data.getJSONObject(key);
    }
}
