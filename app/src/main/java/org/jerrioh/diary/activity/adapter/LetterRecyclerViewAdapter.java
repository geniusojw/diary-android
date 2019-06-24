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
import org.jerrioh.diary.model.Letter;

import java.util.Date;
import java.util.List;

public class LetterRecyclerViewAdapter extends RecyclerView.Adapter<LetterRecyclerViewAdapter.LetterViewHolder> {

    private List<Letter> letterData;
    private boolean lettersToMe;
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

    public LetterRecyclerViewAdapter(List<Letter> letterData, boolean lettersToMe, OnItemClickListener callback) {
        this.letterData = letterData;
        this.lettersToMe = lettersToMe;
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

        ImageView image = letterViewHolder.itemView.findViewById(R.id.letter_row_image);
        TextView titleText = letterViewHolder.itemView.findViewById(R.id.letter_row_title);
        TextView contentText = letterViewHolder.itemView.findViewById(R.id.letter_row_expire_date);

        int imageResource = R.drawable.ic_mail_black_24dp;

        String letterTitle = "";
        if (lettersToMe) {
            if (letter.getStatus() != Letter.LetterStatus.UNREAD) {
                imageResource = R.drawable.ic_drafts_black_24dp;
            }
            letterTitle = "FROM: " + letter.getFromAuthorNickname();
        } else {
            letterTitle = "TO: " + letter.getToAuthorNickname();
        }

        image.setImageResource(imageResource);
        titleText.setText(letterTitle);
        contentText.setText("편지는 36시간 후에 삭제됩니다.\n작성시간: " + new Date(letter.getWrittenTime()));

        CardView cardView = letterViewHolder.itemView.findViewById(R.id.card_view_row_letter);
        cardView.setCardBackgroundColor(0xCCFFFFFF);
    }

    @Override
    public int getItemCount() {
        return letterData == null ? 0 : letterData.size();
    }
}
