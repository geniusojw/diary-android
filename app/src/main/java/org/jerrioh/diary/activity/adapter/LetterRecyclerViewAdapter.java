package org.jerrioh.diary.activity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.draw.SettingActivity;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.Letter;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.PropertyUtil;

import java.util.Date;
import java.util.List;

public class
LetterRecyclerViewAdapter extends RecyclerView.Adapter<LetterRecyclerViewAdapter.LetterViewHolder> {

    private final Context context;
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

    public LetterRecyclerViewAdapter(Context context, List<Letter> letterData, OnItemClickListener callback) {
        this.context = context;
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
        Author author = AuthorUtil.getAuthor();

        Letter letter = letterData.get(i);
        boolean lettersToMe = letter.getToAuthorId().equals(author.getAuthorId());

        ImageView image = letterViewHolder.itemView.findViewById(R.id.letter_row_image);
        TextView titleText = letterViewHolder.itemView.findViewById(R.id.letter_row_title);
        TextView contentText = letterViewHolder.itemView.findViewById(R.id.letter_row_expire_date);

//        ImageView deleteImage = letterViewHolder.itemView.findViewById(R.id.image_view_letter_delete);
//        deleteImage.setOnClickListener(v -> {
//            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
//            alertBuilder.setTitle("Disable diary group beInvited")
//                    .setMessage("You can decline the beInvited without disable it.\nDo you really want to disable it?")
//                    .setPositiveButton("Disable", (dialog, which) -> {
//                        PropertyUtil.setProperty(Property.Key.GROUP_INVITATION_USE, "0", SettingActivity.this);
//                        switchInvitation.setChecked(false);
//                    })
//                    .setNegativeButton("Cancel", (dialog, which) -> {
//                        dialog.cancel();
//                        switchInvitation.setChecked(true);
//                    });
//            AlertDialog alertDialog = alertBuilder.create();
//            alertDialog.show();
//        });


        int imageResource = R.drawable.ic_mail_black_24dp;
        String letterTitle;
        String description = context.getResources().getString(R.string.letter_written_time) + ": " + new Date(letter.getWrittenTime());

        if (lettersToMe) {
            if (letter.getStatus() == Letter.LetterStatus.UNREAD) {
                imageResource = R.drawable.ic_mail_black_24dp;
            } else if (letter.getStatus() == Letter.LetterStatus.READ) {
                imageResource = R.drawable.ic_drafts_black_24dp;
            } else if (letter.getStatus() == Letter.LetterStatus.REPLIED) {
                imageResource = R.drawable.ic_reply_black_24dp;
                description += "\n(" + context.getResources().getString(R.string.letter_written_time) + ")";
            }
            letterTitle = "[" + context.getResources().getString(R.string.letter_receive_letter) + "] FROM: " + letter.getFromAuthorNickname();
        } else {
            letterTitle = "[" + context.getResources().getString(R.string.letter_send_letter) + "] TO: " + letter.getToAuthorNickname();
            imageResource = R.drawable.ic_mail_outline_black_24dp;
        }

        image.setImageResource(imageResource);
        titleText.setText(letterTitle);
        contentText.setText(description);

        CardView cardView = letterViewHolder.itemView.findViewById(R.id.card_view_row_letter);
        cardView.setCardBackgroundColor(0xCCFFFFFF);
    }

    @Override
    public int getItemCount() {
        return letterData == null ? 0 : letterData.size();
    }
}
