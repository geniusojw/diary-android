package org.jerrioh.diary.api;

import android.content.Context;
import android.util.Log;

import org.jerrioh.diary.db.DiaryDao;
import org.jerrioh.diary.dbmodel.Diary;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class DiaryApis {

    private static final String TAG = "DiaryApis";

    public static void getAllDiary(Context context, String token) throws JSONException {

        ApiCaller.Callback callback = (jsonObject) -> {
            int code = (int) jsonObject.get("code");
            if (code == 200000) {
                DiaryDao diaryDao = new DiaryDao(context);
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    String writeDay = obj.getString("writeDay");
                    String writeUserId = obj.getString("writeUserId");
                    String title = obj.getString("title");
                    String content = obj.getString("content");

                    Diary diary = new Diary(writeDay, writeUserId, title, content, 1);
                    diaryDao.insertWrite(diary);
                }
            }
        };

        ApiCaller.get(context, "/diary", callback);
    }

    public static void sendToServerYesterDayDiary(Context context) throws JSONException {
        // 어제의 일기 저장
        DiaryDao diaryDao = new DiaryDao(context);
        String yesterday_yyyyMMdd = DateUtil.getyyyyMMdd(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24));

        Diary yesterdayDiary = diaryDao.getTodayDiary(yesterday_yyyyMMdd);
        if (yesterdayDiary == null || yesterdayDiary.getServerSaved() == 1) {
            return;
        }

        if (StringUtil.isEmpty(yesterdayDiary.getTitle())
                && StringUtil.isEmpty(yesterdayDiary.getContent())) {
            return;
        }

        ApiCaller.Callback callback = (jsonObject) -> {
            int code = (int) jsonObject.get("code");
            if (code == 200000 || code == 409002) { // 저장성공 or 이미 서버에 저장됨
                yesterdayDiary.setServerSaved(1);
                diaryDao.updateTodayDiary(yesterdayDiary);
                Log.d(TAG, "save complete. day=" + yesterdayDiary.getWriteDay() + ", code=" + code);
            }
        };
        JSONObject json = new JSONObject();
        json.put("title", yesterdayDiary.getTitle());
        json.put("content", yesterdayDiary.getContent());
        ApiCaller.post(context, "/diary/" + yesterdayDiary.getWriteDay(), json.toString(), callback);
    }
}
