package org.jerrioh.diary.api.author;

import android.content.Context;

import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.ApiCaller;

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
}
