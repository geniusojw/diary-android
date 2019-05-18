package org.jerrioh.diary.activity.draw;

import android.os.Bundle;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.draw.CommonActionBarActivity;

public class ChocolateShopActivity extends CommonActionBarActivity {
    private static final String TAG = "ChocolateShopActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chocolate_shop);
        setCommonToolBar("Chocolate Shop");


    }
}