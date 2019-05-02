package org.jerrioh.diary.config;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;

import org.jerrioh.diary.api.AccountApis;
import org.jerrioh.diary.api.ApiUtil;
import org.jerrioh.diary.api.DiaryApis;
import org.jerrioh.diary.api.EtcApis;
import org.jerrioh.diary.db.AccountDao;
import org.jerrioh.diary.db.DiaryDao;
import org.jerrioh.diary.dbmodel.Account;
import org.jerrioh.diary.dbmodel.Diary;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Information {
    private static final String TAG = "Information";

    private static Account account;

    public static Account getAccount() {
        return account;
    }

    public static Account getAccount(Context context) {
        if (account == null) {
            updateMyAccount(context);
        }
        return account;
    }

    public static void updateMyAccount(Context context) {
        AccountDao accountDao = new AccountDao(context);
        account = accountDao.getMyAccount();

        if (account == null) { // sqlite DB에 없다면 앱 최초 시작 또는 앱 데이터삭제 등
            account = new Account(UUID.randomUUID().toString(), "", "temporary random nickname", "temporary random description", "","0");
            accountDao.insertMyAccount(account);
        }

        long currentTime = System.currentTimeMillis();
        long nextUpdateTime = Long.parseLong(account.getNextUpdateTime());
        boolean updateExecute = DateUtil.timePassed(currentTime, nextUpdateTime, 0);

        if (!updateExecute) {
            Log.d(TAG, "do not update account information. wait...");
            return;
        }

        try {
            boolean hasValidToken = false;
            boolean isMember = Patterns.EMAIL_ADDRESS.matcher(account.getUserId()).matches();

            if (isMember) {
                Log.d(TAG, "try to update member account information");
                if (StringUtil.isEmpty(account.getToken())) { // start user
                    // logout
                } else {
                    if (AccountApis.expired(account.getToken())) { // refresh
                        // logout
                    } else {
                        AccountApis.refresh(context, account.getToken());
                        hasValidToken = true;
                    }
                }
            } else {
                Log.d(TAG, "try to update non-member account information");
                if (StringUtil.isEmpty(account.getToken())) { // start user
                    AccountApis.signup(context, account.getUserId(), "watermelon#00");
                } else {
                    if (AccountApis.expired(account.getToken())) { // refresh
                        AccountApis.signin(context, account.getUserId(), "watermelon#00");
                    } else {
                        hasValidToken = true;
                    }
                }
                nextUpdateTime = currentTime + TimeUnit.SECONDS.toMillis(15);
            }

            if (hasValidToken) {
                EtcApis.getApplicationInformation(context, account.getToken());
                DiaryApis.sendToServerYesterDayDiary(context, account.getToken());
                nextUpdateTime = currentTime + TimeUnit.SECONDS.toMillis(30);
            }

            accountDao.updateNextUpdateTime(String.valueOf(nextUpdateTime));

        } catch (JSONException e) {
            Log.d(TAG, "json exception occured");
        }
    }
}
