package org.jerrioh.diary.activity.fragment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.adapter.SquareRecyclerViewAdapter;
import org.jerrioh.diary.activity.pop.PostReadPopActivity;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.PostApis;
import org.jerrioh.diary.model.PostIt;
import org.jerrioh.diary.util.ThemeUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SquareFragment extends AbstractFragment {
    private static final String TAG = "SquareFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View squareView = inflater.inflate(R.layout.fragment_square, container, false);

        PostApis postApis = new PostApis(getActivity());
        postApis.get(new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                if (httpStatus == 200) {
                    List<List<PostIt>> postIts = new ArrayList<>();

                    List<PostIt> row = new ArrayList<>();
                    JSONArray data = jsonObject.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        if (row == null) {
                            row = new ArrayList<>();
                        }
                        JSONObject postObject = data.getJSONObject(i);
                        PostIt postIt = new PostIt();
                        postIt.setPostId(postObject.getString("postId"));
                        postIt.setAuthorNickname(postObject.getString("authorNickname"));
                        postIt.setChocolates(postObject.getInt("chocolates"));
                        postIt.setContent(postObject.getString("content"));
                        postIt.setWrittenTime(postObject.getLong("writtenTime"));

                        row.add(postIt);

                        if (row.size() == 3) {
                            postIts.add(row);
                            row = null;
                        }
                    }
                    if (row != null) {
                        postIts.add(row);
                    }

                    final SquareRecyclerViewAdapter mAdapter = new SquareRecyclerViewAdapter(postIts, getActivity());

                    RecyclerView recyclerView = squareView.findViewById(R.id.square_recycler_view);

                    recyclerView.setHasFixedSize(false);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(mAdapter);
                }
            }
        });

        super.setFloatingActionButton(AbstractFragment.BUTTON_TYPE_SQUARE);

        BitmapDrawable bitmap = ThemeUtil.getBitmapDrawablePattern(this, 2);
        squareView.setBackgroundDrawable(bitmap);

        return squareView;
    }
}
