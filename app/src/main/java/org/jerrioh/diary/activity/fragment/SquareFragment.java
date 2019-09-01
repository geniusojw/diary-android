package org.jerrioh.diary.activity.fragment;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.adapter.SquareRecyclerViewAdapter;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.PostApis;
import org.jerrioh.diary.model.Post;
import org.jerrioh.diary.model.db.LetterDao;
import org.jerrioh.diary.model.db.PostDao;
import org.jerrioh.diary.util.ThemeUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SquareFragment extends AbstractFragment {
    private static final String TAG = "SquareFragment";

    private View squareView;
    private String squareType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        squareView = inflater.inflate(R.layout.fragment_square, container, false);

        Bundle args = getArguments();
        squareType = args.getString("squareType");

        if ("PUBLIC".equals(squareType)) {
            getPublicPosts();
            super.setFloatingActionButton(AbstractFragment.BUTTON_TYPE_SQUARE_PUBLIC);
        } else if ("PRIVATE".equals(squareType)) {
            getPrivatePosts();
            super.setFloatingActionButton(AbstractFragment.BUTTON_TYPE_SQUARE_PRIVATE);
        }


        BitmapDrawable bitmap = ThemeUtil.getBitmapDrawablePattern(this, 2);
        squareView.setBackgroundDrawable(bitmap);

        return squareView;
    }

    @Override
    public void onResume() {
        if ("PUBLIC".equals(squareType)) {
            getPublicPosts();
        } else {
            getPrivatePosts();
        }
        super.onResume();
    }

    private void getPrivatePosts() {
        PostDao postDao = new PostDao(getActivity());
        List<Post> allPosts = postDao.getAllPosts(true);

        List<List<Post>> postIts = new ArrayList<>();


        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < allPosts.size(); i++) {
            Post post = allPosts.get(i);
            posts.add(post);

            if (posts.size() == 3) {
                postIts.add(posts);
                posts = new ArrayList<>();
            }
        }
        if (!posts.isEmpty()) {
            postIts.add(posts);
        }

        setPost(postIts);
    }

    private void getPublicPosts() {
        PostApis postApis = new PostApis(getActivity());
        postApis.get(new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                if (httpStatus == 200) {
                    List<List<Post>> postIts = new ArrayList<>();

                    List<Post> row = new ArrayList<>();
                    JSONArray data = jsonObject.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        if (row == null) {
                            row = new ArrayList<>();
                        }
                        JSONObject postObject = data.getJSONObject(i);
                        Post post = new Post();
                        post.setPostId(postObject.getString("postId"));
                        post.setAuthorNickname(postObject.getString("authorNickname"));
                        post.setChocolates(postObject.getInt("chocolates"));
                        post.setContent(postObject.getString("content"));
                        post.setWrittenTime(postObject.getLong("writtenTime"));

                        row.add(post);

                        if (row.size() == 3) {
                            postIts.add(row);
                            row = null;
                        }
                    }
                    if (row != null) {
                        postIts.add(row);
                    }

                    setPost(postIts);
                }
            }
        });
    }

    private void setPost(List<List<Post>> postIts) {
        final SquareRecyclerViewAdapter mAdapter = new SquareRecyclerViewAdapter(postIts, getActivity());

        RecyclerView recyclerView = squareView.findViewById(R.id.square_recycler_view);

        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }
}
