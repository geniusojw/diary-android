package org.jerrioh.diary.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.jerrioh.diary.R;
import org.jerrioh.diary.fragment.DiaryFragment;
import org.jerrioh.diary.fragment.LetterFragment;
import org.jerrioh.diary.fragment.TodayFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 바텀 네비게이션
        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener((menuItem) -> {
            if (menuItem.getItemId() == bottomNav.getSelectedItemId()) {
                return false;
            }
            Fragment fragment = getFragmentByNavId(menuItem.getItemId());

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commit();

            return true;
        });

        Intent intent = getIntent();
        int initNavId = intent.getIntExtra("initNavId", R.id.nav_today);

        bottomNav.setSelectedItemId(initNavId);
        Fragment initFragment = getFragmentByNavId(initNavId);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, initFragment).commit();
    }

    private Fragment getFragmentByNavId(int bottomNavId) {
        if (bottomNavId == R.id.nav_today) {
            return new TodayFragment();
        } else if (bottomNavId == R.id.nav_diary) {
            return new DiaryFragment();
        } else if (bottomNavId == R.id.nav_letter) {
            return new LetterFragment();
        } else {
            Log.d(TAG, "unknown bottomNavId. bottomNavId=" + bottomNavId);
            return new TodayFragment();
        }
    }
}
