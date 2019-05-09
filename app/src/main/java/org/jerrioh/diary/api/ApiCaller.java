package org.jerrioh.diary.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jerrioh.diary.config.Config;
import org.jerrioh.diary.dbmodel.Account;
import org.jerrioh.diary.util.CurrentAccountUtil;
import org.jerrioh.diary.util.StringUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ApiCaller {

    private static final String TAG = "ApiCaller";

    public interface Callback {
        void callback(JSONObject jsonObject) throws JSONException;
    }

    public static void get(Context context, String uri, Callback callback) {
        String validToken = validToken(context);
        if (TextUtils.isEmpty(validToken)) {
            return;
        }

        StringRequest request = getStringRequest(Request.Method.GET, Config.get(context, "api_url") + uri, null, validToken, callback);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public static void post(Context context, String uri, String requestBody, Callback callback) {
        String validToken = validToken(context);
        if (TextUtils.isEmpty(validToken)) {
            return;
        }

        StringRequest request = getStringRequest(Request.Method.POST, Config.get(context, "api_url") + uri, requestBody, validToken, callback);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }


    public static void postWithoutToken(Context context, String uri, String requestBody, Callback callback) {
        StringRequest request = getStringRequest(Request.Method.POST, Config.get(context, "api_url") + uri, requestBody, null, callback);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    private static StringRequest getStringRequest(int method, String url, String requestBody, String token, Callback callback) {
        return new StringRequest(method, url,
                response -> {
                    Log.d(TAG, url + " success!");
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.d(TAG, jsonObject.toString());
                        callback.callback(jsonObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                vollyError -> {
                    Log.d(TAG, url + " fail!" + ", vollyError=" + vollyError.toString());
                    if (vollyError.networkResponse != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(new String(vollyError.networkResponse.data, "utf-8"));
                            Log.d(TAG, jsonObject.toString());
                            callback.callback(jsonObject);

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
                params.put("User-Agent", "Onul Diary Mobile Client");
                params.put("Timestamp", String.valueOf(System.currentTimeMillis()));
                params.put("Country", Locale.getDefault().getISO3Country());
                params.put("Language", Locale.getDefault().getISO3Language());
                params.put("Token", token);
                if (requestBody != null) {
                    params.put("Content-Type", "application/json");
                }
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

    private static String validToken(Context context) {
        Account currentAccount = CurrentAccountUtil.getAccount(context);

        if (CurrentAccountUtil.isMember(currentAccount.getUserId())) {
            Log.d(TAG, "try to update member account information");
            if (StringUtil.isEmpty(currentAccount.getToken())) {
                return null;
            } else if (AccountApis.expired(currentAccount.getToken())) {
                return null;
            } else {
                return currentAccount.getToken();
            }

        } else {
            Log.d(TAG, "try to update non-member account information");
            try {
                if (StringUtil.isEmpty(currentAccount.getToken())) {
                    AccountApis.signup(context, currentAccount.getUserId(), "watermelon#00");
                    return null;
                } else if (AccountApis.expired(currentAccount.getToken())) {
                    AccountApis.signin(context, currentAccount.getUserId(), "watermelon#00");
                    return null;
                } else {
                    return currentAccount.getToken();
                }
            } catch (JSONException e) {
                return null;
            }
        }
    }
}
