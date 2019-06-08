package org.jerrioh.diary.activity.main;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jerrioh.diary.R;
import org.jerrioh.diary.model.Author;
import org.jerrioh.diary.model.Letter;
import org.jerrioh.diary.model.db.LetterDao;
import org.jerrioh.diary.util.AuthorUtil;
import org.jerrioh.diary.util.DateUtil;

public class LetterReadActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_letter);

        Author author = AuthorUtil.getAuthor(this);

        Intent intent = getIntent();
        Letter letter = (Letter) intent.getSerializableExtra("letter");

        TextView letterTimeView = findViewById(R.id.text_view_detail_letter_time);
        letterTimeView.setVisibility(View.VISIBLE);
        letterTimeView.setText("Created on " + DateUtil.getDateStringFull(letter.getWrittenTime()));

        TextView toAuthorView = findViewById(R.id.text_view_detail_letter_to_author);
        toAuthorView.setText("TO: " + letter.getToAuthorNickname());

        EditText contentEditView = findViewById(R.id.edit_text_detail_letter_content);
        contentEditView.setText(letter.getContent());

        TextView fromAuthorView = findViewById(R.id.text_view_detail_letter_from_author);
        fromAuthorView.setText("FROM: " + letter.getFromAuthorNickname());

        if (letter.getStatus() == Letter.LetterStatus.UNREAD) {
            LetterDao letterDao = new LetterDao(this);
            letterDao.updateLetterStatus(letter.getLetterId(), Letter.LetterStatus.READ);
        }

        // 답장하기 버튼
        FloatingActionButton replyButton = findViewById(R.id.floating_action_button_detail_letter_send);
        if (author.getAuthorId().equals(letter.getFromAuthorId())) {
            replyButton.setEnabled(false);
            replyButton.setClickable(false);
            ((View) replyButton).setVisibility(View.GONE);

        } else if (letter.getStatus() == Letter.LetterStatus.REPLIED) {
            replyButton.setEnabled(true);
            replyButton.setClickable(true);
            //replyButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.beige)));
            replyButton.setOnClickListener(v -> {
                Toast.makeText(this, "이미 회신된 편지입니다.", Toast.LENGTH_SHORT).show();
            });

        } else {
            replyButton.setEnabled(true);
            replyButton.setClickable(true);
            replyButton.setOnClickListener(v -> {
                Intent replyIntent = new Intent(this, LetterWriteActivity.class);
                replyIntent.putExtra("letterId", letter.getLetterId());
                startActivity(replyIntent);
                finish();
            });
        }

        // 뒤로가기 버튼
        View backButton = findViewById(R.id.floating_action_button_detail_letter_back);
        backButton.setEnabled(true);
        backButton.setClickable(true);
        backButton.setOnClickListener(v -> {
            finish();
        });
    }
}
