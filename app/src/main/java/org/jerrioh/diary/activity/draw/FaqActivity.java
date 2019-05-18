package org.jerrioh.diary.activity.draw;

import android.os.Bundle;

import org.jerrioh.diary.R;

public class FaqActivity extends CommonActionBarActivity {
    private static final String TAG = "FaqActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        setCommonToolBar("FAQ");


    }
}
