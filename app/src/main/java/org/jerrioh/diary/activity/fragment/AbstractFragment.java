package org.jerrioh.diary.activity.fragment;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.main.DiaryWriteActivity;
import org.jerrioh.diary.activity.pop.DiaryWriteStartPopActivity;
import org.jerrioh.diary.activity.main.LetterWriteActivity;
import org.jerrioh.diary.activity.pop.PostWritePopActivity;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.PostApis;
import org.jerrioh.diary.config.Constants;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.model.PostIt;
import org.jerrioh.diary.model.db.DiaryDao;
import org.jerrioh.diary.util.DateUtil;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class AbstractFragment extends Fragment {

    public static final int BUTTON_TYPE_DIARY = 0;
    public static final int BUTTON_TYPE_STORE = 1;
    public static final int BUTTON_TYPE_SQUARE = 2;

    protected  void setFloatingActionButton(int buttonType) {
        View writeButton = getActivity().findViewById(R.id.floating_action_button_write_diary);
        writeButton.setVisibility(View.VISIBLE);
        writeButton.setEnabled(true);
        writeButton.setClickable(true);

        if (!timeToSleep()) {
            if (buttonType == 0) {
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

            } else if (buttonType == 1) {
                writeButton.setVisibility(View.GONE);

            } else if (buttonType == 2) {
                writeButton.setOnClickListener(view -> {
                    PostApis postApis = new PostApis(getActivity());
                    postApis.hasPost(new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                JSONObject data = jsonObject.getJSONObject("data");

                                PostIt postIt = new PostIt();
                                postIt.setPostId(data.getString("postId"));
                                postIt.setChocolates(data.getInt("chocolates"));
                                postIt.setAuthorNickname(data.getString("authorNickname"));
                                postIt.setContent(data.getString("content"));
                                postIt.setWrittenTime(data.getLong("writtenTime"));

                                Intent intent = new Intent(getActivity(), PostWritePopActivity.class);
                                intent.putExtra("post", postIt);
                                startActivity(intent);

                            } else if (httpStatus == 404) {
                                Toast.makeText(getActivity(), "상점에서 광장발언권을 구매하세요.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                });
                ((FloatingActionButton) writeButton).setImageResource(R.drawable.ic_chat_black_24dp);
            }

        } else {
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
        }
    }

    public boolean timeToSleep() {
        return !(Integer.parseInt(DateUtil.getHHmmss()) >= Constants.PROHIBIT_DIARY_WRITE_HHMMSS);
    }
}
