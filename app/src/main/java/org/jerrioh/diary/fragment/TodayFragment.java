package org.jerrioh.diary.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.TodayPeopleActivity;
import org.jerrioh.diary.activity.TodayWriteActivity;

public class TodayFragment extends Fragment {
    private static final String TAG = "TodayFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 쓰기 버튼 활성화
        View todayView =  inflater.inflate(R.layout.fragment_today, container, false);

        LinearLayout diaryGroupButton = todayView.findViewById(R.id.diary_group_button);
        diaryGroupButton.setClickable(true);
        diaryGroupButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), TodayPeopleActivity.class);
            startActivity(intent);
        });

        View writeButton = todayView.findViewById(R.id.write_diary_button);
        writeButton.setEnabled(true);
        writeButton.setClickable(true);
        writeButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), TodayWriteActivity.class);
            startActivity(intent);
        });

        return todayView;
    }
}
