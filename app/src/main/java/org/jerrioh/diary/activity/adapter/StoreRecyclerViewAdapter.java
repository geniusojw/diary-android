package org.jerrioh.diary.activity.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.fragment.StoreFragment;
import org.jerrioh.diary.model.Letter;

import java.util.Date;
import java.util.List;

public class StoreRecyclerViewAdapter extends RecyclerView.Adapter<StoreRecyclerViewAdapter.StoreViewHolder> {

    private List<StoreFragment.Item> itemData;
    private OnItemClickListener callback;

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    public class StoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public StoreViewHolder(View v) {
            super(v);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            callback.onItemClick(getAdapterPosition());
        }
    }

    public StoreRecyclerViewAdapter(List<StoreFragment.Item> itemData, OnItemClickListener callback) {
        this.itemData = itemData;
        this.callback = callback;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_store, viewGroup, false);
        return new StoreViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder storeViewHolder, int i) {
        StoreFragment.Item item = itemData.get(i);

        ImageView image = storeViewHolder.itemView.findViewById(R.id.image_view_row_store);
        TextView titleText = storeViewHolder.itemView.findViewById(R.id.text_view_row_store_title);
        TextView contentText = storeViewHolder.itemView.findViewById(R.id.text_view_row_store_content);

        image.setImageResource(item.imageResource);
        titleText.setText(item.title);
        contentText.setText(item.description);

        CardView cardView = storeViewHolder.itemView.findViewById(R.id.card_view_row_store);
        cardView.setCardBackgroundColor(0xCCFFFFFF);
    }

    @Override
    public int getItemCount() {
        return itemData.size();
    }
}
