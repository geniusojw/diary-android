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

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.DiaryReadActivity;
import org.jerrioh.diary.activity.LetterReadActivity;
import org.jerrioh.diary.adapter.DiaryRecyclerViewAdapter;
import org.jerrioh.diary.adapter.LetterRecyclerViewAdapter;
import org.jerrioh.diary.db.LetterDao;
import org.jerrioh.diary.dbmodel.Diary;
import org.jerrioh.diary.dbmodel.Letter;

import java.util.List;

public class LetterFragment extends Fragment {
    private static final String TAG = "LetterFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LetterDao letterDao = new LetterDao(getActivity());
        final List<Letter> letterData = letterDao.getTotalLetterToMe();
        Log.d(TAG, "letterData.size()=" + letterData.size());

        final LetterRecyclerViewAdapter mAdapter = new LetterRecyclerViewAdapter(letterData, pos -> {
            Intent intent = new Intent(getActivity(), LetterReadActivity.class);

            Letter letter = letterData.get(pos);
            intent.putExtra("letter", letter);
            startActivity(intent);
        });

        View diaryView = inflater.inflate(R.layout.fragment_letter, container, false);
        RecyclerView diaryRecyclerView = diaryView.findViewById(R.id.letter_recycler_view);

        diaryRecyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        diaryRecyclerView.setLayoutManager(layoutManager);
        diaryRecyclerView.setAdapter(mAdapter);

        return diaryView;
    }
}
