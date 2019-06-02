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

    public static final int CHOCOLATE_STORE_ITEM_CHANGE_DESCRIPTION = 0;
    public static final int CHOCOLATE_STORE_ITEM_CHANGE_NICKNAME = 1;
    public static final int CHOCOLATE_STORE_ITEM_ALIAS_FEATURE_UNLIMITED_USE = 2;
    public static final int CHOCOLATE_STORE_ITEM_INVITE1 = 3;
    public static final int CHOCOLATE_STORE_ITEM_INVITE2 = 4;
    public static final int CHOCOLATE_STORE_ITEM_DONATE = 5;

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
        AuthorApis authorApis = new AuthorApis(this);
        ApiCallback callback = new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
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
                    ChocolateStoreActivity.this.setChangeDescription();
                    ChocolateStoreActivity.this.setChangeNickName();
                    ChocolateStoreActivity.this.setAliasFeatureUnlimitedUse();
                    ChocolateStoreActivity.this.setInvitationTicket();
                    ChocolateStoreActivity.this.setDonateChocolate();

                } else {
                    chocolateTextView.setText("The store has not opened.");
                }
            }
        };
        authorApis.authorInfo(callback);
    }

    private void setChangeDescription() {
        LinearLayout changeDescriptionLayout = findViewById(R.id.linear_layout_chocolate_store_change_description);
        changeDescriptionLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChocolateStorePopActivity.class);
            intent.putExtra("item", CHOCOLATE_STORE_ITEM_CHANGE_DESCRIPTION);
            startActivity(intent);
        });
    }

    private void setChangeNickName() {
        LinearLayout changeNickLayout = findViewById(R.id.linear_layout_chocolate_store_change_nickname);
        changeNickLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChocolateStorePopActivity.class);
            intent.putExtra("item", CHOCOLATE_STORE_ITEM_CHANGE_NICKNAME);
            startActivity(intent);
        });
    }

    private void setAliasFeatureUnlimitedUse() {
        LinearLayout aliasFeatureLayout = findViewById(R.id.linear_layout_chocolate_store_alias_feature);
        aliasFeatureLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChocolateStorePopActivity.class);
            intent.putExtra("item", CHOCOLATE_STORE_ITEM_ALIAS_FEATURE_UNLIMITED_USE);
            startActivity(intent);
        });
    }

    private void setInvitationTicket() {
        LinearLayout invite1Layout = findViewById(R.id.linear_layout_chocolate_store_invite1);
        LinearLayout invite2Layout = findViewById(R.id.linear_layout_chocolate_store_invite2);

        invite1Layout.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChocolateStorePopActivity.class);
            intent.putExtra("item", CHOCOLATE_STORE_ITEM_INVITE1);
            startActivity(intent);
        });
        invite2Layout.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChocolateStorePopActivity.class);
            intent.putExtra("item", CHOCOLATE_STORE_ITEM_INVITE2);
            startActivity(intent);
        });
    }

    private void setDonateChocolate() {
        LinearLayout donateLayout = findViewById(R.id.linear_layout_chocolate_store_donate);
        donateLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChocolateStorePopActivity.class);
            intent.putExtra("item", CHOCOLATE_STORE_ITEM_DONATE);
            startActivity(intent);
        });
    }
}