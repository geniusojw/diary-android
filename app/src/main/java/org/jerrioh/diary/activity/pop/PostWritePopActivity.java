package org.jerrioh.diary.activity.pop;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.main.AbstractDetailActivity;
import org.jerrioh.diary.api.ApiCallback;
import org.jerrioh.diary.api.author.PostApis;
import org.jerrioh.diary.model.Post;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.model.db.PostDao;
import org.jerrioh.diary.util.PropertyUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class PostWritePopActivity extends AbstractDetailActivity {

    private static final String TAG = "PostReadPopActivity";

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square_post_pop);

        super.setWindowAttribute(.95f, .9f);

        Intent intent = getIntent();
        Post post = (Post) intent.getSerializableExtra("post");

        boolean isPrivate = post.getChocolates() == -1;
        RelativeLayout mainLayout = findViewById(R.id.relative_layout_post_pop_main);
        TextView chocolates = findViewById(R.id.text_view_square_post_pop_chocolates);
        if (isPrivate) {
            chocolates.setText(getResources().getString(R.string.post_for_you));
            mainLayout.setBackgroundColor(0x55f5f5dc);
        } else {
            chocolates.setText(getResources().getString(R.string.post_for_all) + "\n(" + getResources().getString(R.string.post_worth, post.getChocolates()) + ")");
            mainLayout.setBackgroundColor(0x55DDEFEF);
        }

        editText = findViewById(R.id.edit_text_square_post_pop_content);
        editText.setFocusableInTouchMode(true);
        editText.setText(post.getContent());

        LinearLayout okLayout = findViewById(R.id.linear_layout_square_post_ok);
        okLayout.setOnClickListener(v -> {
            if (TextUtils.isEmpty(editText.getText())) {
                Toast.makeText(this, "내용을 작성하세요.", Toast.LENGTH_SHORT).show();
            } else {
                if (isPrivate) {
                    PostDao postDao = new PostDao(this);
                    post.setContent(editText.getText().toString());
                    post.setWrittenTime(System.currentTimeMillis());

                    if (postDao.getPost(post.getPostId()) == null) {
                        postDao.insertPost(post);
                    } else {
                        postDao.updatePost(post);
                    }
                    Toast.makeText(PostWritePopActivity.this, "포스트가 작성되었습니다.", Toast.LENGTH_SHORT).show();

                } else {
                    PostApis postApis = new PostApis(this);
                    postApis.post(editText.getText().toString(), new ApiCallback() {
                        @Override
                        protected void execute(int httpStatus, JSONObject jsonObject) throws JSONException {
                            if (httpStatus == 200) {
                                Toast.makeText(PostWritePopActivity.this, "포스트가 작성되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                finish();
            }
        });

        super.setUpMoreOptionsPost(editText, post, true);
    }

}
