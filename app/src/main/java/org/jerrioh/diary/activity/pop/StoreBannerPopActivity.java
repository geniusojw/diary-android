package org.jerrioh.diary.activity.pop;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class StoreBannerPopActivity extends AbstractDiaryPopActivity {

    private static final String TAG = "StoreBannerPopActivity";

    public static int currentChocolates = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_fragment_pop);
        super.setWindowAttribute(.95f, .9f, 0, -20);

        TextView chocolatesHave = findViewById(R.id.text_view_store_fragment_pop_title_chocolates);

        String desc = "";
        if (currentChocolates != -1) {
            desc = getResources().getString(R.string.store_fragment_pop_title_time, currentChocolates);
        }
        chocolatesHave.setText(desc);
    }
}
