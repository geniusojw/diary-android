package org.jerrioh.diary.activity.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.draw.ChocolateStoreActivity;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.AuthorStoreApis;
import org.jerrioh.diary.util.AuthorUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class ChocolateStorePopActivity extends Activity {

    private static final String TAG = "ChocolateStorePopActivity";
    private boolean okEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chocolate_store_pop);

        setWindowAttribute();

        AuthorStoreApis authorStoreApis = new AuthorStoreApis(this);

        Intent intent = getIntent();
        String itemId = intent.getStringExtra("itemId");
        int price = intent.getIntExtra("itemPrice", -1);

        String descriptionText = "";
        View.OnClickListener okClickListener = null;
        if (ChocolateStoreActivity.CHOCOLATE_STORE_ITEM_CHANGE_DESCRIPTION.equals(itemId)) {
            descriptionText = "초콜릿 " + price + "개로 이야기를 들을까요?";
            okClickListener = v -> {
                if (okEnabled) {
                    okEnabled = false;
                    authorStoreApis.changeDescription(new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                buySuccessBasic("좋습니다!\n당신은 멍청이예요.");
                            } else if (httpStatus == 402) {
                                buyFailWithToast("not enough chocolates");
                            } else if (httpStatus == 412) {
                                buyFailWithToast("이미 샀어요.");
                            }
                        }
                    });
                }
            };
        } else if (ChocolateStoreActivity.CHOCOLATE_STORE_ITEM_CHANGE_NICKNAME.equals(itemId)) {
            descriptionText = "초콜릿 " + price + "개로 닉네임을 바꾸시겠습니까?";
            okClickListener = v -> {
                if (okEnabled) {
                    okEnabled = false;
                    authorStoreApis.changeNickname(new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                buySuccessBasic("변경되었습니다!\n당신의 새로운 닉네임은 멍청이예요.");
                            } else if (httpStatus == 402) {
                                buyFailWithToast("not enough chocolates");
                            } else if (httpStatus == 412) {
                                buyFailWithToast("이미 샀어요.");
                            }
                        }
                    });
                }
            };
        } else if (ChocolateStoreActivity.CHOCOLATE_STORE_ITEM_CHANGE_DIARY_THEME.equals(itemId)) {
            descriptionText = "일기장 스타일을 바꿔드립니다.\n당신 취향은 고려하지 않고.\n가격은 초콜릿 " + price + "개";
            okClickListener = v -> {
                if (okEnabled) {
                    okEnabled = false;
                    authorStoreApis.changeDiaryTheme(new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                buySuccessBasic("새로운 스타일로 바뀌었다!");
                            } else if (httpStatus == 402) {
                                buyFailWithToast("not enough chocolates");
                            } else if (httpStatus == 412) {
                                buyFailWithToast("이미 샀어요.");
                            }
                        }
                    });
                }
            };
        } else if (ChocolateStoreActivity.CHOCOLATE_STORE_ITEM_INVITE1.equals(itemId)) {
            descriptionText = "초대1를 하시겠습니까?";
            okClickListener = v -> {
                if (okEnabled) {
                    okEnabled = false;
                    authorStoreApis.inviteTicket1(new ApiCallback() {
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
        } else if (ChocolateStoreActivity.CHOCOLATE_STORE_ITEM_INVITE2.equals(itemId)) {
            descriptionText = "초대2를 하시겠습니까?";
            okClickListener = v -> {
                authorStoreApis.inviteTicket2(new ApiCallback() {
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
            };
        } else if (ChocolateStoreActivity.CHOCOLATE_STORE_ITEM_DONATE.equals(itemId)) {
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
        } else if (ChocolateStoreActivity.CHOCOLATE_STORE_ITEM_ALIAS_FEATURE_UNLIMITED_USE.equals(itemId)) {
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

    private void setWindowAttribute() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        getWindow().setLayout((int)(width*.95), (int)(height*.5));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -50;

        getWindow().setAttributes(params);
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
