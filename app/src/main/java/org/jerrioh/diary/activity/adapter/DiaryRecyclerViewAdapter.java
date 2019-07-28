package org.jerrioh.diary.activity.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
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
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.CommonUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DiaryRecyclerViewAdapter extends RecyclerView.Adapter<DiaryRecyclerViewAdapter.DiaryViewHolder> {

    private enum Type {
        AD, GROUP_DIARY, DIARY
    }

    private final Context context;
    private DiaryGroup diaryGroup;
    private long currentTime;
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

    public DiaryRecyclerViewAdapter(Context context, DiaryGroup diaryGroup, List<Diary> diaryData, OnItemClickListener callbackDiaryGroup, OnItemClickListener callbackDiary) {
        this.context = context;
        this.diaryGroup = diaryGroup;
        this.currentTime = System.currentTimeMillis();
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

        ImageView imageView = diaryViewHolder.itemView.findViewById(R.id.image_view_row_diary_calendar_image);
        imageView.setPadding(2, 2, 2, 2);
        imageView.setImageResource(R.drawable.ic_web_asset_black_24dp);

        Diary diary = diaryData.get(index);
        String diaryDate = diary.getDiaryDate();

        calendarText1.setText(DateUtil.dayOfWeek(diaryDate));
        calendarText2.setText(diaryDate.substring(6, 8));
        calendarText3.setText(diaryDate.substring(0, 4) + "." + diaryDate.substring(4, 6));

        final String today_yyyyMMdd = DateUtil.getyyyyMMdd();
        if (today_yyyyMMdd.equals(diaryDate)) {
            long timeLeft = DateUtil.getTimeLeft();

            SpannableString content = new SpannableString(context.getResources().getString(R.string.diary_today_diary));
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            //content.setSpan(new StyleSpan(Typeface.BOLD), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleText.setText(content);

            if (diaryGroup != null && currentTime > diaryGroup.getStartTime()) {
                contentText.setText("* " + context.getResources().getString(R.string.diary_share, diaryGroup.getCurrentAuthorCount() - 1));
                cardView.setCardBackgroundColor(0xCCFFDDCC);
            } else {
                contentText.setText(context.getResources().getString(R.string.diary_today_remain_time_short, DateUtil.getTimeString(timeLeft)));
                cardView.setCardBackgroundColor(0xCCDDEEFF);
            }

        } else {
            titleText.setText(CommonUtil.defaultIfEmpty(diary.getTitle(), Constants.DEFAULT_TITLE));
            contentText.setText(diary.getContent());
            cardView.setCardBackgroundColor(0xCCFFFFFF);
        }

        int day = DateUtil.getDay(diaryDate);
        if (day == 0) {
            calendarText1.setTextColor(0xCCFF0000);
        } else if (day == 6) {
            calendarText1.setTextColor(0xCC0000FF);
        } else {
            calendarText1.setTextColor(0xFF808080);
        }

        // DEBUG
        if (diary.getAccountDiaryStatus() == Diary.DiaryStatus.UNSAVED_CONFLICT) {
            cardView.setCardBackgroundColor(0xCCFF000F);
        }
    }

    private void setGroupDiaryRow(DiaryViewHolder diaryViewHolder) {
        CardView cardView = diaryViewHolder.itemView.findViewById(R.id.card_view_row_diary);
        TextView calendarText1 = diaryViewHolder.itemView.findViewById(R.id.text_view_row_diary_calendar_top);
        TextView calendarText2 = diaryViewHolder.itemView.findViewById(R.id.text_view_row_diary_calendar_mid);
        TextView calendarText3 = diaryViewHolder.itemView.findViewById(R.id.text_view_row_diary_calendar_bottom);
        calendarText1.setText(null);
        calendarText2.setText(null);
        calendarText3.setText(null);

        TextView titleText = diaryViewHolder.itemView.findViewById(R.id.text_view_row_diary_title);
        TextView contentText = diaryViewHolder.itemView.findViewById(R.id.text_view_row_diary_content);

        ImageView imageView = diaryViewHolder.itemView.findViewById(R.id.image_view_row_diary_calendar_image);
        imageView.setPadding(17, 17, 17, 17);

        //boolean isHost = AuthorUtil.getAuthor().getAuthorId().equals(diaryGroup.getHostAuthorId());
        String title;
        String content;

        if (currentTime > diaryGroup.getStartTime()) { // 시작됨
            cardView.setCardBackgroundColor(0xCCFFDDCC);
            if (diaryGroup.getCurrentAuthorCount() <= 1) {
                imageView.setImageResource(R.drawable.ic_person_black_24dp);
            } else {
                imageView.setImageResource(R.drawable.ic_people_black_24dp);
            }

            title = CommonUtil.defaultIfEmpty(diaryGroup.getDiaryGroupName(), context.getResources().getString(R.string.group_started_no_group_name));

            String start = DateUtil.getDateStringSkipYear(diaryGroup.getStartTime());
            String end = DateUtil.getDateStringSkipYear(diaryGroup.getEndTime() - TimeUnit.MINUTES.toMillis(1));

            String tip;
            if (diaryGroup.getCurrentAuthorCount() >= 2) {
                tip = CommonUtil.randomString(
                        context.getResources().getString(R.string.group_started_tip1),
                        context.getResources().getString(R.string.group_started_tip2),
                        context.getResources().getString(R.string.group_started_tip3, (diaryGroup.getCurrentAuthorCount() - 1)));
            } else {
                tip = CommonUtil.randomString(
                        context.getResources().getString(R.string.group_solo_tip1),
                        context.getResources().getString(R.string.group_solo_tip2));
            }
            String period = start + " ~ " + end;
            content = tip + "\n" + period;

        } else { // 준비중
            cardView.setCardBackgroundColor(0xCCFFFFCC);
            imageView.setImageResource(R.drawable.ic_person_add_black_24dp);

            title = CommonUtil.randomString(
                    context.getResources().getString(R.string.group_prepare_tip1),
                    context.getResources().getString(R.string.group_prepare_tip2),
                    context.getResources().getString(R.string.group_prepare_tip3));

            long timeLeft = diaryGroup.getStartTime() - currentTime;
            content = context.getResources().getString(R.string.group_prepare_content, diaryGroup.getCurrentAuthorCount(), DateUtil.getTimeString(timeLeft));
        }

//        content = "debug max: " + diaryGroup.getCurrentAuthorCount() + " / " + diaryGroup.getMaxAuthorCount()
//                + "\ndebug period: " + period;

        titleText.setText(title);
        contentText.setText(content);
    }
}
