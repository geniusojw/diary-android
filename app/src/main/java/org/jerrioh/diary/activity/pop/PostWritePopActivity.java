package org.jerrioh.diary.activity.pop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.PostApis;
import org.jerrioh.diary.model.PostIt;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.util.PropertyUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class PostWritePopActivity extends CustomPopActivity {

    private static final String TAG = "PostReadPopActivity";

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square_post_pop);

        super.setWindowAttribute(.95f, .9f);

        editText = findViewById(R.id.edit_text_square_post_pop_content);
        editText.setFocusableInTouchMode(true);
        editText.setText("");

        LinearLayout okLayout = findViewById(R.id.linear_layout_square_post_ok);
        okLayout.setOnClickListener(v -> {
            PostApis postApis = new PostApis(this);
            postApis.post(editText.getText().toString(), new ApiCallback() {
                @Override
                protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                    if (httpStatus == 200) {
                        Toast.makeText(PostWritePopActivity.this, "완료되었다", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
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
        editText.setTextSize(fontSize);
    }

}
