package org.jerrioh.diary.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.main.DiaryGroupPopActivity;
import org.jerrioh.diary.model.DiaryGroup;
import org.jerrioh.diary.model.db.DiaryGroupDao;
import org.jerrioh.diary.util.DateUtil;

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
            if (currentTime < diaryGroup.getStartTime()) {
                String period = DateUtil.getDateString_group(diaryGroup.getStartTime()) + " ~ " + DateUtil.getDateString_group(diaryGroup.getEndTime());
                diaryGroupNameView.setText(diaryGroup.getDiaryGroupName());
                diaryGroupEndTimeView.setText(period);

                diaryGroupLayout.setVisibility(View.VISIBLE);
                diaryGroupLayout.setOnClickListener(view -> {
                    Intent intent = new Intent(getActivity(), DiaryGroupPopActivity.class);
                    startActivity(intent);
                });

            } else if (currentTime < diaryGroup.getEndTime()) {
                String period = DateUtil.getDateString_group(diaryGroup.getStartTime()) + " ~ " + DateUtil.getDateString_group(diaryGroup.getEndTime());
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

        setDiaryWriteButton(true);
        return todayView;
    }
}
