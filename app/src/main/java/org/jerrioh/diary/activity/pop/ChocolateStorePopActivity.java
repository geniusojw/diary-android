package org.jerrioh.diary.activity.pop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.draw.ChocolateStoreActivity;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.AuthorStoreApis;
import org.jerrioh.diary.model.Music;
import org.jerrioh.diary.model.Theme;
import org.jerrioh.diary.model.db.AuthorDao;
import org.jerrioh.diary.model.db.MusicDao;
import org.jerrioh.diary.model.db.ThemeDao;
import org.jerrioh.diary.util.AuthorUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class ChocolateStorePopActivity extends CustomPopActivity {

    private static final String TAG = "ChocolateStorePopActivity";
    private boolean okEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chocolate_store_pop);

        super.setWindowAttribute(.95f, .5f);

        AuthorStoreApis authorStoreApis = new AuthorStoreApis(this);

        Intent intent = getIntent();
        String itemId = intent.getStringExtra("itemId");
        int price = intent.getIntExtra("itemPrice", -1);

        String descriptionText = "";
        View.OnClickListener okClickListener = null;
        if (ChocolateStoreActivity.ITEM_CHANGE_DESCRIPTION.equals(itemId)) {
            descriptionText = "초콜릿 " + price + "개로 이야기를 들을까요?";
            okClickListener = v -> {
                if (okEnabled) {
                    okEnabled = false;
                    authorStoreApis.changeDescription(new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                String aboutYou = data.getString("aboutYou");
                                String description = data.getString("description");

                                AuthorDao authorDao = new AuthorDao(ChocolateStorePopActivity.this);
                                authorDao.updateDescription(description);

                                buySuccessBasic(aboutYou);

                            } else if (httpStatus == 402) {
                                buyFailWithToast("not enough chocolates");
                            } else if (httpStatus == 412) {
                                buyFailWithToast("이미 샀어요.");
                            }
                        }
                    });
                }
            };
        } else if (ChocolateStoreActivity.ITEM_CHANGE_NICKNAME.equals(itemId)) {
            descriptionText = "초콜릿 " + price + "개로 닉네임을 바꾸시겠습니까?";
            okClickListener = v -> {
                if (okEnabled) {
                    okEnabled = false;
                    authorStoreApis.changeNickname(new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                String nickname = data.getString("nickname");

                                AuthorDao authorDao = new AuthorDao(ChocolateStorePopActivity.this);
                                authorDao.updateNickname(nickname);

                                buySuccessBasic("변경되었습니다!\n당신의 새로운 닉네임은 " + nickname + "입니다." +
                                        "3");
                            } else if (httpStatus == 402) {
                                buyFailWithToast("not enough chocolates");
                            } else if (httpStatus == 412) {
                                buyFailWithToast("이미 샀어요.");
                            }
                        }
                    });
                }
            };
        } else if (ChocolateStoreActivity.ITEM_PURCHASE_THEME.equals(itemId)) {
            descriptionText = "일기장 스타일을 바꿔드립니다.\n당신 취향은 고려하지 않고.\n가격은 초콜릿 " + price + "개";
            okClickListener = v -> {
                if (okEnabled) {
                    okEnabled = false;
                    authorStoreApis.purchaseTheme(new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            boolean success = true;
                            if (httpStatus == 200) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                String themeName = data.getString("themeName");

                                ThemeDao themeDao = new ThemeDao(ChocolateStorePopActivity.this);
                                if (!themeDao.getAllThemeNames().contains(themeName)) {
                                    Theme theme = new Theme();
                                    theme.setThemeName(themeName);
                                    theme.setPattern0(data.getString("pattern0"));
                                    theme.setPattern1(data.getString("pattern1"));
                                    theme.setPattern2(data.getString("pattern2"));
                                    theme.setPattern3(data.getString("pattern3"));
                                    themeDao.insertTheme(theme);
                                    buySuccessBasic("새로운 테마를 저장하엿다!");
                                } else {
                                    buySuccessBasic("이미 가지고 있는 테마!");
                                }
                            } else if (httpStatus == 402) {
                                buyFailWithToast("not enough chocolates");
                            } else if (httpStatus == 412) {
                                buyFailWithToast("이미 샀어요.");
                            }
                        }
                    });
                }
            };
        } else if (ChocolateStoreActivity.ITEM_PURCHASE_MUSIC.equals(itemId)) {
            descriptionText = "음악을 살 수 있다고?\n당신의 음악 취향은 고려하지 않고.\n가격은 초콜릿 " + price + "개";
            okClickListener = v -> {
                if (okEnabled) {
                    okEnabled = false;
                    authorStoreApis.purchaseMusic(new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            boolean success = true;
                            if (httpStatus == 200) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                String musicName = data.getString("musicName");

                                MusicDao musicDao = new MusicDao(ChocolateStorePopActivity.this);
                                if (!musicDao.getAllMusicNames().contains(musicName)) {
                                    Music music = new Music();
                                    music.setMusicName(musicName);
                                    music.setMusicData(data.getString("musicData"));
                                    musicDao.insertMusic(music);
                                    buySuccessBasic("새로운 음악을 저장하엿다!");
                                } else {
                                    buySuccessBasic("이미 가지고 있는 음악ㅋㅋㅋ");
                                }
                            } else if (httpStatus == 402) {
                                buyFailWithToast("not enough chocolates");
                            } else if (httpStatus == 412) {
                                buyFailWithToast("이미 샀어요.");
                            }
                        }
                    });
                }
            };
        } else if (ChocolateStoreActivity.ITEM_DIARY_GROUP_INVITATION.equals(itemId)) {
            descriptionText = "초대를 하시겠습니까?";
            okClickListener = v -> {
                if (okEnabled) {
                    okEnabled = false;
                    String keyword = null;
                    authorStoreApis.diaryGroupInvitation(keyword, new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                AuthorUtil.syncAuthorDiaryGroupData(ChocolateStorePopActivity.this);
                            } else if (httpStatus == 402) {
                                Toast.makeText(ChocolateStorePopActivity.this, "not enough chocolates", Toast.LENGTH_LONG).show();
                            } else if (httpStatus == 409) {
                                Toast.makeText(ChocolateStorePopActivity.this, "이미 그룹이 있다.", Toast.LENGTH_LONG).show();
                            }
                            finish();
                        }
                    });
                }
            };
        } else if (ChocolateStoreActivity.ITEM_DIARY_GROUP_SUPPORT.equals(itemId)) {
            descriptionText = "지원 하시겠습니까?";
            okClickListener = v -> {
                if (okEnabled) {
                    okEnabled = false;
                    authorStoreApis.diaryGroupSupport("period", new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                AuthorUtil.syncAuthorDiaryGroupData(ChocolateStorePopActivity.this);
                            } else if (httpStatus == 402) {
                                Toast.makeText(ChocolateStorePopActivity.this, "not enough chocolates", Toast.LENGTH_LONG).show();
                            } else if (httpStatus == 412) {
                                Toast.makeText(ChocolateStorePopActivity.this, "최대에 도달했다. 더는 안된다.", Toast.LENGTH_LONG).show();
                            }
                            finish();
                        }
                    });
                }
            };
        } else if (ChocolateStoreActivity.ITEM_CHOCOLATE_DONATION.equals(itemId)) {
            descriptionText = "초콜릿을 기부를 하시겠습니까?";
            okClickListener = v -> {
                if (okEnabled) {
                    okEnabled = false;
                    authorStoreApis.donate(0, new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                buySuccessBasic("기부하였다.");
                            } else if (httpStatus == 402) {
                                buyFailWithToast("not enough chocolates");
                            } else if (httpStatus == 412) {
                                buyFailWithToast("이미 샀어요.");
                            }
                        }
                    });
                }
            };
        } else if (ChocolateStoreActivity.ITEM_ALIAS_FEATURE_UNLIMITED_USE.equals(itemId)) {
            descriptionText = "가명 기능 무제한 사용권을 구매하시겠습니까?\n이 기능을 알고 싶다면 FAQ를 읽어보세요.";
            okClickListener = v -> {
                if (okEnabled) {
                    okEnabled = false;
                    authorStoreApis.aliasFeatureUnlimitedUse(new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                buySuccessBasic("구매 성공!");
                            } else if (httpStatus == 402) {
                                buyFailWithToast("not enough chocolates");
                            } else if (httpStatus == 412) {
                                buyFailWithToast("이미 샀어요.");
                            }
                        }
                    });
                }
            };
        }

        TextView popDescriptionView = findViewById(R.id.text_view_chocolate_pop_description);
        popDescriptionView.setText(descriptionText);

        Button okButton = findViewById(R.id.button_chocolate_pop_ok);
        Button noButton = findViewById(R.id.button_chocolate_pop_no);

        okButton.setOnClickListener(okClickListener);
        noButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void buySuccessBasic(String successText) {
        //Toast.makeText(ChocolateStorePopActivity.this, "success", Toast.LENGTH_LONG).show();

        TextView descriptionView = findViewById(R.id.text_view_chocolate_pop_description);
        descriptionView.setText(successText);

        Button okButton = findViewById(R.id.button_chocolate_pop_ok);
        Button noButton = findViewById(R.id.button_chocolate_pop_no);
        okButton.setVisibility(View.GONE);
        noButton.setVisibility(View.GONE);

        Button confirmButton = findViewById(R.id.button_chocolate_pop_confirm);
        confirmButton.setVisibility(View.VISIBLE);
        confirmButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void buyFailWithToast(String message) {
        Toast.makeText(ChocolateStorePopActivity.this, message, Toast.LENGTH_LONG).show();
        finish();
    }
}
