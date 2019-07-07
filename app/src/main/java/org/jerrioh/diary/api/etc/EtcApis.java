package org.jerrioh.diary.api.etc;

import android.content.Context;
import android.util.Log;

import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.ApiCaller;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class EtcApis extends ApiCaller {
    private static final String TAG = "EtcApis";

    public EtcApis(Context context) {
        super(context);
    }

    public void version(String version, ApiCallback callback) {
        Map<String, String> headers = defaultHeaders();
        super.get("/app/versions/" + version, headers, callback);
    }
}
