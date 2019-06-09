package org.jerrioh.diary.activity.draw;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.main.ChocolateStorePopActivity;
import org.jerrioh.diary.activity.main.DiaryGroupPopActivity;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.AuthorApis;
import org.jerrioh.diary.api.author.DiaryGroupApis;
import org.jerrioh.diary.api.author.AuthorStoreApis;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.model.DiaryGroup;
import org.jerrioh.diary.model.db.DiaryGroupDao;
import org.jerrioh.diary.util.DateUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class ChocolateStoreActivity extends CommonActionBarActivity {
    private static final String TAG = "ChocolateStoreActivity";

    // 각각의 아이템 번호는 서버와 맞춘 값이다. 변경시 서버와 동시에 변경이 필요하다.
    public static final String CHOCOLATE_STORE_ITEM_CHANGE_DESCRIPTION = "ITEM_CHANGE_DESCRIPTION";
    public static final String CHOCOLATE_STORE_ITEM_CHANGE_NICKNAME = "ITEM_CHANGE_NICKNAME";
    public static final String CHOCOLATE_STORE_ITEM_CHANGE_DIARY_THEME = "ITEM_CHANGE_DIARY_THEME";
    public static final String CHOCOLATE_STORE_ITEM_INVITE1 = "ITEM_INVITE_TICKET1";
    public static final String CHOCOLATE_STORE_ITEM_INVITE2 = "ITEM_INVITE_TICKET2";
    public static final String CHOCOLATE_STORE_ITEM_ALIAS_FEATURE_UNLIMITED_USE = "ITEM_ALIAS_FEATURE_UNLIMITED_USE";
    public static final String CHOCOLATE_STORE_ITEM_DONATE = "ITEM_CHOCOLATE_DONATION";

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectionToStore();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chocolate_store);
        setCommonToolBar("Chocolate Store");

        TextView chocolateTextView = findViewById(R.id.text_view_chocolate_store_number_of_chocolate);
        chocolateTextView.setText("Waiting for chocolate grandfather ...");

        checkConnectionToStore();
    }

    private void checkConnectionToStore() {
        TextView chocolateTextView = findViewById(R.id.text_view_chocolate_store_number_of_chocolate);

        AuthorStoreApis authorStoreApis = new AuthorStoreApis(this);
        authorStoreApis.getStoreStatus(new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                int priceDescription = -1;
                int priceNickname = -1;
                int priceDiaryTheme = -1;
                int priceInvite1 = -1;
                int priceInvite2 = -1;
                int priceAliasFeature = -1;
                int priceDonation = -1;
                if (httpStatus == 200) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    int chocolates = data.getInt("chocolates");
                    Log.e(TAG, "YOU HAVE + " + chocolates + " !!!!!!!!!!!!!!!!!!!!!!");
                    if (chocolates > 1) {
                        chocolateTextView.setText("You have " + chocolates + " chocolates.");
                    } else if (chocolates == 1) {
                        chocolateTextView.setText("You have 1 chocolate.");
                    } else {
                        chocolateTextView.setText("You have no chocolate.\n* You can get a chocolate by writing a diary.");
                    }

                    JSONObject priceMap = data.getJSONObject("priceMap");
                    priceDescription = priceMap.getInt(CHOCOLATE_STORE_ITEM_CHANGE_DESCRIPTION);
                    priceNickname = priceMap.getInt(CHOCOLATE_STORE_ITEM_CHANGE_NICKNAME);
                    priceDiaryTheme = priceMap.getInt(CHOCOLATE_STORE_ITEM_CHANGE_DIARY_THEME);
                    priceInvite1 = priceMap.getInt(CHOCOLATE_STORE_ITEM_INVITE1);
                    priceInvite2 = priceMap.getInt(CHOCOLATE_STORE_ITEM_INVITE2);
                    priceAliasFeature = priceMap.getInt(CHOCOLATE_STORE_ITEM_ALIAS_FEATURE_UNLIMITED_USE);
                    priceDonation = priceMap.getInt(CHOCOLATE_STORE_ITEM_DONATE);

                } else {
                    chocolateTextView.setText("The store has not opened.");
                }
                ChocolateStoreActivity.this.setChangeDescription(priceDescription);
                ChocolateStoreActivity.this.setChangeNickName(priceNickname);
                ChocolateStoreActivity.this.setChangeDiaryTheme(priceDiaryTheme);
                ChocolateStoreActivity.this.setInvitationTicket1(priceInvite1);
                ChocolateStoreActivity.this.setInvitationTicket2(priceInvite2);
                ChocolateStoreActivity.this.setAliasFeatureUnlimitedUse(priceAliasFeature);
                ChocolateStoreActivity.this.setDonateChocolate(priceDonation);
            }
        });
    }

    private void setChangeDescription(int price) {
        setProductView(R.id.linear_layout_chocolate_store_change_description, R.id.text_view_chocolate_store_change_description_cost, CHOCOLATE_STORE_ITEM_CHANGE_DESCRIPTION, price);
    }

    private void setChangeNickName(int price) {
        setProductView(R.id.linear_layout_chocolate_store_change_nickname, R.id.text_view_chocolate_store_change_nickname_cost, CHOCOLATE_STORE_ITEM_CHANGE_NICKNAME, price);
    }

    private void setChangeDiaryTheme(int price) {
        setProductView(R.id.linear_layout_chocolate_store_change_diary_theme, R.id.text_view_chocolate_store_change_diary_theme_cost, CHOCOLATE_STORE_ITEM_CHANGE_DIARY_THEME, price);
    }

    private void setInvitationTicket1(int price) {
        setProductView(R.id.linear_layout_chocolate_store_invite1, R.id.text_view_chocolate_store_invite1_cost, CHOCOLATE_STORE_ITEM_INVITE1, price);
    }

    private void setInvitationTicket2(int price) {
        setProductView(R.id.linear_layout_chocolate_store_invite2, R.id.text_view_chocolate_store_invite2_cost, CHOCOLATE_STORE_ITEM_INVITE2, price);
    }

    private void setAliasFeatureUnlimitedUse(int price) {
        setProductView(R.id.linear_layout_chocolate_store_alias_feature, R.id.text_view_chocolate_store_alias_feature_cost, CHOCOLATE_STORE_ITEM_ALIAS_FEATURE_UNLIMITED_USE, price);
    }

    private void setDonateChocolate(int price) {
        setProductView(R.id.linear_layout_chocolate_store_donate, R.id.text_view_chocolate_store_donate_cost, CHOCOLATE_STORE_ITEM_DONATE, price);
    }

    private void setProductView(int productLayoutViewId, int costViewId, String itemId, int price) {
        LinearLayout productView = findViewById(productLayoutViewId);
        TextView costView = findViewById(costViewId);

        if (price == -1) {
            costView.setText("TEMPORARILY\nOUT OF STOCK");
            costView.setTextSize(12.f);
            productView.setBackgroundColor(getResources().getColor(R.color.steelTrap));
            productView.setOnClickListener(v -> {
                String message = "재고가 소진되었습니다.";
                if (CHOCOLATE_STORE_ITEM_CHANGE_DESCRIPTION.equals(itemId) || CHOCOLATE_STORE_ITEM_CHANGE_NICKNAME.equals(itemId)) {
                    message = "일시적으로 재고가 소진되었습니다.";
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            });

        } else {
            String costText;
            if (CHOCOLATE_STORE_ITEM_DONATE.equals(itemId)) {
                costText = "? chocolate";
            } else {
                costText = price + " chocolate";
            }
            costView.setText(costText);
            costView.setTextSize(15.f);
            productView.setBackgroundColor(getResources().getColor(R.color.medwayMist));
            productView.setOnClickListener(v -> {
                Intent intent = new Intent(this, ChocolateStorePopActivity.class);
                intent.putExtra("itemId", itemId);
                intent.putExtra("itemPrice", price);
                startActivity(intent);
            });
        }
    }
}