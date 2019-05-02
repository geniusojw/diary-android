package org.jerrioh.diary.activity;

import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.config.Information;
import org.jerrioh.diary.db.DiaryDao;
import org.jerrioh.diary.db.LetterDao;
import org.jerrioh.diary.dbmodel.Diary;
import org.jerrioh.diary.dbmodel.Letter;
import org.jerrioh.diary.fragment.DiaryFragment;
import org.jerrioh.diary.fragment.LetterFragment;
import org.jerrioh.diary.fragment.TodayFragment;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.StringUtil;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private String diaryYyyyMM = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 드로우 네비게이션 (열기)
        ImageView imageView = findViewById(R.id.main_banner_draw_menu);
        imageView.setClickable(true);
        imageView.setOnClickListener(v -> {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (!drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.openDrawer(GravityCompat.START);

                TextView nickname = findViewById(R.id.userNickname);
                nickname.setText(Information.getAccount(this).getNickname());

                TextView description = findViewById(R.id.userDescription);
                description.setText(Information.getAccount(this).getDescription());
            }
        });

        // 드로우 네비게이션 (메뉴 선택 리스너)
        NavigationView drawNav = findViewById(R.id.drawer_navigation);
        drawNav.setNavigationItemSelectedListener(menu -> {
            int id = menu.getItemId();
            if (id == R.id.nav_login) {
                Intent accountIntent = new Intent(this, AccountActivity.class);
                startActivity(accountIntent);

            } else if (id == R.id.nav_alias) {
                testcode();
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
            Fragment fragment = getFragmentByNavId(menuItem.getItemId(), null);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commit();

            return true;
        });

        // 다이어리 fragment일때 배너의 쬐그만 버튼 2개 (월 조정)
        View leftButton = findViewById(R.id.main_banner_content_mid_left_button);
        View rightButton = findViewById(R.id.main_banner_content_mid_right_button);
        leftButton.setOnClickListener(v -> {
            String yyyyMM = DateUtil.diffMonth(diaryYyyyMM, -1);
            Fragment fragment = getFragmentByNavId(R.id.nav_diary, yyyyMM);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commit();
        });
        rightButton.setOnClickListener(v -> {
            String yyyyMM = DateUtil.diffMonth(diaryYyyyMM, 1);
            Fragment fragment = getFragmentByNavId(R.id.nav_diary, yyyyMM);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commit();
        });

        // 초기 fragment 세팅 (today)
        Intent intent = getIntent();
        int bottomNavId = intent.getIntExtra("initNavId", R.id.nav_today);

        bottomNav.setSelectedItemId(bottomNavId);
        Fragment initFragment = getFragmentByNavId(bottomNavId, null);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, initFragment).commit();

        Information.updateMyAccount(this);
    }

    private Fragment getFragmentByNavId(int bottomNavId, String _diaryYyyyMM) {
        String mainBannerText;
        int weatherImageResource;
        //View.OnClickListener listener;
        Fragment fragment;

        View leftbutton = findViewById(R.id.main_banner_content_mid_left_button);
        View rightbutton = findViewById(R.id.main_banner_content_mid_right_button);
        leftbutton.setVisibility(View.INVISIBLE); leftbutton.setClickable(false);
        rightbutton.setVisibility(View.INVISIBLE); rightbutton.setClickable(false);

        if (bottomNavId == R.id.nav_today) {
            fragment = new TodayFragment();
            //mainBannerText = DateUtil.getDayString(System.currentTimeMillis(), Locale.CHINA);
            mainBannerText = DateUtil.getDayString(System.currentTimeMillis(), Locale.getDefault());
            weatherImageResource = R.drawable.ic_wb_sunny_black_24dp;
            //listener = v -> { System.out.println("tbd"); };

        } else if (bottomNavId == R.id.nav_diary) {
            fragment = new DiaryFragment();
            if (StringUtil.isEmpty(_diaryYyyyMM)) {
                diaryYyyyMM = DateUtil.getyyyyMMdd().substring(0, 6);
            } else {
                diaryYyyyMM = _diaryYyyyMM;
            }

            Bundle args = new Bundle();
            args.putString("diplay_yyyyMM", diaryYyyyMM);
            fragment.setArguments(args);
            //mainBannerText = DateUtil.getDayString_yyyyMM(System.currentTimeMillis(), Locale.getDefault());
            mainBannerText = DateUtil.getDayString_yyyyMM(diaryYyyyMM, Locale.getDefault());

            weatherImageResource = R.drawable.ic_search_black_24dp;
            //listener = v -> { System.out.println("tbd"); };
            leftbutton.setVisibility(View.VISIBLE); leftbutton.setClickable(true);
            rightbutton.setVisibility(View.VISIBLE); rightbutton.setClickable(true);

        } else if (bottomNavId == R.id.nav_letter) {
            fragment = new LetterFragment();
            mainBannerText = DateUtil.getDayString(System.currentTimeMillis(), Locale.getDefault());
            weatherImageResource = R.drawable.ic_search_black_24dp;
            //listener = v -> { System.out.println("tbd"); };

        } else {
            fragment = new TodayFragment();
            Log.d(TAG, "unknown bottomNavId. bottomNavId=" + bottomNavId);
            mainBannerText = DateUtil.getDayString(System.currentTimeMillis(), Locale.CHINA);
            weatherImageResource = R.drawable.ic_wb_sunny_black_24dp;
            //listener = v -> { System.out.println("tbd"); };
        }

        // 1. 배너 세팅 (오늘 날짜, 이번 달, 편지 쓸 사람 목록)
        TextView mainBannerTextView = findViewById(R.id.main_banner_content_mid_text);
        mainBannerTextView.setText(mainBannerText);

        // 2. weatherImageResource -> 배너 우측 날씨, 달력, 행운의 편지
        ImageView weatherView = findViewById(R.id.main_banner_weather);
        weatherView.setClickable(true);
        weatherView.setImageResource(weatherImageResource);
        //weatherView.setOnClickListener(listener);

        return fragment;
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

    private void testcode() {

        Log.d(TAG, "test code started.");
        Toast.makeText(this, "developer is genius", Toast.LENGTH_LONG).show();

         /*
        DiaryDao writeDao = new DiaryDao(this);
        Diary diary1 = new Diary("20190411", Information.account.getUserId(), "today is good day", "i don't know.", 0);
        Diary diary2 = new Diary("20190410", Information.account.getUserId(), "today's diary", "", 0);
        Diary diary3 = new Diary("20190409", Information.account.getUserId(), "4.9 hangul eul sseul su ub da", "font size\n" +
                "alias\n" + "member registration & login\n" + "do not share diary\n" + "background image\n" +
                "export diary\n" + "alarm\n" + "lullaby\n" + "change screen lock password\n" + "no receive fcm push", 0);
        Diary diary4 = new Diary("20190412", Information.account.getUserId(), "abc\n", "abcd12", 0);
        Diary diary5 = new Diary("20190430", Information.account.getUserId(), "title today", "DFASDFfsdflkaj sdk;lfadsf", 0);

        writeDao.insertWrite(diary1);
        writeDao.insertWrite(diary2);
        writeDao.insertWrite(diary3);
        writeDao.insertWrite(diary4);
        writeDao.insertWrite(diary5);


        LetterDao letterDao = new LetterDao(this);
        Letter letter1 = new Letter(String.valueOf(System.currentTimeMillis() - 100L), "jw@hanmail.com", Information.account.getUserId(), "letter title1", "i am find thank you", 0, 0);
        Letter letter2 = new Letter(String.valueOf(System.currentTimeMillis() - 200L), "jw@hanmail2.com", Information.account.getUserId(), "letter title2", "thank you and you", 0, 0);
        Letter letter3 = new Letter(String.valueOf(System.currentTimeMillis() - 300L), "jw@hanmail3.com", Information.account.getUserId(), "letter title3", "", 0, 0);

        letterDao.insertLetter(letter1);
        letterDao.insertLetter(letter2);
        letterDao.insertLetter(letter3); */
    }
}
