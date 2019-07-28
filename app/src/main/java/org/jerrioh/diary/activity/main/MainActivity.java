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
import org.jerrioh.diary.activity.draw.FaqActivity;
import org.jerrioh.diary.activity.draw.SettingActivity;
import org.jerrioh.diary.activity.fragment.SquareFragment;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.AuthorLetterApis;
import org.jerrioh.diary.api.author.DiaryGroupApis;
import org.jerrioh.diary.model.Letter;
import org.jerrioh.diary.model.Post;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.model.db.LetterDao;
import org.jerrioh.diary.model.db.PostDao;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.model.db.DiaryDao;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.activity.fragment.DiaryFragment;
import org.jerrioh.diary.activity.fragment.LetterFragment;
import org.jerrioh.diary.activity.fragment.StoreFragment;
import org.jerrioh.diary.activity.fragment.TodayNightFragment;
import org.jerrioh.diary.util.CommonUtil;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.PropertyUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private String diaryDate_yyyyMM = null;
    private boolean publicSquare = false;

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

        // 편지받기, 일기모임 초대장받기
        this.executeGetApis();
        this.executeAutoDelete();

        AuthorUtil.uploadAuthorDiary(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == REQUEST_ACCOUNT_ACTIVITY) {
//            if (resultCode == RESULT_OK) { // 회원가입, 로그인, 로그아웃 성공 시
//                diaryDate_yyyyMM = DateUtil.getyyyyMMdd().substring(0, 6);
//                BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_view);
//                bottomNav.setSelectedItemId(R.id.bottom_option_store);
//            }
//        } else if (requestCode == REQUEST_SETTING_ACTIVITY) {
//            if (resultCode == RESULT_OK) { // 데이터 초기화 성공 시
//                diaryDate_yyyyMM = DateUtil.getyyyyMMdd().substring(0, 6);
//                BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_view);
//                bottomNav.setSelectedItemId(R.id.bottom_option_store);
//            }
//        }
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
        bottomNav.setSelectedItemId(R.id.bottom_option_diary);
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
            if (id == R.id.drawer_option_account) {
                startActivityForResult(new Intent(this, AccountActivity.class), REQUEST_ACCOUNT_ACTIVITY);
            } else if (id == R.id.drawer_option_setting) {
                startActivityForResult(new Intent(this, SettingActivity.class), REQUEST_SETTING_ACTIVITY);
            } else if (id == R.id.drawer_option_faq) {
                startActivity(new Intent(this, FaqActivity.class));
            } else if (id == R.id.drawer_option_about_application) {
                startActivity(new Intent(this, AboutApplicationActivity.class));
            } else if (id == R.id.drawer_option_share) {
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
                String message = CommonUtil.randomString(getResources().getString(R.string.diary_month_adjust_before1),
                        getResources().getString(R.string.diary_month_adjust_before2),
                        getResources().getString(R.string.diary_month_adjust_before3));
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
                String message = CommonUtil.randomString(getResources().getString(R.string.diary_month_adjust_after1),
                        getResources().getString(R.string.diary_month_adjust_after2),
                        getResources().getString(R.string.diary_month_adjust_after3));
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

        if (bottomNavId == R.id.bottom_option_diary) {
            Bundle args = new Bundle();
            args.putString("display_yyyyMM", diaryDate_yyyyMM);
            fragment = new DiaryFragment();
            fragment.setArguments(args);
            mainBannerText = DateUtil.getDateStringYearMonth(diaryDate_yyyyMM);

            weatherImageResource = R.drawable.ic_mail_outline_black_24dp;
            weatherButtonClickListener = v -> {
                BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_view);
                bottomNav.setSelectedItemId(R.id.bottom_option_letter);
            };
            enableMonthAdjustment = true;

        } else if (bottomNavId == R.id.bottom_option_letter) {
            fragment = new LetterFragment();
            mainBannerText = getResources().getString(R.string.letter);
            weatherImageResource = R.drawable.ic_import_contacts_black_24dp;
            weatherButtonClickListener = v -> {
                BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_view);
                bottomNav.setSelectedItemId(R.id.bottom_option_diary);
            };
            enableMonthAdjustment = false;

        } else if (bottomNavId == R.id.bottom_option_store) {
            fragment = new StoreFragment();
            mainBannerText = getResources().getString(R.string.store);
            weatherImageResource = -1;
            weatherButtonClickListener = null;
            enableMonthAdjustment = false;

        } else if (bottomNavId == R.id.bottom_option_square) {
            weatherImageResource = R.drawable.ic_swap_horiz_black_24dp;

            String squareType;
            if (publicSquare) {
                squareType = "PUBLIC";
                mainBannerText = getResources().getString(R.string.public_square);
                fragment = new SquareFragment();
            } else {
                squareType = "PRIVATE";
                mainBannerText = getResources().getString(R.string.square);
                fragment = new SquareFragment();
            }
            Bundle args = new Bundle();
            args.putString("squareType", squareType);
            fragment.setArguments(args);

            weatherButtonClickListener = v -> {
                publicSquare = !publicSquare;
                BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_view);
                bottomNav.setSelectedItemId(R.id.bottom_option_square);
            };

            enableMonthAdjustment = false;
        }

        // fragment 적용
        this.applyFragment(fragment);

        // 배너 텍스트
