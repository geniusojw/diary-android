package org.jerrioh.diary.activity.pop;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.jerrioh.diary.R;
import org.jerrioh.diary.model.Diary;
import org.jerrioh.diary.model.db.DiaryDao;
import org.jerrioh.diary.util.DateUtil;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DiaryBannerPopActivity extends AbstractDiaryPopActivity implements OnChartValueSelectedListener {

    private static final String TAG = "DiaryBannerPopActivity";

    private HorizontalBarChart chart;
    private String yyyy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_fragment_pop);

        yyyy = getIntent().getStringExtra("yyyy");

        super.setWindowAttribute(.95f, .9f, 0, -20);

        DiaryDao diaryDao = new DiaryDao(this);
        Diary firstDiary = diaryDao.getOneDiary(false);
        if (firstDiary == null) {
            finish();
        }
        int firstDiary_yyyy = Integer.parseInt(firstDiary.getDiaryDate().substring(0, 4));
        int today_yyyy = Integer.parseInt(DateUtil.getyyyyMMdd().substring(0, 4));

        ImageView yearAdjustLeft = findViewById(R.id.image_view_year_adjust_left);
        yearAdjustLeft.setOnClickListener(v -> {
            int current_yyyy = Integer.parseInt(yyyy);
            if (firstDiary_yyyy >= current_yyyy) {
                Toast.makeText(this, "처음 페이지입니다.", Toast.LENGTH_SHORT).show();
            } else {
                yyyy = String.valueOf(current_yyyy - 1);
                this.setChart();
            }
        });

        ImageView yearAdjustRight = findViewById(R.id.image_view_year_adjust_right);
        yearAdjustRight.setOnClickListener(v -> {
            int current_yyyy = Integer.parseInt(yyyy);
            if (today_yyyy <= current_yyyy) {
                Toast.makeText(this, "마지막 페이지입니다.", Toast.LENGTH_SHORT).show();
            } else {
                yyyy = String.valueOf(current_yyyy + 1);
                this.setChart();
            }
        });

        chart = findViewById(R.id.horizontal_bar_chart);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setScaleEnabled(false);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(Typeface.DEFAULT);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value < 1) {
                    return "There is no diary.";
                }
                return new DateFormatSymbols(Locale.getDefault()).getMonths()[(int) value - 1];
            }
        });
        xAxis.setGranularity(1);
        xAxis.setLabelCount(12);

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setTypeface(Typeface.DEFAULT);
        yAxisLeft.setDrawAxisLine(false);
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        yAxisLeft.setAxisMaximum(30f);
        yAxisLeft.setGranularity(10f);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        chart.getLegend().setEnabled(false);

        this.setChart();

        chart.setOnChartValueSelectedListener(this);

        int totalCount = diaryDao.getAllDiaries().size();
        TextView descriptionView = findViewById(R.id.text_view_diary_fragment_pop_description);
        descriptionView.setText(getResources().getString(R.string.diary_written_total, totalCount));
    }

    private void setChart() {
        TextView textView = findViewById(R.id.text_view_diary_fragment_pop_title);
        textView.setText(getResources().getString(R.string.diary_diaries_of_year, yyyy));

        chart.setData(this.getData());
        chart.setFitBars(true);
        chart.animateY(700);
        chart.invalidate();
    }

    private BarData getData() {
        float barWidth = .9f;

        int firstMonth = -1;
        int lastMonth = -1;

        DiaryDao diaryDao = new DiaryDao(this);
        List<Diary> allDiaries = diaryDao.getAllDiaries();
        Map<Integer, Integer> monthlyDiaryCount = new HashMap<>();

        List<BarEntry> values = new ArrayList<>();
        for (Diary diary : allDiaries) {
            if (diary.getDiaryDate().startsWith(yyyy)) {
                int month = Integer.parseInt(diary.getDiaryDate().substring(5, 6));
                if (firstMonth == -1) {
                    firstMonth = month;
                }
                lastMonth = month;

                Integer count = monthlyDiaryCount.get(month);
                monthlyDiaryCount.put(month, count == null ? 1 : count + 1);
            }
        }

        if (firstMonth != -1) {
            for (int i = firstMonth; i <= lastMonth; i++) {
                Integer count = monthlyDiaryCount.get(i);
                values.add(new BarEntry(i, count == null ? 0 : count, getResources().getDrawable(R.drawable.ic_check_black_24dp)));
            }
        }
        Log.d(TAG, "values size = " + values.size());

        BarDataSet set1 = new BarDataSet(values, yyyy);
        set1.setDrawIcons(false);
        set1.setColor(getResources().getColor(R.color.colorAccent));

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(Typeface.DEFAULT);
        data.setBarWidth(barWidth);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        return data;
    }
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null) {
            return;
        }
        int month = (int) e.getX();
        if (month < 1 || month > 12) {
            return;
        }
        String yyyyMM = yyyy + String.format("%02d", month);
        setResult(Integer.parseInt(yyyyMM));
        finish();
    }

    @Override
    public void onNothingSelected() {

    }
}
