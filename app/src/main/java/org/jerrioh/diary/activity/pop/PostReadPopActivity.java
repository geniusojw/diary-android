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
            chocolates.setText(getResources().getString(R.string.post_for_you));
            mainLayout.setBackgroundColor(0x55f5f5dc);
        } else {
            chocolates.setText(getResources().getString(R.string.post_for_all) + "\n(" + getResources().getString(R.string.post_worth, post.getChocolates()) + ")");
            mainLayout.setBackgroundColor(0x55DDEFEF);
        }

        textView = findViewById(R.id.edit_text_square_post_pop_content);
        textView.setText(post.getContent());

        TextView authorNick = findViewById(R.id.text_view_post_pop_author_nick);
        TextView writtenTime = findViewById(R.id.text_view_post_pop_written_time);
        authorNick.setVisibility(View.VISIBLE);
        authorNick.setText(getResources().getString(R.string.writer) + ": " + post.getAuthorNickname());
        writtenTime.setVisibility(View.VISIBLE);

        long deleteTime = post.getWrittenTime() + Property.Config.AUTO_DELETE_MILLIS;
        String writtenTimeText = "(" + getResources().getString(R.string.post_last_modified) + ": " + DateUtil.getDateStringFull(post.getWrittenTime()) + ")";
        if (isPrivate && System.currentTimeMillis() > deleteTime - Property.Config.AUTO_DELETE_CAUTION_MILLIS) {
            writtenTimeText += "\n" + getResources().getString(R.string.delete_soon);
        }
        writtenTime.setText(writtenTimeText);

        LinearLayout okLayout = findViewById(R.id.linear_layout_square_post_ok);
        okLayout.setOnClickListener(v -> {
            finish();
        });

        setUpMoreOptionsPost(textView, post, false);
    }

}