//        TextView mainBannerTextTopView = findViewById(R.id.text_view_main_banner_top);
//        mainBannerTextTopView.setText("Today is " + DateUtil.getDateStringSkipTime());

        TextView mainBannerTextMidView = findViewById(R.id.text_view_main_banner_mid);
        mainBannerTextMidView.setText(mainBannerText);

        TextView mainBannerTextBottomView = findViewById(R.id.text_view_main_banner_bottom);
        mainBannerTextBottomView.setText(getResources().getString(R.string.today_is, DateUtil.getDateStringSkipTime()));

        // 배너 우측 버튼 (날씨 등)
        ImageView weatherImageView = findViewById(R.id.image_view_banner_right_button);
        if (weatherImageResource == -1) {
            weatherImageView.setVisibility(View.INVISIBLE);
        } else {
            weatherImageView.setVisibility(View.VISIBLE);
            weatherImageView.setImageResource(weatherImageResource);
        }
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
        transaction.replace(R.id.frame_layout_fragment_container, fragment).commitAllowingStateLoss();
    }

    private void executeGetApis() {
        boolean timeToRequestBeInvited = false;
        boolean timeToRequestGetLetters = false;
        boolean timeToSyncDiaries = false;

        long currentTime = System.currentTimeMillis();

        String groupInvitationUse = PropertyUtil.getProperty(Property.Key.GROUP_INVITATION_USE, this);
        if (Integer.parseInt(groupInvitationUse) == 1) {
            long latestBeInvitedTime = Long.parseLong(PropertyUtil.getProperty(Property.Key.GET_DIARY_GROUP_API_REQUEST_TIME, this));
            if (currentTime > latestBeInvitedTime + Property.Config.GET_DIARY_GROUP_API_RETRY_MILLIS) {
                PropertyUtil.setProperty(Property.Key.GET_DIARY_GROUP_API_REQUEST_TIME, String.valueOf(currentTime), this);
                timeToRequestBeInvited = true;
            }
        }

        long latestGetLettersTime = Long.parseLong(PropertyUtil.getProperty(Property.Key.GET_LETTERS_API_REQUEST_TIME, this));
        if (currentTime > latestGetLettersTime + Property.Config.GET_LETTERS_API_RETRY_MILLIS) {
            PropertyUtil.setProperty(Property.Key.GET_LETTERS_API_REQUEST_TIME, String.valueOf(currentTime), this);
            timeToRequestGetLetters = true;
        }

        long latestSyncDiariesTime = Long.parseLong(PropertyUtil.getProperty(Property.Key.SYNC_DIARIES_API_REQUEST_TIME, this));
        if (currentTime > latestSyncDiariesTime + Property.Config.SYNC_DIARIES_API_RETRY_MILLIS) {
            PropertyUtil.setProperty(Property.Key.SYNC_DIARIES_API_REQUEST_TIME, String.valueOf(currentTime), this);
            timeToSyncDiaries = true;
        }

        if (timeToRequestBeInvited) {
            beInvited();
        }

        if (timeToRequestGetLetters) {
            getLetters();
        }

        if (timeToSyncDiaries) {
            AuthorUtil.syncAccountDiaries(this, null, null);
            AuthorUtil.refreshAccountToken(this);
        }
    }

    private void executeAutoDelete() {
        long currentTime = System.currentTimeMillis();
        String autoDeletePostUse = PropertyUtil.getProperty(Property.Key.AUTO_DELETE_POST_USE, this);
        if (Integer.parseInt(autoDeletePostUse) == 1) {
            PostDao postDao = new PostDao(this);
            List<Post> allPosts = postDao.getAllPosts(false);

            List<String> postIds = new ArrayList<>();
            for (Post post : allPosts) {
                if (currentTime > post.getWrittenTime() + Property.Config.AUTO_DELETE_MILLIS) {
                    postIds.add(post.getPostId());
                } else {
                    break;
                }
            }
            if (!postIds.isEmpty()) {
                for (String postId : postIds) {
                    postDao.deletePost(postId);
                }
            }
        }

        String autoDeleteLetterUse = PropertyUtil.getProperty(Property.Key.AUTO_DELETE_LETTER_USE, this);
        if (Integer.parseInt(autoDeleteLetterUse) == 1) {
            LetterDao letterDao = new LetterDao(this);
            List<Letter> allLetters = letterDao.getAllLetters(false);

            List<String> letterIds = new ArrayList<>();
            for (Letter letter : allLetters) {
                if (currentTime > letter.getWrittenTime() + Property.Config.AUTO_DELETE_MILLIS) {
                    letterIds.add(letter.getLetterId());
                } else {
                    break;
                }
            }
            if (!letterIds.isEmpty()) {
                for (String letterId : letterIds) {
                    new AuthorLetterApis(MainActivity.this).deleteLetter(letterId, new ApiCallback() {
                        @Override protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200 || httpStatus == 404) {
                                letterDao.deleteLetter(letterId);
                            }
                        }
                    });
                }
            }
        }
    }

    private void beInvited() {
        DiaryGroupApis diaryGroupApis = new DiaryGroupApis(this);
        diaryGroupApis.beInvited(new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                if (httpStatus == 200) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.letter_group_invited), Toast.LENGTH_SHORT).show();
                    getLetters();

                } else if (httpStatus == 409) {
                    AuthorUtil.syncAuthorDiaryGroupData(MainActivity.this);
                }
            }
        });
    }

    private void getLetters() {
        Author author = AuthorUtil.getAuthor();
        AuthorLetterApis apis = new AuthorLetterApis(this);
        apis.receive("all", new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                if (httpStatus == 200) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.length() > 0) {
                        LetterDao letterDao = new LetterDao(MainActivity.this);

                        List<Letter> letters = letterDao.getAllLetters(true);
                        Set<String> letterIds = new HashSet<>();
                        for (Letter letter : letters) {
                            letterIds.add(letter.getLetterId());
                        }

                        for (int index = 0; index < jsonArray.length(); index++) {
                            JSONObject letterResponse = jsonArray.getJSONObject(index);
                            String letterId = letterResponse.getString("letterId");
                            if (letterIds.contains(letterId)) {
                                continue;
                            }

                            Letter newLetter = new Letter();
                            newLetter.setLetterId(letterId);
                            newLetter.setLetterType(letterResponse.getInt("letterType"));
                            newLetter.setFromAuthorId(letterResponse.getString("fromAuthorId"));
                            newLetter.setFromAuthorNickname(letterResponse.getString("fromAuthorNickname"));
                            newLetter.setToAuthorId(letterResponse.getString("toAuthorId"));
                            newLetter.setToAuthorNickname(letterResponse.getString("toAuthorNickname"));
                            newLetter.setContent(letterResponse.getString("content"));
                            newLetter.setWrittenTime(letterResponse.getLong("writtenTime"));

                            if (newLetter.getFromAuthorId().equals(author.getAuthorId())) {
                                newLetter.setStatus(Letter.LetterStatus.REPLIED);
                            } else {
                                newLetter.setStatus(Letter.LetterStatus.UNREAD);
                            }
                            letterDao.insertLetter(newLetter);
                        }
                    }
                }
            }
        });
    }
}
