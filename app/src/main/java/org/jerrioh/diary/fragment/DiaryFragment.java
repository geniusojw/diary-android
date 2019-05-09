package org.jerrioh.diary.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jerrioh.diary.db.DiaryDao;
import org.jerrioh.diary.dbmodel.Diary;
import org.jerrioh.diary.activity.DiaryReadActivity;
import org.jerrioh.diary.adapter.DiaryRecyclerViewAdapter;
import org.jerrioh.diary.R;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DiaryFragment extends MainActivityFragment {
    private static final String TAG = "DiaryFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 보여줄 해와 월
        String yyyyMM = null;

        Bundle args = getArguments();
        if (args != null) {
            yyyyMM = args.getString("display_yyyyMM");
        }
        if (yyyyMM == null) {
            yyyyMM = DateUtil.getyyyyMMdd().substring(0, 6);
        }

        // 일기내용을 리스트로 표시한다
        String yesterday_yyyyMMdd = DateUtil.getyyyyMMdd(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24));
        final List<Diary> diaryData = getDiaryData(yyyyMM, yesterday_yyyyMMdd);
        final DiaryRecyclerViewAdapter mAdapter = new DiaryRecyclerViewAdapter(diaryData, pos -> {
            Intent intent = new Intent(getActivity(), DiaryReadActivity.class);

            Diary diary = diaryData.get(pos);
            intent.putExtra("diary", diary);
            startActivity(intent);
        });

        View diaryView = inflater.inflate(R.layout.fragment_diary, container, false);
        RecyclerView diaryRecyclerView = diaryView.findViewById(R.id.diary_recycler_view);

        diaryRecyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        diaryRecyclerView.setLayoutManager(layoutManager);
        diaryRecyclerView.setAdapter(mAdapter);

        setDiaryWriteButton(true);
        return diaryView;
    }

    private List<Diary> getDiaryData(String yyyyMM, String yesterday_yyyyMMdd) {
        DiaryDao diaryDao = new DiaryDao(getActivity());
        final List<Diary> diaryData = diaryDao.getMyPeriodDiary(yyyyMM, yesterday_yyyyMMdd);
        Log.d(TAG, "diaryData.size()=" + diaryData.size());

        // 리스트 노출전 제목과 내용이 없는 일기 삭제 처리
        List<Diary> removeData = new ArrayList<>();
        for (Diary diary : diaryData) {
            if (StringUtil.isEmpty(diary.getTitle()) && StringUtil.isEmpty(diary.getContent())) {
                removeData.add(diary);
            }
        }
        for (Diary diary : removeData) {
            diaryDao.removeMyDiary(diary.getWriteDay());
            diaryData.remove(diary);
        }
        return diaryData;
    }
}
