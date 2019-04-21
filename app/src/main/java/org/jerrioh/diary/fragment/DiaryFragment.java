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

import org.jerrioh.diary.db.WriteDao;
import org.jerrioh.diary.dbmodel.Write;
import org.jerrioh.diary.activity.DiaryReadActivity;
import org.jerrioh.diary.adapter.DiaryRecyclerViewAdapter;
import org.jerrioh.diary.R;
import org.jerrioh.diary.util.DateUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DiaryFragment extends Fragment {
    private static final String TAG = "DiaryFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View diaryView = inflater.inflate(R.layout.fragment_diary, container, false);
        RecyclerView diaryRecyclerView = diaryView.findViewById(R.id.diary_recycler_view);

        diaryRecyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        diaryRecyclerView.setLayoutManager(layoutManager);


        String yesterday_yyyyMMdd = DateUtil.getyyyyMMdd(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24));
        String yyyyMM = null;
        Bundle args = getArguments();
        if (args != null) {
            yyyyMM = args.getString("diplay_yyyyMM");
        }
        if (yyyyMM == null) {
            yyyyMM = yesterday_yyyyMMdd.substring(0, 6);
        }
        WriteDao writeDao = new WriteDao(getActivity());
        final List<Write> diaryData = writeDao.getMyPeriodDiary(yyyyMM, yesterday_yyyyMMdd);
        Log.d(TAG, "diaryData.size()=" + diaryData.size());

        RecyclerView.Adapter mAdapter = new DiaryRecyclerViewAdapter(diaryData, pos -> {
            Intent intent = new Intent(getActivity(), DiaryReadActivity.class);

            Write diary = diaryData.get(pos);
            intent.putExtra("diary", diary);
            startActivity(intent);
        });

        diaryRecyclerView.setAdapter(mAdapter);
        return diaryView;
    }
}
