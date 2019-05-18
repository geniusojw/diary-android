package org.jerrioh.diary.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.main.AnonymousDiaryActivity;

public class TodayFragment extends MainActivityFragment {
    private static final String TAG = "TodayFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 쓰기 버튼 활성화
        View todayView =  inflater.inflate(R.layout.fragment_today, container, false);

        RelativeLayout diaryGroupLayout = todayView.findViewById(R.id.relative_layout_today_anonymous_diary_group);
        diaryGroupLayout.setClickable(true);
        diaryGroupLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AnonymousDiaryActivity.class);
            startActivity(intent);
        });

        setDiaryWriteButton(true);
        return todayView;
    }
}
