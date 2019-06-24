package org.jerrioh.diary.activity.fragment;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jerrioh.diary.R;
import org.jerrioh.diary.util.ThemeUtil;

public class TodayFragment extends AbstractFragment {
    private static final String TAG = "TodayFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 쓰기 버튼 활성화
        View todayView =  inflater.inflate(R.layout.fragment_today, container, false);

        setDiaryWriteButton(true, BUTTON_TYPE_WRITE_DIARY);

        BitmapDrawable bitmap = ThemeUtil.getBitmapDrawablePattern(this, 0);
        todayView.setBackgroundDrawable(bitmap);

        return todayView;
    }
}
