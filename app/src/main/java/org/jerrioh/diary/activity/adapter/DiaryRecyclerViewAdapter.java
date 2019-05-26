package org.jerrioh.diary.activity.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.config.Constants;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.CommonUtil;

import java.util.List;
import java.util.Locale;

public class DiaryRecyclerViewAdapter extends RecyclerView.Adapter<DiaryRecyclerViewAdapter.DiaryViewHolder> {

    private List<Diary> diaryData;
    private OnItemClickListener callback;

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    public class DiaryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public DiaryViewHolder(View v) {
            super(v);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            callback.onItemClick(getAdapterPosition());
        }
    }

    public DiaryRecyclerViewAdapter(List<Diary> diaryData, OnItemClickListener callback) {
        this.diaryData = diaryData;
        this.callback = callback;
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_diary, viewGroup, false);
        return new DiaryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder diaryViewHolder, int i) {
        TextView imageText1 = diaryViewHolder.itemView.findViewById(R.id.diary_date_image_text1);
        TextView imageText2 = diaryViewHolder.itemView.findViewById(R.id.diary_date_image_text2);
        TextView imageText3 = diaryViewHolder.itemView.findViewById(R.id.diary_date_image_text3);

        TextView titleText = diaryViewHolder.itemView.findViewById(R.id.diary_row_title);
        TextView contentText = diaryViewHolder.itemView.findViewById(R.id.diary_row_content);

        Diary diary = diaryData.get(i);
        String diaryDate = diary.getDiaryDate();

        imageText1.setText(DateUtil.dayOfWeek(diaryDate));
        imageText2.setText(diaryDate.substring(6, 8));
        imageText3.setText(diaryDate.substring(0, 4) + "." + diaryDate.substring(4, 6));

        titleText.setText(CommonUtil.defaultIfEmpty(diary.getTitle(), Constants.DEFAULT_TITLE));
        contentText.setText(diary.getContent());
    }

    @Override
    public int getItemCount() {
        return diaryData == null ? 0 : diaryData.size();
    }
}
