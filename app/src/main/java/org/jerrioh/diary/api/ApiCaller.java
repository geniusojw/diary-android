package org.jerrioh.diary.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jerrioh.diary.config.Config;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.db.AuthorDao;
import org.jerrioh.diary.util.AuthorUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ApiCaller {
    private static final String TAG = "ApiCaller";
    private static final String UTF_8 = "utf-8";

    protected Context context;

    protected ApiCaller(Context context) {
        this.context = context;
    }

    protected void get(String uri, Map<String, String> headers, ApiCallback callback) {
        if (validParameters(uri, headers, callback)) {
            StringRequest request = volleyRequest(Request.Method.GET, Config.get(context, "api_url") + uri, headers, null, callback);
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);
        }
    }

    protected void post(String uri, Map<String, String> headers, String body, ApiCallback callback) {
        if (validParameters(uri, headers, callback)) {
            StringRequest request = volleyRequest(Request.Method.POST, Config.get(context, "api_url") + uri, headers, body, callback);
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);
        }
    }

    private boolean validParameters(String uri, Map<String, String> headers, ApiCallback callback) {
        return uri != null && headers != null && callback != null;
    }

    private StringRequest volleyRequest(int method, String url, Map<String, String> headers, String body, ApiCallback callback) {
        return new StringRequest(method, url,
                response -> {
                    Log.d(TAG, url + " success!");
                    Log.d(TAG, "response = " + response);
                    callback.execute(200, response);
                },
                volleyError -> {
                    Log.d(TAG, url + " fail!" + ", volleyError=" + volleyError.toString());
                    if (volleyError.networkResponse != null) {
                        String response = null;
                        try {
                            response = new String(volleyError.networkResponse.data, UTF_8);
                            Log.d(TAG, "response = " + response);
                            callback.execute(volleyError.networkResponse.statusCode, response);
                        } catch (UnsupportedEncodingException e) {
                            Log.d(TAG, "UnsupportedEncodingException = " + e.toString());
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
            @Override
            public byte[] getBody() {
                if (body != null) {
                    try {
                        return body.getBytes(UTF_8);
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", body, "utf-8");
                    }
                }
                return null;
            }
        };
    }

    protected Map<String, String> defaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Onul Diary Mobile Client");
        headers.put("Timestamp", String.valueOf(System.currentTimeMillis()));
        headers.put("Country", Locale.getDefault().getISO3Country());
        headers.put("Language", Locale.getDefault().getISO3Language());
        headers.put("Content-Type", "application/json");
        return headers;
    }

    protected Map<String, String> authorHeaders() {
        AuthorDao authorDao = new AuthorDao(context);
        Author author = authorDao.getAuthor();

        Map<String, String> headers = defaultHeaders();
        headers.put("Author-ID", author.getAuthorId());
        headers.put("Author-Code", author.getAuthorCode());
        return headers;
    }

    protected Map<String, String> accountHeaders() {
        AuthorDao authorDao = new AuthorDao(context);
        Author author = authorDao.getAuthor();

        // 로그인하지 않은 사용자
        if (TextUtils.isEmpty(author.getAccountEmail())) {
            return null;
        }

        // 로그인하였으나 토큰만료된 상태
        String accountToken = author.getAccountToken();
        if (!isValidAccountToken(accountToken)) {
            AuthorUtil.accountSignOut(context); // 로그아웃 처리
            return null;
        }

        Map<String, String> headers = defaultHeaders();
        headers.put("Token", accountToken);
        return headers;
    }

    private boolean isValidAccountToken(String accountToken) {
        if (TextUtils.isEmpty(accountToken)) {
            Log.d(TAG, "accountToken is empty");
            return false;
        }

        int start = accountToken.indexOf(".") + 1;
        int end = accountToken.indexOf(".", start);

        byte[] decoded = Base64.decode(accountToken.substring(start, end), 0);
        String jwtBody = new String(decoded);
        if (TextUtils.isEmpty(jwtBody)) {
            Log.d(TAG, "JWT body is null");
            return false;
        }

        try {
            JSONObject jsonObject = new JSONObject(jwtBody);
            long expire = jsonObject.getLong("exp") * 1000;
            long current = System.currentTimeMillis();

            if (expire < current) {
                Log.d(TAG, "current: " + current + ", expire: " + expire);
                return false;
            }
        } catch (JSONException e) {
            Log.d(TAG, "JsonException. " + e.toString());
        }
        return true;
    }
}