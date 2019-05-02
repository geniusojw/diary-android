package org.jerrioh.diary.api;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jerrioh.diary.config.Config;
import org.jerrioh.diary.config.Information;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.StringUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ApiUtil {

    private static final String TAG = "ApiUtil";

    public interface Callback {
        void proc(JSONObject jsonObject) throws JSONException;
    }

    // 정보에 따라서 빈번하게 또는 이따금씩만 호출해도 될 수 있다.
    // 예를 들어 날씨 정보는 1시간에 한번만 업데이트 되는 되는 정도라고 생각됨
    public static void get(Context context, String uri, String token, Callback callback) {
        StringRequest request = getStringRequest(Request.Method.GET, Config.get(context, "api_url") + uri, null, token, callback);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
        queue.start();
    }


    public static void post(Context context, String uri, String requestBody, String token, Callback callback) {
        StringRequest request = getStringRequest(Request.Method.POST, Config.get(context, "api_url") + uri, requestBody, token, callback);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
        queue.start();
    }

    private static StringRequest getStringRequest(int method, String url, String requestBody, String token, Callback callback) {
        return new StringRequest(method, url,
                response -> {
                    Log.d(TAG, url + " success!");
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        callback.proc(jsonObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                vollyError -> {
                    Log.d(TAG, url + " fail!");
                    if (vollyError.networkResponse != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(new String(vollyError.networkResponse.data, "utf-8"));
                            Log.d(TAG, jsonObject.toString());

                            callback.proc(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                if (requestBody != null) {
                    params.put("Content-Type", "application/json");
                }
                params.put("User-Agent", "Onul Diary Mobile Client");
                params.put("Timestamp", String.valueOf(System.currentTimeMillis()));
                params.put("Country-Code", Locale.getDefault().getISO3Country());
                params.put("token", token);
                return params;
            }
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
    }
}
