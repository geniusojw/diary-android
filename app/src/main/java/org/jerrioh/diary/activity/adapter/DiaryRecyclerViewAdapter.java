package org.jerrioh.diary.activity.adapter;

import android.graphics.Color;
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
import org.jerrioh.diary.config.Constants;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.model.DiaryGroup;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.CommonUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DiaryRecyclerViewAdapter extends RecyclerView.Adapter<DiaryRecyclerViewAdapter.DiaryViewHolder> {

    private enum Type {
        AD, GROUP_DIARY, DIARY
    }

    private DiaryGroup diaryGroup;
    private List<Diary> diaryData;

    private OnItemClickListener callbackDiaryGroup;
    private OnItemClickListener callbackDiary;

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
            int position = getAdapterPosition();
            Type type = getTypOfPosition(position);
            if (type == Type.GROUP_DIARY) {
                callbackDiaryGroup.onItemClick(-1);
            } else if (type == Type.DIARY) {
                int index = getDiaryIndex(position);
                callbackDiary.onItemClick(index);
            }
        }
    }

    public DiaryRecyclerViewAdapter(DiaryGroup diaryGroup, List<Diary> diaryData, OnItemClickListener callbackDiaryGroup, OnItemClickListener callbackDiary) {
        this.diaryGroup = diaryGroup;
        this.diaryData = diaryData;
        this.callbackDiaryGroup = callbackDiaryGroup;
        this.callbackDiary = callbackDiary;
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
        Type type = getTypOfPosition(i);
        if (type == Type.GROUP_DIARY) {
            setGroupDiaryRow(diaryViewHolder);

        } else if (type == Type.DIARY) {
            int diaryIndex = getDiaryIndex(i);
            setDiaryRow(diaryViewHolder, diaryIndex);
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (diaryGroup != null) {
            count++;
        }
        if (diaryData != null) {
            count += diaryData.size();
        }
        return count;
    }

    private Type getTypOfPosition(int position) {
        int currentPosition = 0;
        if (diaryGroup != null) {
            if (position == currentPosition) {
                return Type.GROUP_DIARY;
            }
            currentPosition++;
        }
        return Type.DIARY;
    }

    private int getDiaryIndex(int position) {
        int offset = 0;
        if (diaryGroup != null) {
            offset--;
        }
        return position + offset;
    }

    private void setDiaryRow(DiaryViewHolder diaryViewHolder, int index) {
        CardView cardView = diaryViewHolder.itemView.findViewById(R.id.card_view_row_diary);
        TextView calendarText1 = diaryViewHolder.itemView.findViewById(R.id.text_view_row_diary_calendar_top);
        TextView calendarText2 = diaryViewHolder.itemView.findViewById(R.id.text_view_row_diary_calendar_mid);
        TextView calendarText3 = diaryViewHolder.itemView.findViewById(R.id.text_view_row_diary_calendar_bottom);

        TextView titleText = diaryViewHolder.itemView.findViewById(R.id.text_view_row_diary_title);
        TextView contentText = diaryViewHolder.itemView.findViewById(R.id.text_view_row_diary_content);

        Diary diary = diaryData.get(index);
        String diaryDate = diary.getDiaryDate();

        int day = DateUtil.getDay(diaryDate);
        if (day == 0) {
            calendarText1.setTextColor(Color.RED);
        } else if (day == 6) {
            calendarText1.setTextColor(Color.BLUE);
        }
        calendarText1.setText(DateUtil.dayOfWeek(diaryDate));
        calendarText2.setText(diaryDate.substring(6, 8));
        calendarText3.setText(diaryDate.substring(0, 4) + "." + diaryDate.substring(4, 6));

        titleText.setText(CommonUtil.defaultIfEmpty(diary.getTitle(), Constants.DEFAULT_TITLE));
        contentText.setText(diary.getContent());
    }

    private void setGroupDiaryRow(DiaryViewHolder diaryViewHolder) {
        CardView cardView = diaryViewHolder.itemView.findViewById(R.id.card_view_row_diary);
        TextView calendarText1 = diaryViewHolder.itemView.findViewById(R.id.text_view_row_diary_calendar_top);
        TextView calendarText2 = diaryViewHolder.itemView.findViewById(R.id.text_view_row_diary_calendar_mid);
        TextView calendarText3 = diaryViewHolder.itemView.findViewById(R.id.text_view_row_diary_calendar_bottom);

        TextView titleText = diaryViewHolder.itemView.findViewById(R.id.text_view_row_diary_title);
        TextView contentText = diaryViewHolder.itemView.findViewById(R.id.text_view_row_diary_content);

        ImageView imageView = diaryViewHolder.itemView.findViewById(R.id.image_view_row_diary_calendar_image);

        //boolean isHost = AuthorUtil.getAuthor().getAuthorId().equals(diaryGroup.getHostAuthorId());
        String start = DateUtil.getDateStringSkipYear(diaryGroup.getStartTime());
        String end = DateUtil.getDateStringSkipYear(diaryGroup.getEndTime() - TimeUnit.MINUTES.toMillis(1));
        String period = start + " ~ " + end;

        cardView.setCardBackgroundColor(0xCCFFFFCC);
        imageView.setPadding(15, 15, 15, 15);

        long currentTime = System.currentTimeMillis();
        if (currentTime > diaryGroup.getStartTime()) {
            imageView.setImageResource(R.drawable.ic_people_black_24dp);

            String yesterday_yyyyMMdd = DateUtil.getyyyyMMdd(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1));
            //calendarText1.setText(DateUtil.dayOfWeek(yesterday_yyyyMMdd));
            //calendarText2.setText(yesterday_yyyyMMdd.substring(6, 8));
            //calendarText3.setText(yesterday_yyyyMMdd.substring(0, 4) + "." + yesterday_yyyyMMdd.substring(4, 6));

        } else {
            imageView.setImageResource(R.drawable.ic_person_add_black_24dp);

            String title = "그룹 일기 준비 중 ...";
            titleText.setText(title);

            long timeLeft = diaryGroup.getStartTime() - currentTime;
            int diaryGroupAuthorCount = 1;
            String content = "현재 참여자 수: " + diaryGroup.getCurrentAuthorCount() + "명"
                    + "\n시작까지 남은 시간: " + DateUtil.getTimeString(timeLeft);
            contentText.setText(content);
        }
    }
}
