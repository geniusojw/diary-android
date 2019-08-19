package org.jerrioh.diary.activity.pop;

import android.os.Bundle;

import org.jerrioh.diary.R;

public class SquareBannerPopActivity extends AbstractDiaryPopActivity {

    private static final String TAG = "SquareBannerPopActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square_fragment_pop);
        super.setWindowAttribute(.95f, .9f, 0, -20);
    }
}
