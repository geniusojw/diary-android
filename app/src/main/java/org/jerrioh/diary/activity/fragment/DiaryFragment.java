package org.jerrioh.diary.activity.fragment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jerrioh.diary.activity.draw.ChocolateStoreActivity;
import org.jerrioh.diary.activity.pop.ChocolateStorePopActivity;
import org.jerrioh.diary.activity.pop.DiaryGroupPopActivity;
import org.jerrioh.diary.model.DiaryGroup;
import org.jerrioh.diary.model.db.DiaryDao;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.activity.main.DiaryReadActivity;
import org.jerrioh.diary.activity.adapter.DiaryRecyclerViewAdapter;
import org.jerrioh.diary.R;
import org.jerrioh.diary.model.db.DiaryGroupDao;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.ThemeUtil;

import java.util.ArrayList;
import java.util.List;

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

        final List<Diary> diaryData = getDiaryData(yyyyMM);
        final DiaryRecyclerViewAdapter mAdapter = new DiaryRecyclerViewAdapter(currentDiaryGroup, diaryData,
                pos -> {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime > currentDiaryGroup.getStartTime()) { // 시작됨
                        Intent intent = new Intent(getActivity(), DiaryGroupPopActivity.class);
                        startActivity(intent);
                    } else { // 준비중
                        // TODO
                        Intent intent = new Intent(getActivity(), ChocolateStorePopActivity.class);
                        intent.putExtra("itemId", ChocolateStoreActivity.ITEM_DIARY_GROUP_SUPPORT);
                        intent.putExtra("itemPrice", 1);
                        startActivity(intent);
                    }
                },
                pos -> {
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

        setDiaryWriteButton(true, BUTTON_TYPE_WRITE_DIARY);

        BitmapDrawable bitmap = ThemeUtil.getBitmapDrawablePattern(this, 1);
        diaryView.setBackgroundDrawable(bitmap);

        return diaryView;
    }

    private List<Diary> getDiaryData(String diaryDate_yyyyMM) {
        DiaryDao diaryDao = new DiaryDao(getActivity());
        final List<Diary> diaryData = diaryDao.getMonthDiariesBeforeToday(diaryDate_yyyyMM, DateUtil.getyyyyMMdd());
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
