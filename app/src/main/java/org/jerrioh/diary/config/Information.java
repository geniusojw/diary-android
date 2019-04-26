package org.jerrioh.diary.config;

import android.content.Context;
import android.util.Log;

import org.jerrioh.diary.db.AccountDao;
import org.jerrioh.diary.db.WriteDao;
import org.jerrioh.diary.dbmodel.Account;
import org.jerrioh.diary.dbmodel.Write;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.DiaryApiUtil;
import org.jerrioh.diary.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Information {
    private static final String TAG = "Information";

    public static Account account;

    public static void updateMyAccount(Context context) {
        AccountDao accountDao = null;
        if (account == null) {
            accountDao = new AccountDao(context);
            account = accountDao.getMyAccount();

            if (account == null) { // sqlite DB에 없다면 앱 최초 시작 또는 앱 데이터삭제 등
                account = new Account(UUID.randomUUID().toString(), "", "", "", "0");
                accountDao.insertMyAccount(account);
            }
        }

        long currentTime = System.currentTimeMillis();
        long nextUpdateTime = Long.parseLong(account.getNextUpdateTime());
        boolean updateExecute = DateUtil.timePassed(currentTime, nextUpdateTime, 0);

        if (!updateExecute) {
            Log.d(TAG, "do not update account information. wait...");
            return;
        }
        Log.d(TAG, "try to update account information");

        try {
            if (StringUtil.isEmpty(account.getToken())) { // start user
                signup(context);
                account.setNextUpdateTime(String.valueOf(currentTime + TimeUnit.SECONDS.toMillis(15)));
            } else if (DiaryApiUtil.timeToUpdateToken(account.getToken())) { // refresh
                signin(context);
                account.setNextUpdateTime(String.valueOf(currentTime + TimeUnit.SECONDS.toMillis(15)));
            } else {
                getApplicationInformation(context);
                sendToServerYesterDayDiary(context);
                account.setNextUpdateTime(String.valueOf(currentTime + TimeUnit.SECONDS.toMillis(30)));
            }

            if (accountDao == null) {
                accountDao = new AccountDao(context);
            }
            accountDao.updateMyAccount(account);

        } catch (JSONException e) {
            Log.d(TAG, "json exception occured");
        }
    }

    private static void signup(Context context) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("userId", account.getUserId());
        json.put("password", "watermelon#00");

        DiaryApiUtil.Callback callback = (jsonObject) -> {
            int code = (int) jsonObject.get("code");
            if (code == 200000) {
                JSONObject data = (JSONObject) jsonObject.get("data");
                String token = (String) data.get("token");
                account.setToken(token);
                new AccountDao(context).updateMyAccount(account);
            }
        };
        DiaryApiUtil.post(context, "/account/signup", json.toString(), null, callback);
    }

    private static void signin(Context context) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("userId", account.getUserId());
        json.put("password", "watermelon#00");

        DiaryApiUtil.Callback callback = (jsonObject) -> {
            int code = (int) jsonObject.get("code");
            if (code == 200000) {
                JSONObject data = (JSONObject) jsonObject.get("data");
                String token = (String) data.get("token");
                account.setToken(token);
                new AccountDao(context).updateMyAccount(account);
            }
        };
        DiaryApiUtil.post(context, "/account/signin", json.toString(), null, callback);
    }

    private static void getApplicationInformation(Context context) throws JSONException {
        DiaryApiUtil.Callback callback = (jsonObject) -> {
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
        DiaryApiUtil.get(context, "/application-information", account.getToken(), callback);
    }

    private static void sendToServerYesterDayDiary(Context context) throws JSONException {
        // 어제의 일기 저장
        WriteDao writeDao = new WriteDao(context);
        String yesterday_yyyyMMdd = DateUtil.getyyyyMMdd(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24));

        Write yesterdayDiary = writeDao.getTodayDiary(yesterday_yyyyMMdd);
        if (yesterdayDiary == null || yesterdayDiary.getServerSaved() == 1) {
            return;
        }

        if (StringUtil.isEmpty(yesterdayDiary.getTitle())
            && StringUtil.isEmpty(yesterdayDiary.getContent())) {
            return;
        }

        DiaryApiUtil.Callback callback = (jsonObject) -> {
            int code = (int) jsonObject.get("code");
            if (code == 200000 || code == 409002) { // 저장성공 or 이미 서버에 저장됨
                yesterdayDiary.setServerSaved(1);
                writeDao.updateTodayDiary(yesterdayDiary);
                Log.d(TAG, "save complete. day=" + yesterdayDiary.getWriteDay() + ", code=" + code);
            }
        };
        JSONObject json = new JSONObject();
        json.put("title", yesterdayDiary.getTitle());
        json.put("content", yesterdayDiary.getContent());
        DiaryApiUtil.post(context, "/diary/" + yesterdayDiary.getWriteDay(), json.toString(), account.getToken(), callback);
    }
}
