package org.jerrioh.diary.api.author;

import android.content.Context;

import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.ApiCaller;
import org.json.JSONObject;

import java.util.Map;

public class DiaryGroupApis extends ApiCaller {
    private static final String TAG = "DiaryGroupApis";

    public DiaryGroupApis(Context context) {
        super(context);
    }

    public void getDiaryGroup(ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        super.get("/author/diary-group", headers, callback);
    }

    public void readYesterdayDiaries(ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        super.get("/author/diary-group/yesterday-diaries", headers, callback);
    }

    public void beInvited(ApiCallback callback) {
        Map<String, String> headers = authorHeaders();
        JSONObject json = new JSONObject();
        super.post("/author/diary-group/be-invited", headers, json.toString(), callback);
    }
}
