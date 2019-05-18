package org.jerrioh.diary.activity.fragment;

import android.content.Intent;
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
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.AuthorLetterApis;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.db.LetterDao;
import org.jerrioh.diary.model.Letter;
import org.jerrioh.diary.util.AuthorUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LetterFragment extends MainActivityFragment {
    private static final String TAG = "LetterFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Author author = AuthorUtil.getAuthor(getActivity());

        AuthorLetterApis apis = new AuthorLetterApis(getActivity());
        apis.receive(new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                if (httpStatus == 200) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.length() > 0) {
                        LetterDao letterDao = new LetterDao(getActivity());

                        List<Letter> letters = letterDao.getLettersToMe(author.getAuthorId());
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
                            newLetter.setFromAuthorId(letterResponse.getString("fromAuthorId"));
                            newLetter.setToAuthorId(author.getAuthorId());
                            newLetter.setTitle(letterResponse.getString("title"));
                            newLetter.setContent(letterResponse.getString("content"));
                            newLetter.setWrittenTime(letterResponse.getLong("writtenTime"));
                            newLetter.setStatus(Letter.DiaryStatus.UNREAD);

                            letterDao.insertLetter(newLetter);
                        }
                    }
                }
            }
        });

        LetterDao letterDao = new LetterDao(getActivity());
        final List<Letter> letterData = letterDao.getLettersToMe(author.getAuthorId());
        Log.d(TAG, "letterData.size()=" + letterData.size());

        final LetterRecyclerViewAdapter mAdapter = new LetterRecyclerViewAdapter(letterData, pos -> {
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

        setDiaryWriteButton(true);
        return letterView;
    }
}
