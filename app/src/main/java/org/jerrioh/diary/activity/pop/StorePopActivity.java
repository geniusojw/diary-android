package org.jerrioh.diary.activity.pop;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.fragment.StoreFragment;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class StorePopActivity extends CustomPopActivity {

    private static final String TAG = "StorePopActivity";
    private boolean okEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chocolate_store_pop);

        super.setWindowAttribute(.95f, .8f);

        AuthorStoreApis authorStoreApis = new AuthorStoreApis(this);

        Intent intent = getIntent();
        String itemId = intent.getStringExtra("itemId");
        int price = intent.getIntExtra("itemPrice", -1);
        int currentChocolates = intent.getIntExtra("currentChocolates", 0);

        String priceText = "당신은 " + currentChocolates + "개의 초콜릿이 있습니다.";

        TextView priceTextView = findViewById(R.id.text_view_chocolate_pop_chocolate_count);
        priceTextView.setText(priceText);

        if (currentChocolates < price) {
            priceTextView.setTextColor(getResources().getColor(R.color.colorRed));
            TextView priceTipView = findViewById(R.id.text_view_chocolate_pop_chocolate_count_tip);
            priceTipView.setVisibility(View.VISIBLE);
        }

        String descriptionText1 = "";
        String descriptionText2;
        if (price == 0) {
            descriptionText2 = "무료입니다!";
        } else {
            descriptionText2 = "가격 : 초콜릿" + price + "개";
        }

        View.OnClickListener okClickListener = null;

        if (StoreFragment.ITEM_WEATHER.equals(itemId)) {
            if (price == 0) {
                descriptionText1 = "날씨 정보를 들을까요?";
            } else {
                descriptionText1 = "\"날씨 정보\"";
            }

            okClickListener = v -> {
                if (okEnabled) {
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 0);
                    } else {
                        okEnabled = false;
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                String cityName = addresses.get(0).getLocality();
                                String countryCode = addresses.get(0).getCountryCode();

                                authorStoreApis.weather(cityName, countryCode, new ApiCallback() {
                                    @Override
                                    protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                                        if (httpStatus == 200) {
                                            JSONObject data = jsonObject.getJSONObject("data");
                                            String description = data.getString("description");
                                            buySuccessBasic(description);
                                        } else if (httpStatus == 402) {
                                            buyFailWithToast("not enough chocolates");
                                        } else if (httpStatus == 412) {
                                            buyFailWithToast("이미 샀어요.");
                                        } else {
                                            buySuccessBasic("현재 당신 기분 날씨는 흐림..");
                                        }
                                    }
                                });

                            } catch (IOException e) {
                                Log.e(TAG, "io exception. " + e.toString());
                            }
                        } else {
                            buySuccessBasic("현재 당신 기분 날씨는 맑음!!");
                        }
                    }
                }
            };

        } else if (StoreFragment.ITEM_POST_IT.equals(itemId)) {

            NumberPicker numberPicker = findViewById(R.id.number_picker_store_pop);
            numberPicker.setVisibility(View.VISIBLE);
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(30);
//            numberPicker.setScaleX(0.8f);
//            numberPicker.setScaleY(0.8f);
            numberPicker.setWrapSelectorWheel(false);

            descriptionText1 = "\"포스트잇\"";
            descriptionText2 = "중요한 이야기는 높은 가격으로!";
            okClickListener = v -> {
                if (okEnabled) {
                    int postItPrice = numberPicker.getValue();
                    okEnabled = false;
                    authorStoreApis.buyPostIt(postItPrice, new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                buySuccessBasic("광장에 가서 글을 쓰세요! =>" + postItPrice);
                            } else if (httpStatus == 402) {
                                buyFailWithToast("not enough chocolates");
                            } else if (httpStatus == 412) {
                                buyFailWithToast("이미 포스트잇이 있네요!");
                            }
                            numberPicker.setVisibility(View.GONE);
                        }
                    });
                }
            };

        } else if (StoreFragment.ITEM_CHANGE_DESCRIPTION.equals(itemId)) {
            descriptionText1 = "\"당신에 대한 이야기\"";
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

                                AuthorDao authorDao = new AuthorDao(StorePopActivity.this);
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
        } else if (StoreFragment.ITEM_CHANGE_NICKNAME.equals(itemId)) {
            descriptionText1 = "\"닉네임 랜덤 변경\"";
            okClickListener = v -> {
                if (okEnabled) {
                    okEnabled = false;
                    authorStoreApis.changeNickname(new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                String nickname = data.getString("nickname");

                                AuthorDao authorDao = new AuthorDao(StorePopActivity.this);
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
        } else if (StoreFragment.ITEM_PURCHASE_THEME.equals(itemId)) {
            descriptionText1 = "\"일기장 테마 랜덤 뽑기\"";
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

                                ThemeDao themeDao = new ThemeDao(StorePopActivity.this);
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
        } else if (StoreFragment.ITEM_PURCHASE_MUSIC.equals(itemId)) {
            descriptionText1 = "\"음악 랜덤 뽑기\"";
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

                                MusicDao musicDao = new MusicDao(StorePopActivity.this);
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
        } else if (StoreFragment.ITEM_DIARY_GROUP_INVITATION.equals(itemId)) {
            descriptionText1 = "\"일기모임 주최\"";
            okClickListener = v -> {
                if (okEnabled) {
                    okEnabled = false;
                    String keyword = null;
                    authorStoreApis.diaryGroupInvitation(keyword, new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                AuthorUtil.syncAuthorDiaryGroupData(StorePopActivity.this);
                            } else if (httpStatus == 402) {
                                Toast.makeText(StorePopActivity.this, "not enough chocolates", Toast.LENGTH_LONG).show();
                            } else if (httpStatus == 409) {
                                Toast.makeText(StorePopActivity.this, "이미 그룹이 있다.", Toast.LENGTH_LONG).show();
                            }
                            finish();
                        }
                    });
                }
            };
        } else if (StoreFragment.ITEM_DIARY_GROUP_SUPPORT.equals(itemId)) {
            descriptionText1 = "\"일기모임 지원\"";
            okClickListener = v -> {
                if (okEnabled) {
                    okEnabled = false;
                    authorStoreApis.diaryGroupSupport("scale", new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                AuthorUtil.syncAuthorDiaryGroupData(StorePopActivity.this);
                            } else if (httpStatus == 402) {
                                Toast.makeText(StorePopActivity.this, "not enough chocolates", Toast.LENGTH_LONG).show();
                            } else if (httpStatus == 412) {
                                Toast.makeText(StorePopActivity.this, "최대에 도달했다. 더는 안된다.", Toast.LENGTH_LONG).show();
                            }
                            finish();
                        }
                    });
                }
            };
        } else if (StoreFragment.ITEM_CHOCOLATE_DONATION.equals(itemId)) {
            descriptionText1 = "\"초콜릿 기부\"";
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
        } else {
            descriptionText1 = "뭔가 이상하다.";
        }

        TextView popDescriptionView1 = findViewById(R.id.text_view_chocolate_pop_description1);
        popDescriptionView1.setText(descriptionText1);

        TextView popDescriptionView2 = findViewById(R.id.text_view_chocolate_pop_description2);
        popDescriptionView2.setText(descriptionText2);

        View okButton = findViewById(R.id.linear_layout_chocolate_pop_select1);
        View noButton = findViewById(R.id.linear_layout_chocolate_pop_select2);

        okButton.setOnClickListener(okClickListener);
        noButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void buySuccessBasic(String successText) {
        //Toast.makeText(StorePopActivity.this, "success", Toast.LENGTH_LONG).show();

        TextView descriptionView = findViewById(R.id.text_view_chocolate_pop_description1);
        descriptionView.setText(successText);

        TextView popDescriptionView2 = findViewById(R.id.text_view_chocolate_pop_description2);
        popDescriptionView2.setVisibility(View.GONE);

        View okButton = findViewById(R.id.linear_layout_chocolate_pop_select1);
        View noButton = findViewById(R.id.linear_layout_chocolate_pop_select2);
        okButton.setVisibility(View.GONE);
        noButton.setVisibility(View.GONE);

        View confirmButton = findViewById(R.id.linear_layout_chocolate_pop_select3);
        confirmButton.setVisibility(View.VISIBLE);
        confirmButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void buyFailWithToast(String message) {
        Toast.makeText(StorePopActivity.this, message, Toast.LENGTH_LONG).show();
        finish();
    }
}
