package org.jerrioh.diary.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jerrioh.diary.config.Information;
import org.jerrioh.diary.db.DbHelper;
import org.jerrioh.diary.db.WritingDao;
import org.jerrioh.diary.dbmodel.Writing;
import org.jerrioh.diary.activity.DiaryReadActivity;
import org.jerrioh.diary.adapter.DiaryRecyclerViewAdapter;
import org.jerrioh.diary.R;

import java.util.ArrayList;
import java.util.List;

public class DiaryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View diaryView = inflater.inflate(R.layout.fragment_diary, container, false);
        RecyclerView diaryRecyclerView = diaryView.findViewById(R.id.diary_recycler_view);

        diaryRecyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        diaryRecyclerView.setLayoutManager(layoutManager);

        final List<Writing> diaryData = getDiaryData();

        RecyclerView.Adapter mAdapter = new DiaryRecyclerViewAdapter(diaryData, new DiaryRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Intent intent = new Intent(getActivity(), DiaryReadActivity.class);

                Writing diary = diaryData.get(pos);
                intent.putExtra("diary", diary);
                startActivity(intent);
            }
        });

        diaryRecyclerView.setAdapter(mAdapter);
        return diaryView;
    }

    private List<Writing> getDiaryData() {
        WritingDao writingDao = new WritingDao(getActivity());
        List<Writing> myTotalDiary = writingDao.getMyTotalDiary();
        return myTotalDiary;
    }
}
