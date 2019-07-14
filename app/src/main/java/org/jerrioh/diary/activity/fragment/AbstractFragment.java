package org.jerrioh.diary.activity.fragment;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.main.DiaryWriteActivity;
import org.jerrioh.diary.activity.pop.DiaryWriteStartPopActivity;
import org.jerrioh.diary.activity.pop.PostWritePopActivity;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.PostApis;
import org.jerrioh.diary.config.Constants;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.model.Post;
import org.jerrioh.diary.model.db.DiaryDao;
import org.jerrioh.diary.model.db.PostDao;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.DateUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public abstract class AbstractFragment extends Fragment {

    public static final int BUTTON_TYPE_DIARY = 0;
    public static final int BUTTON_TYPE_STORE = 1;
    public static final int BUTTON_TYPE_SQUARE_PRIVATE = 2;
    public static final int BUTTON_TYPE_SQUARE_PUBLIC = 3;

    protected  void setFloatingActionButton(int buttonType) {
        View writeButton = getActivity().findViewById(R.id.floating_action_button_write_diary);
        writeButton.setVisibility(View.VISIBLE);
        writeButton.setEnabled(true);
        writeButton.setClickable(true);

//        if (!timeToSleep()) {
            if (buttonType == BUTTON_TYPE_DIARY) {
                writeButton.setOnClickListener(view -> {
                    DiaryDao diaryDao = new DiaryDao(getActivity());
                    String today_yyyyMMdd = DateUtil.getyyyyMMdd();
                    Diary todayDiary = diaryDao.getDiary(today_yyyyMMdd);

                    Intent intent;
                    if (todayDiary != null) {
                        intent = new Intent(getActivity(), DiaryWriteActivity.class);
                    } else {
                        intent = new Intent(getActivity(), DiaryWriteStartPopActivity.class);
                        writeButton.setVisibility(View.GONE);
                    }
                    startActivity(intent);
                });
                ((FloatingActionButton) writeButton).setImageResource(R.drawable.ic_edit_black_48dp);

            } else if (buttonType == BUTTON_TYPE_STORE) {
                writeButton.setVisibility(View.GONE);

            } else if (buttonType == BUTTON_TYPE_SQUARE_PRIVATE) {
                writeButton.setOnClickListener(view -> {
                    int count = new PostDao(getActivity()).getAllPosts().size();
                    if (count >= 15) {
                        Toast.makeText(getActivity(), getActivity().getString(R.string.post_too_many_post), Toast.LENGTH_SHORT).show();
                    } else {
                        Post post = new Post();
                        post.setPostId(UUID.randomUUID().toString());
                        post.setChocolates(-1);
                        post.setAuthorNickname(AuthorUtil.getAuthor().getNickname());
                        post.setContent("");
                        post.setWrittenTime(0L);

                        Intent intent = new Intent(getActivity(), PostWritePopActivity.class);
                        intent.putExtra("post", post);
                        startActivity(intent);
                    }
                });
                ((FloatingActionButton) writeButton).setImageResource(R.drawable.ic_chat_black_24dp);

            } else if (buttonType == BUTTON_TYPE_SQUARE_PUBLIC) {
                writeButton.setOnClickListener(view -> {
                    PostApis postApis = new PostApis(getActivity());
                    postApis.hasPost(new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                JSONObject data = jsonObject.getJSONObject("data");

                                Post post = new Post();
                                post.setPostId(data.getString("postId"));
                                post.setChocolates(data.getInt("chocolates"));
                                post.setAuthorNickname(data.getString("authorNickname"));
                                post.setContent(data.getString("content"));
                                post.setWrittenTime(data.getLong("writtenTime"));

                                Intent intent = new Intent(getActivity(), PostWritePopActivity.class);
                                intent.putExtra("post", post);
                                startActivity(intent);

                            } else if (httpStatus == 404) {
                                Toast.makeText(getActivity(), getActivity().getString(R.string.post_no_public_post_it), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                });
                ((FloatingActionButton) writeButton).setImageResource(R.drawable.ic_chat_black_24dp);
            }

//        } else {
//            writeButton.setOnClickListener(view -> {
//                DiaryDao diaryDao = new DiaryDao(getActivity());
//                String today_yyyyMMdd = DateUtil.getyyyyMMdd();
//                Diary todayDiary = diaryDao.getDiary(today_yyyyMMdd);
//
//                Intent intent;
//                if (todayDiary != null) {
//                    intent = new Intent(getActivity(), DiaryWriteActivity.class);
//                } else {
//                    intent = new Intent(getActivity(), DiaryWriteStartPopActivity.class);
//                    writeButton.setVisibility(View.GONE);
//                }
//                startActivity(intent);
//            });
//            ((FloatingActionButton) writeButton).setImageResource(R.drawable.ic_edit_black_48dp);
//        }
    }

    public boolean timeToSleep() {
        return !(Integer.parseInt(DateUtil.getHHmmss()) >= Constants.PROHIBIT_DIARY_WRITE_HHMMSS);
    }
}
