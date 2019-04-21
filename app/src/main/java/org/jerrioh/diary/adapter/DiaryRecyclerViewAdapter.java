package org.jerrioh.diary.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.dbmodel.Write;

import java.util.List;

public class DiaryRecyclerViewAdapter extends RecyclerView.Adapter<DiaryRecyclerViewAdapter.DiaryViewHolder> {

    private final List<Write> diaryData;
    private final OnItemClickListener callback;

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

    public DiaryRecyclerViewAdapter(List<Write> diaryData, OnItemClickListener callback) {
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
        TextView textView = (TextView) diaryViewHolder.itemView.findViewById(R.id.row_diary_title);
        Write diary = diaryData.get(i);
        textView.setText(diary.getWriteDay() + " / " + diary.getTitle() + " / " + diary.getContent());
    }

    @Override
    public int getItemCount() {
        return diaryData == null ? 0 : diaryData.size();
    }
}
