package org.jerrioh.diary.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.dbmodel.Diary;
import org.jerrioh.diary.dbmodel.Letter;
import org.jerrioh.diary.util.DateUtil;

import java.util.List;
import java.util.Locale;

public class LetterRecyclerViewAdapter extends RecyclerView.Adapter<LetterRecyclerViewAdapter.LetterViewHolder> {

    private List<Letter> letterData;
    private OnItemClickListener callback;

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    public class LetterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public LetterViewHolder(View v) {
            super(v);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            callback.onItemClick(getAdapterPosition());
        }
    }

    public LetterRecyclerViewAdapter(List<Letter> letterData, OnItemClickListener callback) {
        this.letterData = letterData;
        this.callback = callback;
    }

    @NonNull
    @Override
    public LetterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_letter, viewGroup, false);
        return new LetterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LetterViewHolder letterViewHolder, int i) {
        Letter letter = letterData.get(i);
        String writeUser = letter.getWriteUserId();

        ImageView image = (ImageView) letterViewHolder.itemView.findViewById(R.id.letter_row_image);
        TextView titleText = (TextView) letterViewHolder.itemView.findViewById(R.id.letter_row_title);
        TextView contentText = (TextView) letterViewHolder.itemView.findViewById(R.id.letter_row_expire_date);

        int imageResource;
        if (letter.getStatus() == 0) {
            imageResource = R.drawable.ic_mail_black_24dp;
        } else {
            imageResource = R.drawable.ic_drafts_black_24dp;
        }
        image.setImageResource(imageResource);
        titleText.setText("FROM " + writeUser);
        contentText.setText("* 편지는 36시간 후에 삭제됩니다.");
    }

    @Override
    public int getItemCount() {
        return letterData == null ? 0 : letterData.size();
    }
}
