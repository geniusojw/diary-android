package org.jerrioh.diary.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.config.Constants;
import org.jerrioh.diary.util.CurrentAccountUtil;
import org.jerrioh.diary.db.DiaryDao;
import org.jerrioh.diary.dbmodel.Account;
import org.jerrioh.diary.dbmodel.Diary;
import org.jerrioh.diary.fragment.DiaryFragment;
import org.jerrioh.diary.fragment.LetterFragment;
import org.jerrioh.diary.fragment.TodayFragment;
import org.jerrioh.diary.fragment.TodayNightFragment;
import org.jerrioh.diary.util.DateUtil;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String diaryYyyyMM = "";

    private static final int REQUEST_MEMBER_ACTIVITY = 1;

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        defaultSetting(bottomNav.getSelectedItemId(), diaryYyyyMM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_MEMBER_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                defaultSetting(R.id.nav_today, "");
            }
        }
    }

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

                Account account = CurrentAccountUtil.getAccount(this);

                TextView nickname = findViewById(R.id.userNickname);
                nickname.setText(account.getNickname());

                TextView description = findViewById(R.id.userDescription);
                description.setText(account.getDescription());

                View syncView = findViewById(R.id.drawer_sync);
                if (CurrentAccountUtil.isMember(account.getUserId())) {
                    syncView.setClickable(true);
                    syncView.setVisibility(View.VISIBLE);
                } else {
                    syncView.setClickable(false);
                    syncView.setVisibility(View.GONE);
                }
            }
        });

        // 드로우 네비게이션 (메뉴 선택 리스너)
        NavigationView drawNav = findViewById(R.id.drawer_navigation);
        drawNav.setNavigationItemSelectedListener(menu -> {
            int id = menu.getItemId();
            if (id == R.id.drawer_nav_account) {
                Intent accountIntent = new Intent(this, MemberActivity.class);
                startActivityForResult(accountIntent, REQUEST_MEMBER_ACTIVITY);

            } else if (id == R.id.drawer_nav_setting) {
                Intent accountIntent = new Intent(this, SettingActivity.class);
                startActivity(accountIntent);

            } else if (id == R.id.drawer_nav_faq) {
            } else if (id == R.id.drawer_nav_appInfo) {
                Intent accountIntent = new Intent(this, AppInformationActivity.class);
                startActivity(accountIntent);

            } else if (id == R.id.drawer_nav_share) {
            } else if (id == R.id.drawer_nav_feed_back) {
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        // 바텀 네비게이션 (메뉴 선택 리스너)
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
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
            Diary firstDiary = new DiaryDao(this).getFirstDiary();
            String firstYyyyMM;
            if (firstDiary != null) {
                firstYyyyMM = firstDiary.getWriteDay().substring(0, 6);
            } else {
                firstYyyyMM = DateUtil.getyyyyMMdd().substring(0, 6);
            }
            if (Integer.parseInt(yyyyMM) < Integer.parseInt(firstYyyyMM)) {
                int randomInt = new Random().nextInt(3);
                String message;
                if (randomInt == 0) {
                    message = "No more diaries.";
                } else if (randomInt == 1) {
                    message = "There is no diary in the past.";
                } else {
                    message = "It is the first page of your diary.";
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else {
                Fragment fragment = getFragmentByNavId(R.id.nav_diary, yyyyMM);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment).commit();
            }

        });
        rightButton.setOnClickListener(v -> {
            String yyyyMM = DateUtil.diffMonth(diaryYyyyMM, 1);
            String latestYyyyMM = DateUtil.getyyyyMMdd().substring(0, 6);
            if (Integer.parseInt(yyyyMM) > Integer.parseInt(latestYyyyMM)) {
                int randomInt = new Random().nextInt(3);
                String message;
                if (randomInt == 0) {
                    message = "Do you want to know the future?";
                } else if (randomInt == 1) {
                    message = "Please keep a diary in the future.";
                } else {
                    message = "You can not see the diary tomorrow.";
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else {
                Fragment fragment = getFragmentByNavId(R.id.nav_diary, yyyyMM);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment).commit();
            }
        });

        defaultSetting(R.id.nav_today, "");

        CurrentAccountUtil.updateAccount(this);
    }

    private void defaultSetting(int bottomNavId, String _diaryYyyyMM) {
        // 일기쓰기 버튼
        View writeButton = findViewById(R.id.write_diary_button);
        writeButton.setEnabled(true);
        writeButton.setClickable(true);
        writeButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, TodayWriteActivity.class);
            startActivity(intent);
        });

        // 초기 fragment 세팅 (today)
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(bottomNavId);
        Fragment initFragment = getFragmentByNavId(bottomNavId, _diaryYyyyMM);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, initFragment).commit();
    }

    private Fragment getFragmentByNavId(int bottomNavId, String _diaryYyyyMM) {
        String mainBannerText;
        int weatherImageResource;
        //View.OnClickListener listener;
        Fragment fragment;

        View leftButton = findViewById(R.id.main_banner_content_mid_left_button);
        View rightButton = findViewById(R.id.main_banner_content_mid_right_button);
        leftButton.setVisibility(View.INVISIBLE); leftButton.setClickable(false);
        rightButton.setVisibility(View.INVISIBLE); rightButton.setClickable(false);

        if (bottomNavId == R.id.nav_today) {
            String hhmmss = DateUtil.getHHmmss().substring(0, 6);
            if (Integer.parseInt(hhmmss) >= Constants.DIARY_WRITE_BUTTON_HHMMSS) {
                fragment = new TodayFragment();
            } else {
                fragment = new TodayNightFragment();
            }


            mainBannerText = DateUtil.getDayString(System.currentTimeMillis(), Locale.CHINA);
            //mainBannerText = DateUtil.getDayString(System.currentTimeMillis(), Locale.getDefault());
            weatherImageResource = R.drawable.ic_wb_sunny_black_24dp;
            //listener = v -> { System.out.println("tbd"); };

        } else if (bottomNavId == R.id.nav_diary) {
            fragment = new DiaryFragment();
            if (TextUtils.isEmpty(_diaryYyyyMM)) {
                diaryYyyyMM = DateUtil.getyyyyMMdd().substring(0, 6);
            } else {
                diaryYyyyMM = _diaryYyyyMM;
            }

            Bundle args = new Bundle();
            args.putString("display_yyyyMM", diaryYyyyMM);
            fragment.setArguments(args);
            mainBannerText = DateUtil.getDayString_yyyyMM(diaryYyyyMM, Locale.getDefault());

            weatherImageResource = R.drawable.ic_search_black_24dp;
            //listener = v -> { System.out.println("tbd"); };
            leftButton.setVisibility(View.VISIBLE); leftButton.setClickable(true);
            rightButton.setVisibility(View.VISIBLE); rightButton.setClickable(true);

        } else if (bottomNavId == R.id.nav_letter) {
            fragment = new LetterFragment();
            mainBannerText = "Letter";
            weatherImageResource = R.drawable.ic_search_black_24dp;
            //listener = v -> { System.out.println("tbd"); };

        } else {
            fragment = new TodayNightFragment();
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
        Diary diary1 = new Diary("20190411", CurrentAccountUtil.account.getUserId(), "today is good day", "i don't know.", 0);
        Diary diary2 = new Diary("20190410", CurrentAccountUtil.account.getUserId(), "today's diary", "", 0);
        Diary diary3 = new Diary("20190409", CurrentAccountUtil.account.getUserId(), "4.9 hangul eul sseul su ub da", "font size\n" +
                "alias\n" + "member registration & login\n" + "do not share diary\n" + "background image\n" +
                "export diary\n" + "alarm\n" + "lullaby\n" + "change screen lock password\n" + "no receive fcm push", 0);
        Diary diary4 = new Diary("20190412", CurrentAccountUtil.account.getUserId(), "abc\n", "abcd12", 0);
        Diary diary5 = new Diary("20190430", CurrentAccountUtil.account.getUserId(), "title today", "DFASDFfsdflkaj sdk;lfadsf", 0);

        writeDao.insertWrite(diary1);
        writeDao.insertWrite(diary2);
        writeDao.insertWrite(diary3);
        writeDao.insertWrite(diary4);
        writeDao.insertWrite(diary5);


        LetterDao letterDao = new LetterDao(this);
        Letter letter1 = new Letter(String.valueOf(System.currentTimeMillis() - 100L), "jw@hanmail.com", CurrentAccountUtil.account.getUserId(), "letter title1", "i am find thank you", 0, 0);
        Letter letter2 = new Letter(String.valueOf(System.currentTimeMillis() - 200L), "jw@hanmail2.com", CurrentAccountUtil.account.getUserId(), "letter title2", "thank you and you", 0, 0);
        Letter letter3 = new Letter(String.valueOf(System.currentTimeMillis() - 300L), "jw@hanmail3.com", CurrentAccountUtil.account.getUserId(), "letter title3", "", 0, 0);

        letterDao.insertLetter(letter1);
        letterDao.insertLetter(letter2);
        letterDao.insertLetter(letter3); */
    }
}
