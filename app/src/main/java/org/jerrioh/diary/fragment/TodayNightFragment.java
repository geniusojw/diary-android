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

public class TodayNightFragment extends MainActivityFragment {
        private static final String TAG = "TodayNightFragment";

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View todayView =  inflater.inflate(R.layout.fragment_today_night, container, false);




            setDiaryWriteButton(false);
            return todayView;
        }
}
