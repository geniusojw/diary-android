package org.jerrioh.diary.activity.pop;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
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
import org.jerrioh.diary.util.DateUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class StorePopActivity extends AbstractDiaryPopActivity {

    private static final int REQUEST_SENTENCE_POP_CREATE_WISE_SAYING_ACTIVITY = 1;

    private static final String TAG = "StorePopActivity";
    private boolean okEnabled = true;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SENTENCE_POP_CREATE_WISE_SAYING_ACTIVITY) {
            if (resultCode == 200) {
                buySuccessBasic(getResources().getString(R.string.store_pop_create_wise_saying_result_title),
                        getResources().getString(R.string.store_pop_create_wise_saying_result_description),
                        getResources().getString(R.string.store_pop_create_wise_saying_result_description_detail));

            } else if (resultCode == 402) {
                buyFailWithToast(getResources().getString(R.string.store_pop_not_enough_money));
            } else if (resultCode == 412) {
                buyFailWithToast(getResources().getString(R.string.store_pop_already_bought));
            } else {
                finish();
            }
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
        }

        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider,int status,Bundle extras){}
    };

    private void updateWithNewLocation(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.i(TAG, "latitude: " + latitude + ", longitude: " + longitude);
        }
    }

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

        String priceText = getResources().getString(R.string.store_pop_current_money, currentChocolates);


        TextView priceTextView = findViewById(R.id.text_view_chocolate_pop_chocolate_count);
        priceTextView.setText(priceText);

        if (currentChocolates < price) {
            priceTextView.setTextColor(getResources().getColor(R.color.colorRed));
            TextView priceTipView = findViewById(R.id.text_view_chocolate_pop_chocolate_count_tip);
            priceTipView.setVisibility(View.VISIBLE);
        }

        String itemTitle = "";
        String itemDescription = "";
        String itemDescriptionDetail = "";
        String itemPrice;
        if (price == 0) {
            itemPrice = getResources().getString(R.string.store_pop_it_is_free);
        } else {
            itemPrice = getResources().getString(R.string.store_pop_require_money, price);
        }

        boolean noPrice = false;
        View.OnClickListener okClickListener = null;

        if (StoreFragment.ITEM_WISE_SAYING.equals(itemId)) {
            itemTitle = getResources().getString(R.string.store_pop_wise_saying_title);
            itemDescription = getResources().getString(R.string.store_pop_wise_saying_description);
            itemDescriptionDetail = getResources().getString(R.string.store_pop_wise_saying_description_detail);

            okClickListener = v -> {
                if (okEnabled) {
                    okEnabled = false;
                    authorStoreApis.getWiseSaying(new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                String source = "- " + data.getString("source");
                                String wiseSaying = data.getString("wiseSaying");

                                buySuccessBasic(wiseSaying, source, null);

                            } else if (httpStatus == 402) {
                                buyFailWithToast(getResources().getString(R.string.store_pop_not_enough_money));
                            } else if (httpStatus == 412) {
                                buyFailWithToast(getResources().getString(R.string.store_pop_already_bought));
                            }
                        }
                    });
                }
            };

        } else if (StoreFragment.ITEM_CREATE_WISE_SAYING.equals(itemId)) {
            itemTitle = getResources().getString(R.string.store_pop_create_wise_saying_title);
            itemDescription = getResources().getString(R.string.store_pop_create_wise_saying_description);
            itemDescriptionDetail = getResources().getString(R.string.store_pop_create_wise_saying_description_detail);

            okClickListener = v -> {
                if (okEnabled) {
                    okEnabled = false;

                    Intent wiseSayingPop = new Intent(StorePopActivity.this, SentencePopActivity.class);
                    wiseSayingPop.putExtra("type", SentencePopActivity.TYPE_STORE_CREATE_WISE_SAYING);
                    startActivityForResult(wiseSayingPop, REQUEST_SENTENCE_POP_CREATE_WISE_SAYING_ACTIVITY);
                }
            };

        } else if (StoreFragment.ITEM_WEATHER.equals(itemId)) {
            itemTitle = getResources().getString(R.string.store_pop_weather_title);
            itemDescription = getResources().getString(R.string.store_pop_weather_description);

            okClickListener = v -> {
                if (okEnabled) {
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 0);
                    } else {
                        okEnabled = false;

                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 500.0f, locationListener);
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null) {
                            this.callWeatherApiByLatitudeLongitude(location.getLatitude(), location.getLongitude());

                        } else {
                            buySuccessBasic(
                                    getResources().getString(R.string.store_pop_weather_result_title),
                                    getResources().getString(R.string.store_pop_weather_result_description2),
                                    null);
                        }
                    }
                }
            };

        } else if (StoreFragment.ITEM_POST_IT.equals(itemId)) {
            itemTitle = getResources().getString(R.string.store_pop_post_title);
            itemDescription = getResources().getString(R.string.store_pop_post_description);
            itemDescriptionDetail = getResources().getString(R.string.store_pop_post_description_detail);

            noPrice = true;
            NumberPicker numberPicker = findViewById(R.id.number_picker_store_pop);
            numberPicker.setVisibility(View.VISIBLE);
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(100);
            numberPicker.setWrapSelectorWheel(false);

            okClickListener = v -> {
                if (okEnabled) {
                    int postItPrice = numberPicker.getValue();
                    okEnabled = false;
                    authorStoreApis.buyPostIt(postItPrice, new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                buySuccessBasic(getResources().getString(R.string.store_pop_post_result_title),
                                        getResources().getString(R.string.store_pop_post_result_description),
                                        null);
                            } else if (httpStatus == 402) {
                                buyFailWithToast(getResources().getString(R.string.store_pop_not_enough_money));
                            } else if (httpStatus == 412) {
                                buyFailWithToast(getResources().getString(R.string.store_pop_already_bought));
                            } else {
                                finish();
                            }
                            numberPicker.setVisibility(View.GONE);
                        }
                    });
                }
            };

        } else if (StoreFragment.ITEM_CHANGE_DESCRIPTION.equals(itemId)) {
            itemTitle = getResources().getString(R.string.store_pop_about_title);
            itemDescription = getResources().getString(R.string.store_pop_about_description);
            itemDescriptionDetail = getResources().getString(R.string.store_pop_about_description_detail);

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

                                buySuccessBasic(description, aboutYou, null);

                            } else if (httpStatus == 402) {
                                buyFailWithToast(getResources().getString(R.string.store_pop_not_enough_money));
                            } else if (httpStatus == 412) {
                                buyFailWithToast(getResources().getString(R.string.store_pop_already_bought));
                            } else {
                                finish();
                            }
                        }
                    });
                }
            };
        } else if (StoreFragment.ITEM_CHANGE_NICKNAME.equals(itemId)) {
            itemTitle = getResources().getString(R.string.store_pop_nickname_title);
            itemDescription = getResources().getString(R.string.store_pop_nickname_description);

            okClickListener = v -> {
                if (okEnabled) {
                    okEnabled = false;
                    authorStoreApis.changeNickname(new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                String originalNickname = AuthorUtil.getAuthor().getNickname();
                                JSONObject data = jsonObject.getJSONObject("data");
                                String nickname = data.getString("nickname");

                                AuthorDao authorDao = new AuthorDao(StorePopActivity.this);
                                authorDao.updateNickname(nickname);

                                buySuccessBasic(getResources().getString(R.string.store_pop_nickname_result_title),
                                        getResources().getString(R.string.store_pop_nickname_result_description, originalNickname, nickname),
                                        null);

                            } else if (httpStatus == 402) {
                                buyFailWithToast(getResources().getString(R.string.store_pop_not_enough_money));
                            } else if (httpStatus == 412) {
                                buyFailWithToast(getResources().getString(R.string.store_pop_already_bought));
                            } else {
                                finish();
                            }
                        }
                    });
                }
            };
        } else if (StoreFragment.ITEM_PURCHASE_THEME.equals(itemId)) {
            itemTitle = getResources().getString(R.string.store_pop_theme_title);
            itemDescription = getResources().getString(R.string.store_pop_theme_description);
            itemDescriptionDetail = getResources().getString(R.string.store_pop_theme_description_detail);

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
                                    theme.setPattern0(null);
                                    theme.setPattern1(null);
                                    theme.setPattern2(null);
                                    theme.setPattern3(null);
                                    theme.setBannerColor(null);
                                    themeDao.insertTheme(theme);
                                    buySuccessBasic(getResources().getString(R.string.store_pop_theme_result_title),
                                            getResources().getString(R.string.store_pop_theme_result_description, themeName),
                                            getResources().getString(R.string.store_pop_go_to_setting));
                                } else {
                                    buySuccessBasic(getResources().getString(R.string.store_pop_lose_the_draw),
                                            getResources().getString(R.string.store_pop_lose_the_draw_description),
                                            null);
                                }
                            } else if (httpStatus == 402) {
                                buyFailWithToast(getResources().getString(R.string.store_pop_not_enough_money));
                            } else if (httpStatus == 412) {
                                buyFailWithToast(getResources().getString(R.string.store_pop_already_bought));
                            } else {
                                finish();
                            }
                        }
                    });
                }
            };
        } else if (StoreFragment.ITEM_PURCHASE_MUSIC.equals(itemId)) {
            itemTitle = getResources().getString(R.string.store_pop_music_title);
            itemDescription = getResources().getString(R.string.store_pop_music_description);
            itemDescriptionDetail = getResources().getString(R.string.store_pop_music_description_detail);

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
                                    music.setMusicData(null);
                                    musicDao.insertMusic(music);
                                    buySuccessBasic(getResources().getString(R.string.store_pop_music_result_title),
                                            getResources().getString(R.string.store_pop_music_result_description, musicName),
                                            getResources().getString(R.string.store_pop_go_to_setting));
                                } else {
                                    buySuccessBasic(getResources().getString(R.string.store_pop_lose_the_draw),
                                            getResources().getString(R.string.store_pop_lose_the_draw_description),
                                            null);
                                }
                            } else if (httpStatus == 402) {
                                buyFailWithToast(getResources().getString(R.string.store_pop_not_enough_money));
                            } else if (httpStatus == 412) {
                                buyFailWithToast(getResources().getString(R.string.store_pop_already_bought));
                            } else {
                                finish();
                            }
                        }
                    });
                }
            };
        } else if (StoreFragment.ITEM_DIARY_GROUP_INVITATION.equals(itemId)) {
            itemTitle = getResources().getString(R.string.store_pop_group_invite_title);
            itemDescription = getResources().getString(R.string.store_pop_group_invite_description);
            itemDescriptionDetail = getResources().getString(R.string.store_pop_group_invite_description_detail);

            okClickListener = v -> {
                if (okEnabled) {
                    okEnabled = false;
                    String keyword = null;
                    authorStoreApis.diaryGroupInvitation(keyword, new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                AuthorUtil.syncAuthorDiaryGroupData(StorePopActivity.this);
                                buySuccessBasic(getResources().getString(R.string.store_pop_group_invite_result_title),
                                        getResources().getString(R.string.store_pop_group_invite_result_description),
                                        getResources().getString(R.string.store_pop_group_invite_result_description_detail));
                            } else if (httpStatus == 402) {
                                buyFailWithToast(getResources().getString(R.string.store_pop_not_enough_money));
                            } else if (httpStatus == 409) {
                                buyFailWithToast(getResources().getString(R.string.store_pop_group_already_exists));
                            } else {
                                finish();
                            }
                        }
                    });
                }
            };
        } else if (StoreFragment.ITEM_DIARY_GROUP_SUPPORT.equals(itemId)) {
            itemTitle = getResources().getString(R.string.store_pop_group_support_title);
            itemDescription = getResources().getString(R.string.store_pop_group_support_description);

            okClickListener = v -> {
                if (okEnabled) {
                    okEnabled = false;
                    authorStoreApis.diaryGroupSupport("scale", new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                AuthorUtil.syncAuthorDiaryGroupData(StorePopActivity.this);
                                buySuccessBasic(getResources().getString(R.string.store_pop_group_support_result_title),
                                        getResources().getString(R.string.store_pop_group_support_result_description),
                                        getResources().getString(R.string.store_pop_group_support_result_description_detail));
                            } else if (httpStatus == 402) {
                                buyFailWithToast(getResources().getString(R.string.store_pop_not_enough_money));
                            } else if (httpStatus == 412) {
                                buyFailWithToast(getResources().getString(R.string.store_pop_cannot_support));
                            } else {
                                finish();
                            }
                        }
                    });
                }
            };
        } else if (StoreFragment.ITEM_CHOCOLATE_DONATION.equals(itemId)) {
            itemTitle = getResources().getString(R.string.store_pop_donation_title);
            itemDescription = getResources().getString(R.string.store_pop_donation_description);

            noPrice = true;
            NumberPicker numberPicker = findViewById(R.id.number_picker_store_pop);
            numberPicker.setVisibility(View.VISIBLE);
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(100);
            numberPicker.setWrapSelectorWheel(false);

            okClickListener = v -> {
                if (okEnabled) {
                    int donationPrice = numberPicker.getValue();
                    okEnabled = false;
                    authorStoreApis.donate(donationPrice, new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                int totalDonations = data.getInt("totalDonations") + donationPrice;

                                String resultMessage;
                                if (donationPrice == 0) {
                                    resultMessage = getResources().getString(R.string.store_pop_donation_result_description_0);
                                } else {
                                    resultMessage = getResources().getString(R.string.store_pop_donation_result_description, donationPrice);
                                }
                                String resultMessageDetail = getResources().getString(R.string.store_pop_donation_result_description_detail, totalDonations);

                                buySuccessBasic(getResources().getString(R.string.store_pop_donation_result_title),
                                        resultMessage, resultMessageDetail);
                            } else if (httpStatus == 402) {
                                buyFailWithToast(getResources().getString(R.string.store_pop_not_enough_money));
                            } else if (httpStatus == 412) {
                                buyFailWithToast(getResources().getString(R.string.store_pop_already_bought));
                            } else {
                                finish();
                            }
                            numberPicker.setVisibility(View.GONE);
                        }
                    });
                }
            };
        } else {
            itemTitle = "it is broken...";
        }

        TextView itemTitleView = findViewById(R.id.text_view_chocolate_pop_item_title);
        TextView itemDescriptionView = findViewById(R.id.text_view_chocolate_pop_item_description);
        TextView itemDescriptionDetailView = findViewById(R.id.text_view_chocolate_pop_item_description_detail);
        TextView itemPriceView = findViewById(R.id.text_view_chocolate_pop_item_price);

        itemTitleView.setText("\"" + itemTitle + "\"");
        itemDescriptionView.setText(itemDescription);

        if (noPrice) {
            itemPriceView.setVisibility(View.GONE);
        } else {
            itemPriceView.setText(itemPrice);
        }

        if (!TextUtils.isEmpty(itemDescriptionDetail)) {
            itemDescriptionDetailView.setVisibility(View.VISIBLE);
            itemDescriptionDetailView.setText("* " + itemDescriptionDetail);
        }

        View okButton = findViewById(R.id.linear_layout_chocolate_pop_select1);
        View noButton = findViewById(R.id.linear_layout_chocolate_pop_select2);

        okButton.setOnClickListener(okClickListener);
        noButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void callWeatherApiByLatitudeLongitude(double latitude, double longitude) {
        List<Address> addresses;
        Geocoder geocoder = new Geocoder(this, Locale.US);
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.isEmpty()) {
                buySuccessBasic(
                        getResources().getString(R.string.store_pop_weather_result_title),
                        getResources().getString(R.string.store_pop_weather_result_description1),
                        null);
                return;
            }

            String countryCode = addresses.get(0).getCountryCode();
            String cityName = addresses.get(0).getAdminArea();

            AuthorStoreApis authorStoreApis = new AuthorStoreApis(this);
            authorStoreApis.weather(cityName, countryCode, new ApiCallback() {
                @Override
                protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                    if (httpStatus == 200) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        String description = data.getString("description");
                        buySuccessBasic(getResources().getString(R.string.store_pop_weather_result_title), description, null);
                    } else if (httpStatus == 402) {
                        buyFailWithToast(getResources().getString(R.string.store_pop_not_enough_money));
                    } else if (httpStatus == 412) {
                        buyFailWithToast(getResources().getString(R.string.store_pop_already_bought));
                    } else {
                        buySuccessBasic(
                                getResources().getString(R.string.store_pop_weather_result_title),
                                getResources().getString(R.string.store_pop_weather_result_description2),
                                null);
                    }
                }
            });

        } catch (IOException e) {
            Log.e(TAG, "io exception. " + e.toString());
        }
    }

    private void buySuccessBasic(String successText, String descriptionText, String detailText) {
        TextView titleView = findViewById(R.id.text_view_chocolate_pop_item_title);
        TextView descriptionView = findViewById(R.id.text_view_chocolate_pop_item_description);
        TextView descriptionDetailView = findViewById(R.id.text_view_chocolate_pop_item_description_detail);

        titleView.setText(successText);
        if (TextUtils.isEmpty(descriptionText)) {
            descriptionView.setVisibility(View.GONE);
        } else {
            descriptionView.setVisibility(View.VISIBLE);
            descriptionView.setText(descriptionText);
        }
        if (TextUtils.isEmpty(detailText)) {
            descriptionDetailView.setVisibility(View.GONE);
        } else {
            descriptionDetailView.setVisibility(View.VISIBLE);
            descriptionDetailView.setText(detailText);
        }

        TextView priceView = findViewById(R.id.text_view_chocolate_pop_item_price);
        View okButton = findViewById(R.id.linear_layout_chocolate_pop_select1);
        View noButton = findViewById(R.id.linear_layout_chocolate_pop_select2);
        TextView priceTextView = findViewById(R.id.text_view_chocolate_pop_chocolate_count);
        TextView priceTipView = findViewById(R.id.text_view_chocolate_pop_chocolate_count_tip);

        priceView.setVisibility(View.GONE);
        okButton.setVisibility(View.GONE);
        noButton.setVisibility(View.GONE);
        priceTextView.setVisibility(View.GONE);
        priceTipView.setVisibility(View.GONE);

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
