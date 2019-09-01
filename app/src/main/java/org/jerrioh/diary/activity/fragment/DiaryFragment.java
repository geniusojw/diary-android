package org.jerrioh.diary.activity.fragment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jerrioh.diary.activity.main.DiaryWriteActivity;
import org.jerrioh.diary.activity.pop.SentencePopActivity;
import org.jerrioh.diary.activity.pop.StorePopActivity;
import org.jerrioh.diary.activity.pop.DiaryGroupPopActivity;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.AuthorStoreApis;
import org.jerrioh.diary.model.DiaryGroup;
import org.jerrioh.diary.model.db.DiaryDao;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.activity.main.DiaryReadActivity;
import org.jerrioh.diary.activity.adapter.DiaryRecyclerViewAdapter;
import org.jerrioh.diary.R;
import org.jerrioh.diary.model.db.DiaryGroupDao;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.ThemeUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static org.jerrioh.diary.activity.fragment.StoreFragment.ITEM_DIARY_GROUP_SUPPORT;

public class DiaryFragment extends AbstractFragment {
    private static final String TAG = "DiaryFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 보여줄 해와 월
        String yyyyMM = null;

        Bundle args = getArguments();
        if (args != null) {
            yyyyMM = args.getString("display_yyyyMM");
        }
        if (yyyyMM == null) {
            yyyyMM = DateUtil.getyyyyMMdd().substring(0, 6);
        }

        // 일기내용을 리스트로 표시한다.
        DiaryGroupDao diaryGroupDao = new DiaryGroupDao(getActivity());

        final DiaryGroup currentDiaryGroup;
        DiaryGroup diaryGroup = diaryGroupDao.getDiaryGroup();
        if (diaryGroup != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime > diaryGroup.getEndTime()) { // 종료됨
                diaryGroupDao.deleteDiaryGroup();
                currentDiaryGroup = null;
            } else { // 준비 또는 시작
                currentDiaryGroup = diaryGroup;
            }
        } else {
            currentDiaryGroup = null;
        }

        final String today_yyyyMMdd = DateUtil.getyyyyMMdd();
        final List<Diary> diaryData = getDiaryData(yyyyMM, today_yyyyMMdd);
        final DiaryRecyclerViewAdapter mAdapter = new DiaryRecyclerViewAdapter(getActivity(), currentDiaryGroup, diaryData,
                pos -> {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime > currentDiaryGroup.getStartTime()) { // 시작됨
                        Intent intent = new Intent(getActivity(), DiaryGroupPopActivity.class);
                        startActivity(intent);

                    } else { // 준비중
                        String authorId = AuthorUtil.getAuthor(getActivity()).getAuthorId();
                        AuthorStoreApis authorStoreApis = new AuthorStoreApis(getActivity());
                        authorStoreApis.getStoreStatus(new ApiCallback() {
                            @Override
                            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                                if (httpStatus == 200) {
                                    if (authorId.equals(diaryGroup.getHostAuthorId())) {
                                        Intent intent = new Intent(getActivity(), SentencePopActivity.class);
                                        intent.putExtra("type", SentencePopActivity.TYPE_DIARY_GROUP_KEYWORD);
                                        startActivity(intent);

                                    } else {
                                        JSONObject data = jsonObject.getJSONObject("data");
                                        int chocolates = data.getInt("chocolates");

                                        Intent intent = new Intent(getActivity(), StorePopActivity.class);
                                        intent.putExtra("itemId", ITEM_DIARY_GROUP_SUPPORT);
                                        intent.putExtra("itemPrice", 3);
                                        intent.putExtra("currentChocolates", chocolates);
                                        startActivity(intent);
                                    }
                                }
                            }
                        });
                    }
                },
                pos -> {
                    Diary diary = diaryData.get(pos);

                    if (diary.getDiaryDate().equals(today_yyyyMMdd)) {
                        startActivity(new Intent(getActivity(), DiaryWriteActivity.class));
                    } else {
                        Intent intent = new Intent(getActivity(), DiaryReadActivity.class);
                        intent.putExtra("diary", diary);
                        startActivity(intent);
                    }
        });

        View diaryView = inflater.inflate(R.layout.fragment_diary, container, false);
        RecyclerView diaryRecyclerView = diaryView.findViewById(R.id.diary_recycler_view);

        diaryRecyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        diaryRecyclerView.setLayoutManager(layoutManager);
        diaryRecyclerView.setAdapter(mAdapter);

        super.setFloatingActionButton(AbstractFragment.BUTTON_TYPE_DIARY);

        BitmapDrawable bitmap = ThemeUtil.getBitmapDrawablePattern(this, 0);
        diaryView.setBackgroundDrawable(bitmap);

        return diaryView;
    }

    private List<Diary> getDiaryData(String diaryDate_yyyyMM, String today_yyyyMMdd) {
        DiaryDao diaryDao = new DiaryDao(getActivity());

        final List<Diary> diaryData = new ArrayList<>();
        Diary todayDiary = diaryDao.getDiary(today_yyyyMMdd);
        if (todayDiary != null) {
            diaryData.add(todayDiary);
        }
        List<Diary> pastDiaries = diaryDao.getMonthDiariesBeforeToday(diaryDate_yyyyMM, today_yyyyMMdd);
        if (!pastDiaries.isEmpty()) {
            diaryData.addAll(pastDiaries);
        }

        Log.d(TAG, "diaryData.size()=" + diaryData.size());

        // 리스트 노출전 제목과 내용이 없는 일기 삭제 처리
        List<Diary> removeData = new ArrayList<>();
        for (Diary diary : diaryData) {
            if (TextUtils.isEmpty(diary.getTitle()) && TextUtils.isEmpty(diary.getContent())) {
                removeData.add(diary);
            }
        }
        for (Diary diary : removeData) {
            diaryData.remove(diary);
            diaryDao.deleteDiary(diary.getDiaryDate());
        }
        return diaryData;
    }
}
