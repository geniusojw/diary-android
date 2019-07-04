package org.jerrioh.diary.activity.pop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.model.PostIt;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.util.PropertyUtil;

public class PostReadPopActivity extends CustomPopActivity {

    private static final String TAG = "PostReadPopActivity";

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square_post_pop);

        super.setWindowAttribute(.95f, .9f);

        Intent intent = getIntent();
        PostIt postIt = (PostIt) intent.getSerializableExtra("post");

        TextView chocolates = findViewById(R.id.text_view_square_post_pop_chocolates);
        chocolates.setText(postIt.getChocolates() + " chocolates");

        textView = findViewById(R.id.edit_text_square_post_pop_content);
        textView.setText(postIt.getContent() + "\n\n - " + postIt.getAuthorNickname());

        LinearLayout okLayout = findViewById(R.id.linear_layout_square_post_ok);
        okLayout.setOnClickListener(v -> {
            finish();
        });

        // 글자 크기 조절을 위한 세팅
        TextView adjustText = findViewById(R.id.text_view_detail_square_post_pop_font_size_adjust);
        adjustText.setOnClickListener(v -> {
            startActivity(new Intent(this, FontSizePopActivity.class));
        });
        this.setContentFontSize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentFontSize();
    }

    private void setContentFontSize() {
        int fontSizeProgress = Integer.parseInt(PropertyUtil.getProperty(Property.Key.FONT_SIZE, this));
        int fontSize = (fontSizeProgress * 2) + Property.Config.FONT_SIZE_OFFSET;
        textView.setTextSize(fontSize);
    }

}
