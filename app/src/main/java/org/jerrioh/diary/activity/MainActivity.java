package org.jerrioh.diary.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jerrioh.diary.R;
import org.jerrioh.diary.config.Config;
import org.jerrioh.diary.config.Information;
import org.jerrioh.diary.db.WriteDao;
import org.jerrioh.diary.dbmodel.Write;
import org.jerrioh.diary.fragment.DiaryFragment;
import org.jerrioh.diary.fragment.LetterFragment;
import org.jerrioh.diary.fragment.TodayFragment;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.DiaryApiUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 드로우 네비게이션 (열기)
        ImageView imageView = findViewById(R.id.main_banner_draw_menu);
        imageView.setClickable(true);
        imageView.setOnClickListener(v -> {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (!drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        // 드로우 네비게이션 (메뉴 선택 리스너)
        NavigationView drawNav = findViewById(R.id.drawer_navigation);
        drawNav.setNavigationItemSelectedListener(menu -> {
            int id = menu.getItemId();
            if (id == R.id.developer_test) {
                Log.d(TAG, "test code started.");
                /*WriteDao writeDao = new WriteDao(this);
                Write write1 = new Write(Write.WriteType.DIARY, "20190411", "GUEST_JW", "GUEST_JW", "today is good day", "i don't know.");
                Write write2 = new Write(Write.WriteType.DIARY, "20190410", "GUEST_JW", "GUEST_JW", "today's diary", "");
                Write write3 = new Write(Write.WriteType.DIARY, "20190409", "GUEST_JW", "GUEST_JW", "4.9 hangul eul sseul su ub da", "font size\n" +
                        "alias\n" + "member registration & login\n" + "do not share diary\n" + "background image\n" +
                        "export diary\n" + "alarm\n" + "lullaby\n" + "change screen lock password\n" + "no receive fcm push");
                Write write4 = new Write(Write.WriteType.DIARY, "20190412", "GUEST_JW", "GUEST_JW", "abc\n", "abcd12");
                Write write5 = new Write(Write.WriteType.DIARY, "20190430", "GUEST_JW", "GUEST_JW", "title today", "DFASDFfsdflkaj sdk;lfadsf");

                writeDao.insertWrite(write1);
                writeDao.insertWrite(write2);
                writeDao.insertWrite(write3);
                writeDao.insertWrite(write4);
                writeDao.insertWrite(write5);
                Toast.makeText(this, "developer is genius", Toast.LENGTH_LONG);*/
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        // 바텀 네비게이션 (메뉴 선택 리스너)
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

        // 초기 fragment 세팅 (today)
        Intent intent = getIntent();
        int initNavId = intent.getIntExtra("initNavId", R.id.nav_today);

        bottomNav.setSelectedItemId(initNavId);
        Fragment initFragment = getFragmentByNavId(initNavId);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, initFragment).commit();

        // 배너 세팅
        TextView textView = findViewById(R.id.main_banner_content_mid);
        //textView.setText(DateUtil.getTodayDateString(System.currentTimeMillis(), Locale.KOREAN));
        textView.setText(DateUtil.getTodayDateString(System.currentTimeMillis(), Locale.CHINA));

        Information.updateMyAccount(this);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer_navigation, menu);
        return true;
    }
}
