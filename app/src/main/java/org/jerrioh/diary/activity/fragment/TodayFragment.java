package org.jerrioh.diary.activity.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.main.DiaryGroupPopActivity;
import org.jerrioh.diary.model.DiaryGroup;
import org.jerrioh.diary.model.db.DiaryGroupDao;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.ThemeUtil;

import java.util.concurrent.TimeUnit;

import static org.jerrioh.diary.R.id.relative_layout_today_anonymous_diary_group;
import static org.jerrioh.diary.R.id.text_view_today_anonymous_diary_group_end_time;
import static org.jerrioh.diary.R.id.text_view_today_anonymous_diary_group_name;

public class TodayFragment extends MainActivityFragment {
    private static final String TAG = "TodayFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 쓰기 버튼 활성화
        View todayView =  inflater.inflate(R.layout.fragment_today, container, false);

        DiaryGroupDao diaryGroupDao = new DiaryGroupDao(getActivity());
        DiaryGroup diaryGroup = diaryGroupDao.getDiaryGroup();

        RelativeLayout diaryGroupLayout = todayView.findViewById(relative_layout_today_anonymous_diary_group);
        TextView diaryGroupNameView = todayView.findViewById(text_view_today_anonymous_diary_group_name);
        TextView diaryGroupEndTimeView = todayView.findViewById(text_view_today_anonymous_diary_group_end_time);

        if (diaryGroup != null) {
            long currentTime = System.currentTimeMillis();
            String start = DateUtil.getDateStringSkipYear(diaryGroup.getStartTime());
            String end = DateUtil.getDateStringSkipYear(diaryGroup.getEndTime() - TimeUnit.MINUTES.toMillis(1));
            String period = start + " ~ " + end;

            if (currentTime < diaryGroup.getStartTime()) {
                diaryGroupNameView.setText(diaryGroup.getDiaryGroupName());
                diaryGroupEndTimeView.setText(period);

                diaryGroupLayout.setVisibility(View.VISIBLE);
                diaryGroupLayout.setOnClickListener(view -> {
                    Toast.makeText(getActivity(), "시작시간 : " + start, Toast.LENGTH_SHORT).show();
                });

            } else if (currentTime < diaryGroup.getEndTime()) {
                diaryGroupNameView.setText(diaryGroup.getDiaryGroupName());
                diaryGroupEndTimeView.setText(period);

                diaryGroupLayout.setVisibility(View.VISIBLE);
                diaryGroupLayout.setOnClickListener(view -> {
                    Intent intent = new Intent(getActivity(), DiaryGroupPopActivity.class);
                    startActivity(intent);
                });

            } else {
                diaryGroupLayout.setVisibility(View.GONE);
                diaryGroupDao.deleteDiaryGroup();
            }
        }

        setDiaryWriteButton(true, BUTTON_TYPE_WRITE_DIARY);

        BitmapDrawable bitmap = ThemeUtil.getBitmapDrawablePattern(this, 0);
        todayView.setBackgroundDrawable(bitmap);

        return todayView;
    }
}
