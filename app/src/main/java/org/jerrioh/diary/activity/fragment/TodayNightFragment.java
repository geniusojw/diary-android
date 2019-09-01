package org.jerrioh.diary.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jerrioh.diary.R;

public class TodayNightFragment extends AbstractFragment {
        private static final String TAG = "TodayNightFragment";

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View todayView =  inflater.inflate(R.layout.fragment_today_night, container, false);




            super.setFloatingActionButton(AbstractFragment.BUTTON_TYPE_DIARY);
            return todayView;
        }
}
