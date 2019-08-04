package org.jerrioh.diary.activity.fragment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.main.LetterReadActivity;
import org.jerrioh.diary.activity.adapter.LetterRecyclerViewAdapter;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.db.LetterDao;
import org.jerrioh.diary.model.Letter;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.ThemeUtil;

import java.util.List;

public class LetterFragment extends AbstractFragment {
    private static final String TAG = "LetterFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        boolean lettersToMe = true;

        Bundle args = getArguments();
        if (args != null) {
            lettersToMe = args.getBoolean("lettersToMe", true);
        }

        Author author = AuthorUtil.getAuthor(getActivity());
        LetterDao letterDao = new LetterDao(getActivity());
        final List<Letter> letterData = letterDao.getAllLetters(true);

        Log.d(TAG, "letterData.size()=" + letterData.size());

        final LetterRecyclerViewAdapter mAdapter = new LetterRecyclerViewAdapter(getActivity(), letterData, pos -> {
            Intent intent = new Intent(getActivity(), LetterReadActivity.class);

            Letter letter = letterData.get(pos);
            intent.putExtra("letter", letter);
            startActivity(intent);
        });

        View letterView = inflater.inflate(R.layout.fragment_letter, container, false);
        RecyclerView diaryRecyclerView = letterView.findViewById(R.id.letter_recycler_view);

        diaryRecyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        diaryRecyclerView.setLayoutManager(layoutManager);
        diaryRecyclerView.setAdapter(mAdapter);

        super.setFloatingActionButton(AbstractFragment.BUTTON_TYPE_INVISIBLE);

        BitmapDrawable bitmap = ThemeUtil.getBitmapDrawablePattern(this, 0);
        letterView.setBackgroundDrawable(bitmap);

        return letterView;
    }
}
