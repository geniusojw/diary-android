package org.jerrioh.diary.activity.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.main.LetterReadActivity;
import org.jerrioh.diary.activity.adapter.LetterRecyclerViewAdapter;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.AuthorLetterApis;
import org.jerrioh.diary.api.author.DiaryGroupApis;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.db.LetterDao;
import org.jerrioh.diary.model.Letter;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.ThemeUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LetterFragment extends MainActivityFragment {
    private static final String TAG = "LetterFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        boolean lettersToMe = true;
        boolean refreshed = false;

        Bundle args = getArguments();
        if (args != null) {
            lettersToMe = args.getBoolean("lettersToMe", true);
            refreshed = args.getBoolean("refreshed", false);
        }

        Author author = AuthorUtil.getAuthor(getActivity());
        LetterDao letterDao = new LetterDao(getActivity());
        final List<Letter> letterData;
        if (lettersToMe) {
            letterData = letterDao.getLettersToMe(author.getAuthorId());
        } else {
            letterData = letterDao.getLettersToOthers(author.getAuthorId());
        }

        if (!refreshed) { // async
            getLetters(author, lettersToMe);
        }

        Log.d(TAG, "letterData.size()=" + letterData.size());

        final LetterRecyclerViewAdapter mAdapter = new LetterRecyclerViewAdapter(letterData, lettersToMe, pos -> {
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

        setDiaryWriteButton(true, BUTTON_TYPE_WRITE_LETTER);

        BitmapDrawable bitmap = ThemeUtil.getBitmapDrawablePattern(this, 2);
        letterView.setBackgroundDrawable(bitmap);

        return letterView;
    }

    private void getLetters(Author author, boolean lettersToMe) {
        String range = lettersToMe ? "in" : "out";

        AuthorLetterApis apis = new AuthorLetterApis(getActivity());
        apis.receive(range, new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                if (httpStatus == 200) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.length() > 0) {
                        LetterDao letterDao = new LetterDao(getActivity());

                        List<Letter> letters = letterDao.getAllLetters();
                        Set<String> letterIds = new HashSet<>();
                        for (Letter letter : letters) {
                            letterIds.add(letter.getLetterId());
                        }

                        for (int index = 0; index < jsonArray.length(); index++) {
                            JSONObject letterResponse = jsonArray.getJSONObject(index);
                            String letterId = letterResponse.getString("letterId");
                            if (letterIds.contains(letterId)) {
                                continue;
                            }

                            Letter newLetter = new Letter();
                            newLetter.setLetterId(letterId);
                            newLetter.setLetterType(letterResponse.getInt("letterType"));
                            newLetter.setFromAuthorId(letterResponse.getString("fromAuthorId"));
                            newLetter.setFromAuthorNickname(letterResponse.getString("fromAuthorNickname"));
                            newLetter.setToAuthorId(letterResponse.getString("toAuthorId"));
                            newLetter.setToAuthorNickname(letterResponse.getString("toAuthorNickname"));
                            newLetter.setContent(letterResponse.getString("content"));
                            newLetter.setWrittenTime(letterResponse.getLong("writtenTime"));

                            newLetter.setStatus(lettersToMe ? Letter.LetterStatus.UNREAD : Letter.LetterStatus.REPLIED);
                            letterDao.insertLetter(newLetter);
                        }

                        Bundle args = new Bundle();
                        args.putBoolean("lettersToMe", lettersToMe);
                        args.putBoolean("refreshed", true);

                        Fragment fragment = LetterFragment.this;
                        fragment.setArguments(args);

                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.detach(fragment);
                        fragmentTransaction.attach(fragment);
                        fragmentTransaction.commit();
                    }
                }
            }
        });
    }
}
