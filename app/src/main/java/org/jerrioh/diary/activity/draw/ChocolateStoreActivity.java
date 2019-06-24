package org.jerrioh.diary.activity.draw;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.pop.ChocolateStorePopActivity;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.AuthorStoreApis;
import org.jerrioh.diary.util.ThemeUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class ChocolateStoreActivity extends CommonActionBarActivity {
    private static final String TAG = "ChocolateStoreActivity";

    // 각각의 아이템 번호는 서버와 맞춘 값이다. 변경시 서버와 동시에 변경이 필요하다.
    public static final String ITEM_CHANGE_DESCRIPTION = "ITEM_CHANGE_DESCRIPTION";
    public static final String ITEM_CHANGE_NICKNAME = "ITEM_CHANGE_NICKNAME";
    public static final String ITEM_PURCHASE_THEME = "ITEM_PURCHASE_THEME";
    public static final String ITEM_PURCHASE_MUSIC = "ITEM_PURCHASE_MUSIC";
    public static final String ITEM_DIARY_GROUP_INVITATION = "ITEM_DIARY_GROUP_INVITATION";
    public static final String ITEM_ALIAS_FEATURE_UNLIMITED_USE = "ITEM_ALIAS_FEATURE_UNLIMITED_USE";
    public static final String ITEM_CHOCOLATE_DONATION = "ITEM_CHOCOLATE_DONATION";
    public static final String ITEM_DIARY_GROUP_SUPPORT = "ITEM_DIARY_GROUP_SUPPORT";

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectionToStore();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BitmapDrawable bitmapDrawable = ThemeUtil.getBitmapDrawablePattern(this, 3);
        getWindow().setBackgroundDrawable(bitmapDrawable);

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
                int pricePurchaseTheme = -1;
                int pricePurchaseMusic = -1;
                int priceInvite = -1;
                int priceAliasFeature = -1;
                int priceDonation = -1;
                if (httpStatus == 200) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    int chocolates = data.getInt("chocolates");
                    if (chocolates > 1) {
                        chocolateTextView.setText("You have " + chocolates + " chocolates.");
                    } else if (chocolates == 1) {
                        chocolateTextView.setText("You have 1 chocolate.");
                    } else {
                        chocolateTextView.setText("You have no chocolate.\n* You can get a chocolate by writing a diary.");
                    }

                    JSONObject priceMap = data.getJSONObject("priceMap");
                    priceDescription = priceMap.getInt(ITEM_CHANGE_DESCRIPTION);
                    priceNickname = priceMap.getInt(ITEM_CHANGE_NICKNAME);
                    pricePurchaseTheme = priceMap.getInt(ITEM_PURCHASE_THEME);
                    pricePurchaseMusic = priceMap.getInt(ITEM_PURCHASE_MUSIC);
                    priceInvite = priceMap.getInt(ITEM_DIARY_GROUP_INVITATION);
                    priceAliasFeature = priceMap.getInt(ITEM_ALIAS_FEATURE_UNLIMITED_USE);
                    priceDonation = priceMap.getInt(ITEM_CHOCOLATE_DONATION);

                } else {
                    chocolateTextView.setText("The store has not opened.");
                }
                ChocolateStoreActivity.this.setChangeDescription(priceDescription);
                ChocolateStoreActivity.this.setChangeNickName(priceNickname);
                ChocolateStoreActivity.this.setPurchaseTheme(pricePurchaseTheme);
                ChocolateStoreActivity.this.setPurchaseDiaryMusic(pricePurchaseMusic);
                ChocolateStoreActivity.this.setInvitationTicket(priceInvite);
                ChocolateStoreActivity.this.setAliasFeatureUnlimitedUse(priceAliasFeature);
                ChocolateStoreActivity.this.setDonateChocolate(priceDonation);
            }
        });
    }

    private void setChangeDescription(int price) {
        setProductView(R.id.linear_layout_chocolate_store_change_description, R.id.text_view_chocolate_store_change_description_cost, ITEM_CHANGE_DESCRIPTION, price);
    }

    private void setChangeNickName(int price) {
        setProductView(R.id.linear_layout_chocolate_store_change_nickname, R.id.text_view_chocolate_store_change_nickname_cost, ITEM_CHANGE_NICKNAME, price);
    }

    private void setPurchaseTheme(int price) {
        setProductView(R.id.linear_layout_chocolate_store_purchase_theme, R.id.text_view_chocolate_store_purchase_theme_cost, ITEM_PURCHASE_THEME, price);
    }

    private void setPurchaseDiaryMusic(int price) {
        setProductView(R.id.linear_layout_chocolate_store_purchase_music, R.id.text_view_chocolate_store_purchase_music_cost, ITEM_PURCHASE_MUSIC, price);
    }

    private void setInvitationTicket(int price) {
        setProductView(R.id.linear_layout_chocolate_store_invitation, R.id.text_view_chocolate_store_invitation_cost, ITEM_DIARY_GROUP_INVITATION, price);
    }

    private void setAliasFeatureUnlimitedUse(int price) {
        setProductView(R.id.linear_layout_chocolate_store_alias_feature, R.id.text_view_chocolate_store_alias_feature_cost, ITEM_ALIAS_FEATURE_UNLIMITED_USE, price);
    }

    private void setDonateChocolate(int price) {
        setProductView(R.id.linear_layout_chocolate_store_donate, R.id.text_view_chocolate_store_donate_cost, ITEM_CHOCOLATE_DONATION, price);
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
                if (ITEM_CHANGE_DESCRIPTION.equals(itemId) || ITEM_CHANGE_NICKNAME.equals(itemId)) {
                    message = "일시적으로 재고가 소진되었습니다.";
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            });

        } else {
            costView.setText(price + " chocolate");
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