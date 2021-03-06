package org.jerrioh.diary.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.adapter.StoreRecyclerViewAdapter;
import org.jerrioh.diary.activity.pop.StoreBannerPopActivity;
import org.jerrioh.diary.activity.pop.StorePopActivity;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.AuthorStoreApis;
import org.jerrioh.diary.util.ThemeUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class StoreFragment extends AbstractFragment {

    public static class Item {
        public String itemId;
        public int price;
        public String title;
        public String description;
        public int imageResource;
    }

    private static final String TAG = "StoreFragment";

    // 각각의 아이템 번호는 서버와 맞춘 값이다. 변경시 서버와 동시에 변경이 필요하다.
    public static final String ITEM_WISE_SAYING = "ITEM_WISE_SAYING";
    public static final String ITEM_CREATE_WISE_SAYING = "ITEM_CREATE_WISE_SAYING";
    public static final String ITEM_WEATHER = "ITEM_WEATHER";
    public static final String ITEM_POST_IT = "ITEM_POST_IT";
    public static final String ITEM_CHANGE_DESCRIPTION = "ITEM_CHANGE_DESCRIPTION";
    public static final String ITEM_CHANGE_NICKNAME = "ITEM_CHANGE_NICKNAME";
    public static final String ITEM_PURCHASE_THEME = "ITEM_PURCHASE_THEME";
    public static final String ITEM_PURCHASE_MUSIC = "ITEM_PURCHASE_MUSIC";
    public static final String ITEM_DIARY_GROUP_INVITATION = "ITEM_DIARY_GROUP_INVITATION";
    public static final String ITEM_CHOCOLATE_DONATION = "ITEM_CHOCOLATE_DONATION";
    public static final String ITEM_DIARY_GROUP_SUPPORT = "ITEM_DIARY_GROUP_SUPPORT";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 쓰기 버튼 활성화
        View todayView =  inflater.inflate(R.layout.fragment_store, container, false);

        this.checkConnectionToStore(todayView);

        super.setFloatingActionButton(AbstractFragment.BUTTON_TYPE_INVISIBLE);

        BitmapDrawable bitmap = ThemeUtil.getBitmapDrawablePattern(this, 1);
        todayView.setBackgroundDrawable(bitmap);

        return todayView;
    }

    private void checkConnectionToStore(View todayView) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Log.e(TAG, "activity is null !!");
            return;
        }

        AuthorStoreApis authorStoreApis = new AuthorStoreApis(activity);
        authorStoreApis.getStoreStatus(new ApiCallback() {
            @Override
            protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {

//                RelativeLayout relativeView = getActivity().findViewById(R.id.relative_layout_fragment_store_waiting);
//                relativeView.setVisibility(View.GONE);

                if (httpStatus == 200) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    int chocolates = data.getInt("chocolates");
                    JSONObject priceMap = data.getJSONObject("priceMap");

                    StoreBannerPopActivity.currentChocolates = chocolates;

                    List<Item> items = getItems(priceMap, activity);

                    Collections.sort(items, new Comparator<Item>() {
                        @Override
                        public int compare(Item o1, Item o2) {
                            return o1.price - o2.price;
                        }
                    });

                    final StoreRecyclerViewAdapter mAdapter = new StoreRecyclerViewAdapter(items, pos -> {
                        Item item = items.get(pos);
                        Intent intent = new Intent(activity, StorePopActivity.class);
                        intent.putExtra("itemId", item.itemId);
                        intent.putExtra("itemPrice", item.price);
                        intent.putExtra("currentChocolates", chocolates);
                        startActivity(intent);
                    });

                    RecyclerView diaryRecyclerView = todayView.findViewById(R.id.today_recycler_view);
                    diaryRecyclerView.setHasFixedSize(false);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    diaryRecyclerView.setLayoutManager(layoutManager);
                    diaryRecyclerView.setAdapter(mAdapter);
                }
            }
        });
    }

    private List<Item> getItems(JSONObject priceMap, Context context) throws JSONException {
        List<Item> items = new ArrayList<>();
        Iterator<String> iterator = priceMap.keys();

        String itemId = null;
        int price = -1;
        String title = null;
        String description = null;
        int imageResource = -1;

        while (iterator.hasNext()) {
            itemId = iterator.next();
            price = priceMap.getInt(itemId);

            if (price == -1) {
                continue;
            }

            switch (itemId) {
                case ITEM_WISE_SAYING:
                    title = context.getResources().getString(R.string.store_item_wise_saying);
                    description = context.getResources().getString(R.string.store_item_wise_saying_description);
                    imageResource = R.drawable.ic_comment_black_24dp;
                    break;
                case ITEM_CREATE_WISE_SAYING:
                    title = context.getResources().getString(R.string.store_item_create_wise_saying);
                    description = context.getResources().getString(R.string.store_item_create_wise_saying_description);
                    imageResource = R.drawable.ic_edit_black_24dp;
                    break;
                case ITEM_WEATHER:
                    title = context.getResources().getString(R.string.store_item_weather);
                    description = context.getResources().getString(R.string.store_item_weather_description);
                    imageResource = R.drawable.weather_sunny;
                    break;
                case ITEM_POST_IT:
                    title = context.getResources().getString(R.string.store_item_post);
                    description = context.getResources().getString(R.string.store_item_post_description);
                    imageResource = R.drawable.ic_chat_black_24dp;
                    break;
                case ITEM_CHANGE_DESCRIPTION:
                    title = context.getResources().getString(R.string.store_item_about);
                    description = context.getResources().getString(R.string.store_item_about_description);
                    imageResource = R.drawable.ic_face_black_24dp;
                    break;
                case ITEM_CHANGE_NICKNAME:
                    title = context.getResources().getString(R.string.store_item_nick);
                    description = context.getResources().getString(R.string.store_item_nick_description);
                    imageResource = R.drawable.ic_fiber_new_black_24dp;
                    break;
                case ITEM_PURCHASE_THEME:
                    title = context.getResources().getString(R.string.store_item_theme);
                    description = context.getResources().getString(R.string.store_item_theme_description);
                    imageResource = R.drawable.ic_gradient_black_24dp;
                    break;
                case ITEM_PURCHASE_MUSIC:
                    title = context.getResources().getString(R.string.store_item_music);
                    description = context.getResources().getString(R.string.store_item_music_description);
                    imageResource = R.drawable.ic_audiotrack_black_24dp;
                    break;
                case ITEM_DIARY_GROUP_INVITATION:
                    title = context.getResources().getString(R.string.store_item_invitation);
                    description = context.getResources().getString(R.string.store_item_invitation_description);
                    imageResource = R.drawable.ic_mail_outline_black_24dp;
                    break;
                case ITEM_CHOCOLATE_DONATION:
                    title = context.getResources().getString(R.string.store_item_donation);
                    description = context.getResources().getString(R.string.store_item_donation_description);
                    imageResource = R.drawable.ic_favorite_black_24dp;
                    break;
                case ITEM_DIARY_GROUP_SUPPORT:
                    title = "";
                    description = "";
                    imageResource = -1;
                    continue;
                    //break;
                default:
                    continue;
                    //break;
            }

            Item item = new Item();
            item.itemId = itemId;
            item.price = price;
            item.title = title;
            item.description = description;
            item.imageResource = imageResource;
            items.add(item);
        }
        return items;
    }
}
