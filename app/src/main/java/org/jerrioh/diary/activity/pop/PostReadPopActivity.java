package org.jerrioh.diary.activity.pop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jerrioh.diary.R;
import org.jerrioh.diary.activity.main.AbstractDetailActivity;
import org.jerrioh.diary.model.Post;
import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.util.DateUtil;
import org.jerrioh.diary.util.PropertyUtil;

public class PostReadPopActivity extends AbstractDetailActivity {

    private static final String TAG = "PostReadPopActivity";

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square_post_pop);

        super.setWindowAttribute(.95f, .9f);

        Intent intent = getIntent();
        Post post = (Post) intent.getSerializableExtra("post");

        RelativeLayout mainLayout = findViewById(R.id.relative_layout_post_pop_main);
        TextView chocolates = findViewById(R.id.text_view_square_post_pop_chocolates);

        boolean isPrivate = post.getChocolates() == -1;
        if (isPrivate) {
            chocolates.setText("당신만 볼 수 있는 포스트입니다.");
            mainLayout.setBackgroundColor(0x55f5f5dc);
        } else {
            chocolates.setText("THIS POST IS WORTH " + post.getChocolates() + " TIME MONEY");
            mainLayout.setBackgroundColor(0x55DDEFEF);
        }

        textView = findViewById(R.id.edit_text_square_post_pop_content);
        textView.setText(post.getContent());

        TextView authorNick = findViewById(R.id.text_view_post_pop_author_nick);
        TextView writtenTime = findViewById(R.id.text_view_post_pop_written_time);
        authorNick.setVisibility(View.VISIBLE);
        authorNick.setText("작성자: " + post.getAuthorNickname());
        writtenTime.setVisibility(View.VISIBLE);
        writtenTime.setText("(" + DateUtil.getDateStringFull(post.getWrittenTime()) + ")");

        LinearLayout okLayout = findViewById(R.id.linear_layout_square_post_ok);
        okLayout.setOnClickListener(v -> {
            finish();
        });

        setUpMoreOptionsPost(textView, post, false);
    }

}
