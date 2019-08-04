package org.jerrioh.diary.activity.pop;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.util.PropertyUtil;

public class FontSizePopActivity extends AbstractDiaryPopActivity {

    private static final String TAG = "FontSizePopActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String sampleString = "\"YOU ARE SO CUTE.\"";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_size_adjust_pop);

        super.setWindowAttribute(.95f, .45f);

        TextView sampleText = findViewById(R.id.text_view_font_size_sample_text);
        TextView sizeText = findViewById(R.id.text_view_font_size_size_value);
        SeekBar seekBar = findViewById(R.id.seek_bar_font_size_adjust);

        LinearLayout okLayout = findViewById(R.id.linear_layout_font_size_ok);
        TextView okTextView = findViewById(R.id.text_view_font_size_ok);

        String fontSizeProgress = PropertyUtil.getProperty(Property.Key.FONT_SIZE, this);
        sizeText.setText(fontSizeProgress);

        int fontSize = this.convertRealFontSize(Integer.parseInt(fontSizeProgress));
        sampleText.setTextSize(fontSize);

        seekBar.setProgress(Integer.parseInt(fontSizeProgress));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sizeText.setText(String.valueOf(progress));

                int fontSize = FontSizePopActivity.this.convertRealFontSize(progress);
                sampleText.setTextSize(fontSize);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                okTextView.setTextColor(getResources().getColor(R.color.colorAccent));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int fontSizeProgress = Integer.parseInt(sizeText.getText().toString());
                int fontSize = FontSizePopActivity.this.convertRealFontSize(fontSizeProgress);
                sampleText.setTextSize(fontSize);
            }
        });

        okTextView.setOnClickListener(v -> {
            PropertyUtil.setProperty(Property.Key.FONT_SIZE, sizeText.getText().toString(), FontSizePopActivity.this);
            finish();
        });
    }

    private int convertRealFontSize(int fontSizeProgress) {
        return (fontSizeProgress * 2) + Property.Config.FONT_SIZE_OFFSET;
    }
}
