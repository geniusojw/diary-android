package org.jerrioh.diary.activity.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jerrioh.diary.R;

public class TodayNightFragment extends AbstractFragment {
        private static final String TAG = "TodayNightFragment";

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View todayView =  inflater.inflate(R.layout.fragment_today_night, container, false);




            setDiaryWriteButton(false, BUTTON_TYPE_WRITE_DIARY);
            return todayView;
        }
}
