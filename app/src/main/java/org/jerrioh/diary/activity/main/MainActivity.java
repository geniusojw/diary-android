package org.jerrioh.diary.activity.main;

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
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.draw.AccountActivity;
import org.jerrioh.diary.activity.draw.AboutApplicationActivity;
import org.jerrioh.diary.activity.draw.ChocolateStoreActivity;
import org.jerrioh.diary.activity.draw.FaqActivity;
import org.jerrioh.diary.activity.draw.SettingActivity;
import org.jerrioh.diary.config.Constants;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.model.db.DiaryDao;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.activity.fragment.DiaryFragment;
import org.jerrioh.diary.activity.fragment.LetterFragment;
import org.jerrioh.diary.activity.fragment.TodayFragment;
import org.jerrioh.diary.activity.fragment.TodayNightFragment;
import org.jerrioh.diary.util.DateUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final List<String> MONTH_LEFT_ADJUST_FAIL_MESSAGES = Arrays.asList("No more diaries.", "There is no diary in the past.", "It is the first page of your diary.");
    private static final List<String> MONTH_RIGHT_ADJUST_FAIL_MESSAGES = Arrays.asList("Do you want to know the future?", "Please keep a diary in the future.", "You can not see the diary tomorrow.");

    private String diaryDate_yyyyMM = null;
    private boolean lettersToMe = true;

    private static final int REQUEST_ACCOUNT_ACTIVITY = 1;
    private static final int REQUEST_SETTING_ACTIVITY = 2;

    @Override
    protected void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(diaryDate_yyyyMM)) {
            this.diaryDate_yyyyMM = DateUtil.getyyyyMMdd().substring(0, 6);
        }
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_view);
        bottomNav.setSelectedItemId(bottomNav.getSelectedItemId());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ACCOUNT_ACTIVITY) {
            if (resultCode == RESULT_OK) { // 회원가입, 로그인, 로그아웃 성공 시
                diaryDate_yyyyMM = DateUtil.getyyyyMMdd().substring(0, 6);
                BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_view);
                bottomNav.setSelectedItemId(R.id.bottom_option_today);
            }
        } else if (requestCode == REQUEST_SETTING_ACTIVITY) {
            if (resultCode == RESULT_OK) { // 데이터 초기화 성공 시
                diaryDate_yyyyMM = DateUtil.getyyyyMMdd().substring(0, 6);
                BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_view);
                bottomNav.setSelectedItemId(R.id.bottom_option_today);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_main_activity);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_navigation, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Author author = AuthorUtil.getAuthor(this);

        setDrawerNavigation();// 드로우 네비게이션 (열기, 메뉴 선택)
        setBottomNavigation();// 바텀 네비게이션 (메뉴 선택 리스너)
        setMonthAdjustButton();// 월 이동 버튼

        // 초기 fragment 세팅 (today)
        diaryDate_yyyyMM = DateUtil.getyyyyMMdd().substring(0, 6);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_view);
        bottomNav.setSelectedItemId(R.id.bottom_option_today);
    }

    private void setDrawerNavigation() {
        ImageView imageView = findViewById(R.id.image_view_open_drawer_hamburger);
        imageView.setClickable(true);
        imageView.setOnClickListener(v -> {
            DrawerLayout drawer = findViewById(R.id.drawer_layout_main_activity);
            if (!drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.openDrawer(GravityCompat.START);

                TextView nickTextView = findViewById(R.id.text_view_drawer_header_nickname);
                TextView descTextView = findViewById(R.id.text_view_drawer_header_description);

                Author account = AuthorUtil.getAuthor(this);
                nickTextView.setText(account.getNickname());
                descTextView.setText(account.getDescription());
            }
        });

        NavigationView drawerNavigationView = findViewById(R.id.navigation_view_drawer);
        drawerNavigationView.setNavigationItemSelectedListener(menu -> {
            int id = menu.getItemId();
            if (id == R.id.drawer_option_chocolate_store) {
                startActivity(new Intent(this, ChocolateStoreActivity.class));
            } else if (id == R.id.drawer_option_setting) {
                startActivityForResult(new Intent(this, SettingActivity.class), REQUEST_SETTING_ACTIVITY);
            } else if (id == R.id.drawer_option_account) {
                startActivityForResult(new Intent(this, AccountActivity.class), REQUEST_ACCOUNT_ACTIVITY);
            } else if (id == R.id.drawer_option_faq) {
                startActivity(new Intent(this, FaqActivity.class));
            } else if (id == R.id.drawer_option_about_application) {
                startActivity(new Intent(this, AboutApplicationActivity.class));
            } else if (id == R.id.drawer_option_share) {
            } else if (id == R.id.drawer_option_feedback) {
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main_activity);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_view);
        bottomNav.setOnNavigationItemSelectedListener(menuItem -> {
            if (TextUtils.isEmpty(diaryDate_yyyyMM)) {
                this.diaryDate_yyyyMM = DateUtil.getyyyyMMdd().substring(0, 6);
            }
            this.setFragmentByBottomNavId(menuItem.getItemId());
            return true;
        });
    }

    private void setMonthAdjustButton() {
        // 월 조정 버튼 2개
        ImageView monthLeft = findViewById(R.id.image_view_month_adjust_left);
        ImageView monthRight = findViewById(R.id.image_view_month_adjust_right);
        monthLeft.setOnClickListener(v -> {
            Diary firstDiary = new DiaryDao(this).getFirstDiary();
            String firstDiaryDateMonth;
            if (firstDiary != null) {
                firstDiaryDateMonth = firstDiary.getDiaryDate().substring(0, 6);
            } else {
                firstDiaryDateMonth = DateUtil.getyyyyMMdd().substring(0, 6);
            }
            String date_yyyyMM = DateUtil.diffMonth(diaryDate_yyyyMM, -1);
            if (Integer.parseInt(date_yyyyMM) < Integer.parseInt(firstDiaryDateMonth)) {
                String message = MONTH_LEFT_ADJUST_FAIL_MESSAGES.get(new Random().nextInt(MONTH_LEFT_ADJUST_FAIL_MESSAGES.size()));
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else {
                diaryDate_yyyyMM = date_yyyyMM;
                BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_view);
                bottomNav.setSelectedItemId(R.id.bottom_option_diary);
            }

        });
        monthRight.setOnClickListener(v -> {
            String date_yyyyMM = DateUtil.diffMonth(diaryDate_yyyyMM, 1);
            String latestDate_yyyyMM = DateUtil.getyyyyMMdd().substring(0, 6);
            if (Integer.parseInt(date_yyyyMM) > Integer.parseInt(latestDate_yyyyMM)) {
                String message = MONTH_RIGHT_ADJUST_FAIL_MESSAGES.get(new Random().nextInt(MONTH_RIGHT_ADJUST_FAIL_MESSAGES.size()));
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else {
                diaryDate_yyyyMM = date_yyyyMM;
                BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_view);
                bottomNav.setSelectedItemId(R.id.bottom_option_diary);
            }
        });
    }

    private void setFragmentByBottomNavId(int bottomNavId) {
        Fragment fragment = new TodayNightFragment();
        String mainBannerText = "";
        int weatherImageResource = 0;
        View.OnClickListener weatherButtonClickListener = null;
        boolean enableMonthAdjustment = false;

        if (bottomNavId == R.id.bottom_option_today) {
            String hhMMss = DateUtil.getHHmmss().substring(0, 6);
            if (Integer.parseInt(hhMMss) >= Constants.PROHIBIT_DIARY_WRITE_HHMMSS) {
                fragment = new TodayFragment();
            } else {
                fragment = new TodayNightFragment();
            }
            //mainBannerText = DateUtil.getDateStringSkipTime(System.currentTimeMillis(), Locale.CHINA);
            mainBannerText = DateUtil.getDateStringSkipTime();
            weatherImageResource = R.drawable.ic_wb_sunny_black_24dp;
            //weatherButtonClickListener = v -> { System.out.println("tbd"); };
            enableMonthAdjustment = false;

        } else if (bottomNavId == R.id.bottom_option_diary) {
            Bundle args = new Bundle();
            args.putString("display_yyyyMM", diaryDate_yyyyMM);
            fragment = new DiaryFragment();
            fragment.setArguments(args);
            mainBannerText = DateUtil.getDateStringYearMonth(diaryDate_yyyyMM);

            weatherImageResource = R.drawable.ic_search_black_24dp;
            //weatherButtonClickListener = v -> { System.out.println("tbd"); };
            enableMonthAdjustment = true;

        } else if (bottomNavId == R.id.bottom_option_letter) {
            Bundle args = new Bundle();
            args.putBoolean("lettersToMe", lettersToMe);
            fragment = new LetterFragment();
            fragment.setArguments(args);
            if (lettersToMe) {
                mainBannerText = "내게 보내진 편지";
            } else {
                mainBannerText = "내가 쓴 편지";
            }
            weatherImageResource = R.drawable.ic_swap_horiz_black_24dp;
            weatherButtonClickListener = v -> {
                lettersToMe = !lettersToMe;
                BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_view);
                bottomNav.setSelectedItemId(R.id.bottom_option_letter);
            };
            enableMonthAdjustment = false;
        }

        // fragment 적용
        this.applyFragment(fragment);

        // 배너 텍스트
        TextView mainBannerTextView = findViewById(R.id.text_view_main_banner_mid);
        mainBannerTextView.setText(mainBannerText);

        // 배너 우측 버튼 (날씨 등)
        ImageView weatherImageView = findViewById(R.id.image_view_banner_right_button);
        weatherImageView.setImageResource(weatherImageResource);
        weatherImageView.setOnClickListener(weatherButtonClickListener);

        // 월조절 버튼 활성화/비활성화
        View leftButton = findViewById(R.id.image_view_month_adjust_left);
        View rightButton = findViewById(R.id.image_view_month_adjust_right);
        if (enableMonthAdjustment) {
            leftButton.setVisibility(View.VISIBLE); leftButton.setClickable(true);
            rightButton.setVisibility(View.VISIBLE); rightButton.setClickable(true);
        } else {
            leftButton.setVisibility(View.INVISIBLE); leftButton.setClickable(false);
            rightButton.setVisibility(View.INVISIBLE); rightButton.setClickable(false);
        }
    }

    private void applyFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout_fragment_container, fragment).commit();
    }
}
