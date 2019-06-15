package org.jerrioh.diary.api.author;

import android.content.Context;
import android.util.Log;

import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.ApiCaller;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class UtilApis extends ApiCaller {
    private static final String TAG = "UtilApis";

    public UtilApis(Context context) {
        super(context);
    }

    public void weather(String cityName, String countryCode, ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        super.get("/author/util/weather?city=" + cityName + "&country=" + countryCode, headers, callback);
    }
}
